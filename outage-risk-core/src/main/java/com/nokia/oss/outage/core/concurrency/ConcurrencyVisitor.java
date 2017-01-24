package com.nokia.oss.outage.core.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.nokia.oss.outage.core.bean.RiskTypes;
import com.nokia.oss.outage.core.matcher.MethodCallMatcher;
import com.nokia.oss.outage.core.matcher.MethodCallOnVariableMatcher;
import com.nokia.oss.outage.core.matcher.NodeMatcher;
import com.nokia.oss.outage.core.matcher.NodeTypeMatcher;
import com.nokia.oss.outage.core.util.ClassUtils;
import com.nokia.oss.outage.core.util.NodeTreeUtils;
import com.nokia.oss.outage.core.util.SourceCodeVisitorSupport;

/**
 * Created by harchen on 2015/12/7.
 */
public class ConcurrencyVisitor
    extends SourceCodeVisitorSupport
{
    public static final String GET = "get";
    private RiskTypes riskTypes;
    private List<String> concurrentHashMapFields = new ArrayList<String>();
    private List<String> lockFields = new ArrayList<String>();

    public ConcurrencyVisitor( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }

    @Override
    public void visit(MethodCallExpr expression, Object arg )
    {
        checkConcurrentHashMap( expression );
        checkLockRelease( expression );
        super.visit( expression, arg );
    }

    @Override
    public void visit( MethodDeclaration method, Object arg )
    {
        if( ModifierSet.isSynchronized( method.getModifiers() ) )
        {
            checkWaitAndNotify( method.getBody() );
        }
        super.visit( method, arg );
    }

    private void checkWaitAndNotify( BlockStmt body )
    {

        NodeMatcher matcher = new MethodCallMatcher( "wait", "notify", "notifyAll" );
        List<MethodCallExpr> methodCalls = NodeTreeUtils.findAllSubNode( body, matcher );
        if( !methodCalls.isEmpty() )
        {
            reportRisk( riskTypes.get( RiskTypes.OUTAGE_CONCURRENCY_WAIT_AND_NOTIFY), body );
        }
    }

    @Override
    public void visit( SynchronizedStmt statement, Object arg )
    {
        checkSynchronizedOnLocalVariable( statement );
        checkNestedSynchronization( statement );
        checkWaitAndNotify( statement.getBlock() );
        super.visit( statement, arg );
    }

    private void checkNestedSynchronization( SynchronizedStmt statement )
    {
        BlockStmt blockStmt = statement.getBlock();
        NodeMatcher matcher = new NodeTypeMatcher( SynchronizedStmt.class );
        List<SynchronizedStmt> synchronizedStmts = NodeTreeUtils.findAllSubNode( blockStmt, matcher );
        if( !synchronizedStmts.isEmpty() )
        {
            reportRisk( riskTypes.get( RiskTypes.OUTAGE_CONCURRENCY_NESTED_SYNCHRONIZATION), statement );
        }
    }

    private void checkSynchronizedOnLocalVariable( SynchronizedStmt statement )
    {
        Expression expression = statement.getExpr();
        if( expression instanceof ObjectCreationExpr)
        {
            reportRisk( riskTypes.get( RiskTypes.OUTAGE_CONCURRENCY_SYNCHRONIZE_ON_LOCAL_VARIABLE), statement );
        }
        else if( expression instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr)expression;
            String variableName = nameExpr.getName();
            MethodDeclaration method = NodeTreeUtils.findFirstEnclosingNodeOfType( statement, MethodDeclaration.class );
            NodeTypeMatcher matcher = new NodeTypeMatcher( VariableDeclarator.class );
            List<VariableDeclarator> localVariables = NodeTreeUtils.findAllSubNode( method, matcher );
            for( VariableDeclarator variable : localVariables )
            {
                if( variable.getId().getName().equals( variableName ) )
                {
                    reportRisk( riskTypes.get( RiskTypes.OUTAGE_CONCURRENCY_SYNCHRONIZE_ON_LOCAL_VARIABLE), statement );
                }
            }
        }
    }

    private void checkLockRelease( MethodCallExpr expression )
    {
        for( String field : lockFields )
        {
            if( isMethodCallOnField( expression, field, "lock", "tryLock", "lockInterruptibly" ) )
            {
                if( !isUnlockedInFinallyBlock( expression, field ) )
                {
                    reportRisk(
                        riskTypes.get( RiskTypes.OUTAGE_CONCURRENCY_LOCK_IS_NOT_UNLOCKED_IN_FINALLY_BLOCK), expression );
                }
            }
        }
    }

    private boolean isUnlockedInFinallyBlock( MethodCallExpr expression, String field )
    {
        boolean isUnlocked = false;
        MethodDeclaration method = NodeTreeUtils.findFirstEnclosingNodeOfType( expression, MethodDeclaration.class );
        List<TryStmt> tryStmtList = NodeTreeUtils.findAllSubNode( method, new NodeTypeMatcher( TryStmt.class ) );
        for( TryStmt tryStmt : tryStmtList )
        {
            List<Node> closeLockStatements =
                NodeTreeUtils.findAllSubNode( tryStmt.getFinallyBlock(), new MethodCallOnVariableMatcher(
                    field, "unlock" ) );
            if( !closeLockStatements.isEmpty() )
            {
                isUnlocked = true;
                break;
            }
        }
        return isUnlocked;
    }

    private void checkConcurrentHashMap( MethodCallExpr expression )
    {
        for( String field : concurrentHashMapFields )
        {
            if( isMethodCallOnField( expression, field, GET ) )
            {
                BinaryExpr binaryExpr = NodeTreeUtils.findFirstEnclosingNodeOfType( expression, BinaryExpr.class );
                if( binaryExpr != null )
                {
                    reportRisk(
                        riskTypes.get( RiskTypes.OUTAGE_CONCURRENCY_USE_NONE_ATOMIC_OPERATION_ON_THREAD_SAFE_COLLECTION),
                        expression );
                }
            }
        }
    }

    private boolean isMethodCallOnField( MethodCallExpr expression, String field, String... methods )
    {
        boolean result = false;
        for( String method : methods )
        {
            if( isMethodCallOnVariable( expression, field, method ) ||
                isMethodCallOnVariable( expression, "this." + field, method ) )
            {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean isMethodCallOnVariable( Node node, String variable, String method )
    {
        boolean result = false;
        if( node instanceof MethodCallExpr )
        {
            MethodCallExpr expr = (MethodCallExpr)node;
            Expression scope = expr.getScope();
            String name = expr.getName();
            if( scope != null && name != null )
            {
                result = scope.toString().equals( variable ) && name.equals( method );
            }
        }
        return result;
    }

    @Override
    public void visit( FieldDeclaration fieldDeclaration, Object arg )
    {
        Type fieldDeclarationType = fieldDeclaration.getType();
        if( fieldDeclarationType instanceof ReferenceType )
        {
            ReferenceType referenceType = (ReferenceType)fieldDeclarationType;
            if( referenceType.getType() instanceof ClassOrInterfaceType )
            {
                ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType)referenceType.getType();
                String typeName = classOrInterfaceType.getName();
                Class type = ClassUtils.getPossibleFullClassName( typeName, importList );
                if( ClassUtils.isSubTypeOfAny( type, ConcurrentHashMap.class ) )
                {
                    for( VariableDeclarator variable : fieldDeclaration.getVariables() )
                    {
                        concurrentHashMapFields.add( variable.getId().getName() );
                    }
                }
                if( ClassUtils.isSubTypeOfAny( type, Lock.class ) )
                {
                    for( VariableDeclarator variable : fieldDeclaration.getVariables() )
                    {
                        lockFields.add( variable.getId().getName() );
                    }
                }
            }
        }
        super.visit( fieldDeclaration, arg );
    }
}
