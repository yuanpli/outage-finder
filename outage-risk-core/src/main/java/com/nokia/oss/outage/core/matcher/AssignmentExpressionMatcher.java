package com.nokia.oss.outage.core.matcher;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;

/**
 * Created by harchen on 2015/10/15.
 */
public class AssignmentExpressionMatcher
    implements NodeMatcher
{
    private String variable;

    public AssignmentExpressionMatcher( String variable )
    {
        this.variable = variable;
    }

    @Override
    public boolean match( Node node )
    {
        boolean result = false;
        if( node instanceof AssignExpr )
        {
            AssignExpr expr = (AssignExpr)node;
            Expression target = expr.getTarget();
            if( variable.equals( target.toString() ) )
            {
                result = true;
            }
        }
        return result;
    }
}
