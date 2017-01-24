package com.nokia.oss.outage.core.matcher;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.nokia.oss.outage.core.util.NodeTreeUtils;

/**
 * Created by harchen on 2015/10/13.
 */
public class MethodCallUsingVariableMatcher
    implements NodeMatcher
{
    private String methodName;
    private String variable;

    public MethodCallUsingVariableMatcher( String methodName, String variable )
    {
        this.methodName = methodName;
        this.variable = variable;
    }

    public MethodCallUsingVariableMatcher( String variable )
    {
        this.variable = variable;
    }

    @Override
    public boolean match( Node node )
    {
        boolean result = false;
        if( node instanceof MethodCallExpr)
        {
            MethodCallExpr methodCall = (MethodCallExpr)node;
            if( methodName == null )
            {
                result = NodeTreeUtils.isVariableArgumentOfMethodCall( methodCall, variable );
            }
            else
            {
                Expression scope = methodCall.getScope();
                if( scope != null )
                {
                    String call = scope.toString() + '.' + methodCall.getName();
                    if( call.equals( methodName ) )
                    {
                        result = NodeTreeUtils.isVariableArgumentOfMethodCall( methodCall, variable );
                    }
                }
            }
        }
        return result;
    }
}
