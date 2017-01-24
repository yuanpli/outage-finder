package com.nokia.oss.outage.core.matcher;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

/**
 * Created by harchen on 2015/10/12.
 */
public class MethodCallOnVariableMatcher
    implements NodeMatcher
{
    private String variable;
    private String method;

    public MethodCallOnVariableMatcher(String variable, String method)
    {
        this.variable = variable;
        this.method = method;
    }

    @Override
    public boolean match( Node node )
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
}
