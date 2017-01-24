package com.nokia.oss.outage.core.matcher;

import com.github.javaparser.ast.Node;

/**
 * Created by harchen on 2015/10/12.
 */
public class NodeTypeMatcher
    implements NodeMatcher
{
    private Class<? extends Node> type;

    public NodeTypeMatcher( Class<? extends Node> type )
    {
        this.type = type;
    }

    @Override
    public boolean match( Node node )
    {
        if( node == null )
        {
            return false;
        }
        else
        {
            return type.isAssignableFrom( node.getClass() );
        }
    }
}
