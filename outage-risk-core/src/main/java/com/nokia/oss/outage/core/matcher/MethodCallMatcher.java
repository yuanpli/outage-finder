package com.nokia.oss.outage.core.matcher;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;

/**
 * Created by harchen on 2015/12/24.
 */
public class MethodCallMatcher
    implements NodeMatcher
{
    private String[] methodNames;

    public MethodCallMatcher( String... methodNames )
    {
        this.methodNames = methodNames;
    }

    @Override
    public boolean match( Node node )
    {
        boolean result = false;
        if( node instanceof MethodCallExpr )
        {
            MethodCallExpr expr = (MethodCallExpr)node;
            for( String methodName : methodNames )
            {
                if( methodName.equals( expr.getName() ) )
                {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
