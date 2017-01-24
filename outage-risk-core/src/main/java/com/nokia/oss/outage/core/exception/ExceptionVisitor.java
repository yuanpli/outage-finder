package com.nokia.oss.outage.core.exception;

import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.nokia.oss.outage.core.bean.RiskTypes;
import com.nokia.oss.outage.core.matcher.MethodCallOnVariableMatcher;
import com.nokia.oss.outage.core.matcher.NodeMatcher;
import com.nokia.oss.outage.core.matcher.NodeTypeMatcher;
import com.nokia.oss.outage.core.util.NodeTreeUtils;
import com.nokia.oss.outage.core.util.SourceCodeVisitorSupport;

/**
 * Created by harchen on 2015/12/7.
 */
public class ExceptionVisitor
    extends SourceCodeVisitorSupport
{
    private RiskTypes riskTypes;

    public ExceptionVisitor( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }

    @Override
    public void visit( CatchClause catchClause, Object arg )
    {
        String exceptionName = catchClause.getExcept().getId().getName();
        BlockStmt block = catchClause.getCatchBlock();
        if( isExceptionUsedInThrowStatement( block, exceptionName ) )
        {

        }
        else if( isExceptionUsedAsParameterInMethodCall( block, exceptionName ) )
        {

        }
        else if( isExceptionMethodCalled( block, exceptionName ) )
        {

        }
        else
        {
            reportRisk( riskTypes.get( RiskTypes.OUTAGE_EXCEPTION_IGNORE_EXCEPTION_IN_CATCH_STATEMENT ), catchClause );
        }
        super.visit( catchClause, arg );
    }

    private boolean isExceptionMethodCalled( BlockStmt blockStmt, String exceptionName )
    {
        NodeMatcher matcher = new MethodCallOnVariableMatcher( exceptionName, "printStackTrace" );
        List<MethodCallExpr> methodCalls = NodeTreeUtils.findAllSubNode( blockStmt, matcher );
        return methodCalls.size() > 0;
    }

    private boolean isExceptionUsedAsParameterInMethodCall( BlockStmt blockStmt, String exceptionName )
    {
        boolean result = false;
        NodeMatcher matcher = new NodeTypeMatcher( MethodCallExpr.class );
        List<MethodCallExpr> throwStatements = NodeTreeUtils.findAllSubNode( blockStmt, matcher );
        for( MethodCallExpr methodCallExpr : throwStatements )
        {
            List<Expression> args = methodCallExpr.getArgs();
            if( args != null )
            {
                for( Expression argument : args )
                {
                    if( argument instanceof NameExpr )
                    {
                        NameExpr nameExpr = (NameExpr)argument;
                        if( nameExpr.getName().equals( exceptionName ) )
                        {
                            result = true;
                            break;
                        }
                    }
                }
            }

        }
        return result;
    }

    private boolean isExceptionUsedInThrowStatement( BlockStmt blockStmt, String exceptionName )
    {
        boolean result = false;
        NodeMatcher matcher = new NodeTypeMatcher( ThrowStmt.class );
        List<ThrowStmt> throwStatements = NodeTreeUtils.findAllSubNode( blockStmt, matcher );
        for( ThrowStmt throwStatement : throwStatements )
        {
            Expression expression = throwStatement.getExpr();
            if( expression instanceof ObjectCreationExpr )
            {
                if( isUsedToCreateNewException( exceptionName, (ObjectCreationExpr)expression ) )
                {
                    result = true;
                    break;
                }
            }
            else if( expression instanceof NameExpr )
            {
                NameExpr nameExpr = (NameExpr)expression;
                if( nameExpr.getName().equals( exceptionName ) )
                {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private boolean isUsedToCreateNewException( String exceptionName, ObjectCreationExpr expression )
    {
        boolean result = false;
        List<Expression> args = expression.getArgs();
        if( args != null )
        {
            for( Expression argument : args )
            {
                if( argument instanceof NameExpr )
                {
                    NameExpr nameExpr = (NameExpr)argument;
                    if( nameExpr.getName().equals( exceptionName ) )
                    {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }
}
