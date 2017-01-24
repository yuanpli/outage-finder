package com.nokia.oss.outage.core.thread;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.Type;
import com.nokia.oss.outage.core.bean.RiskTypes;
import com.nokia.oss.outage.core.matcher.MethodCallOnVariableMatcher;
import com.nokia.oss.outage.core.matcher.NodeMatcher;
import com.nokia.oss.outage.core.util.ClassUtils;
import com.nokia.oss.outage.core.util.NodeTreeUtils;
import com.nokia.oss.outage.core.util.SourceCodeVisitorSupport;

/**
 * Created by harchen on 2015/10/21.
 */

public class ThreadManagementVisitor
    extends SourceCodeVisitorSupport
{

    private RiskTypes riskTypes;

    public ThreadManagementVisitor( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }

    @Override
    public void visit(ObjectCreationExpr expression, Object arg )
    {
        Class type = getObjectType( expression.getType().getName() );
        checkBlockingQueue( expression, type );
        checkThread( expression, type );
        super.visit( expression, arg );
    }

    @Override
    public void visit( MethodCallExpr expression, Object arg )
    {
        checkBlockingQueueInThreadPool( expression );
        checkThreadPoolSize( expression );
        super.visit( expression, arg );
    }

    @Override
    public void visit( CatchClause expression, Object arg )
    {
        List<Type> types = expression.getExcept().getTypes();
        for( Type type : types )
        {
            if( "InterruptedException".equals( type.toString() ) )
            {
                checkInterruptionStatus( expression );
                checkLoopTermination( expression );
            }
        }
        super.visit( expression, arg );
    }

    private void checkLoopTermination( CatchClause expression )
    {
        WhileStmt whileStmt = NodeTreeUtils.findFirstEnclosingNodeOfType( expression, WhileStmt.class );
        if( whileStmt != null )
        {
            Expression condition = whileStmt.getCondition();
            if( condition instanceof BooleanLiteralExpr)
            {
                BlockStmt catchBlock = expression.getCatchBlock();
                BreakStmt breakStmt = NodeTreeUtils.findFirstSubNodeOfType( catchBlock, BreakStmt.class );
                ReturnStmt returnStmt = NodeTreeUtils.findFirstSubNodeOfType( catchBlock, ReturnStmt.class );
                ThrowStmt throwStmt = NodeTreeUtils.findFirstSubNodeOfType( catchBlock, ThrowStmt.class );
                if( breakStmt == null && returnStmt == null && throwStmt == null )
                {
                    reportRisk(
                        riskTypes.get( RiskTypes.OUTAGE_THREAD_INTERRUPTED_EXCEPTION_CAUGHT_WITHOUT_BREAK_THE_LOOP),
                        expression );
                }
            }
        }
        ForStmt forStmt = NodeTreeUtils.findFirstEnclosingNodeOfType( expression, ForStmt.class );
        if( forStmt != null )
        {
            BlockStmt catchBlock = expression.getCatchBlock();
            BreakStmt breakStmt = NodeTreeUtils.findFirstSubNodeOfType( catchBlock, BreakStmt.class );
            ReturnStmt returnStmt = NodeTreeUtils.findFirstSubNodeOfType( catchBlock, ReturnStmt.class );
            ThrowStmt throwStmt = NodeTreeUtils.findFirstSubNodeOfType( catchBlock, ThrowStmt.class );
            if( breakStmt == null && returnStmt == null && throwStmt == null )
            {
                reportRisk(
                    riskTypes.get( RiskTypes.OUTAGE_THREAD_INTERRUPTED_EXCEPTION_CAUGHT_WITHOUT_BREAK_THE_LOOP), expression );
            }
        }
    }

    private void checkInterruptionStatus( CatchClause expression )
    {
        BlockStmt blockStmt = expression.getCatchBlock();
        MethodCallOnVariableMatcher matcher = new MethodCallOnVariableMatcher( "Thread.currentThread()", "interrupt" );
        List<Statement> statements = NodeTreeUtils.findAllSubNode( blockStmt, matcher );
        if( statements == null || statements.isEmpty() )
        {
            reportRisk(
                riskTypes.get( RiskTypes.OUTAGE_THREAD_INTERRUPTED_EXCEPTION_CAUGHT_WITHOUT_RESET_INTERRUPTION_STATUS),
                expression );
        }
    }

    private Class getObjectType( String typeName )
    {
        return ClassUtils.getPossibleFullClassName( typeName, importList );
    }

    private void checkThreadPoolSize( MethodCallExpr expression )
    {
        NodeMatcher matcher = new MethodCallOnVariableMatcher( "Executors", "newCachedThreadPool" );
        if( matcher.match( expression ) )
        {
            reportRisk( riskTypes.get( RiskTypes.OUTAGE_THREAD_POOL_WITHOUT_SIZE_LIMITATION), expression );
        }
    }

    private void checkBlockingQueueInThreadPool( MethodCallExpr expression )
    {
        Expression scope = expression.getScope();
        if( scope instanceof NameExpr)
        {
            Class type = getObjectType( ((NameExpr)scope).getName() );
            if( ClassUtils.isSubTypeOfAny( type, Executors.class ) )
            {
                reportRisk( riskTypes.get( RiskTypes.OUTAGE_THREAD_POOL_WITHOUT_QUEUE_SIZE_LIMITATION), expression );
            }
        }
    }

    private void checkThread( ObjectCreationExpr objectCreationExpr, Class type )
    {
        if( ClassUtils.isSubTypeOfAny( type, Thread.class ) )
        {
            reportRisk( riskTypes.get( RiskTypes.OUTAGE_THREAD_NOT_MANAGED_BY_POOL), objectCreationExpr );
        }
    }

    private void checkBlockingQueue( ObjectCreationExpr objectCreationExpr, Class type )
    {
        List<Expression> args = objectCreationExpr.getArgs();
        if( ClassUtils.isSubTypeOfAny( type, LinkedBlockingQueue.class, LinkedBlockingDeque.class ) && args == null )
        {
            reportRisk( riskTypes.get( RiskTypes.OUTAGE_THREAD_BLOCKING_QUEUE_WITHOUT_SIZE_LIMITATION), objectCreationExpr );
        }
    }
}
