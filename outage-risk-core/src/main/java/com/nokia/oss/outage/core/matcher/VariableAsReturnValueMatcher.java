package com.nokia.oss.outage.core.matcher;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ReturnStmt;

/**
 * Created by harchen on 2015/10/13.
 */
public class VariableAsReturnValueMatcher
    implements NodeMatcher
{
    private String variable;

    public VariableAsReturnValueMatcher( String variable )
    {
        this.variable = variable;
    }

    @Override
    public boolean match( Node node )
    {
        boolean result = false;
        if( node instanceof ReturnStmt )
        {
            ReturnStmt returnStmt = (ReturnStmt)node;
            Expression expr = returnStmt.getExpr();
            if( expr != null )
            {
                result = expr.toString().equals( variable );
            }
        }
        return result;
    }
}
