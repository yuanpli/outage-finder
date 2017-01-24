package com.nokia.oss.outage.core.util;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.nokia.oss.outage.core.matcher.NodeMatcher;

/**
 * Created by harchen on 2015/10/13.
 */
public class NodeTreeUtils
{
    public static <T> T findFirstSubNodeOfType( Node node, Class<T> type )
    {
        T result = null;
        List<Node> children = node.getChildrenNodes();
        if( children != null )
        {
            for( Node child : children )
            {
                if( type.isAssignableFrom( child.getClass() ) )
                {
                    result = (T)child;
                    break;
                }
                else
                {
                    result = findFirstSubNodeOfType( child, type );
                    if( result != null )
                    {
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static <T> T findFirstEnclosingNodeOfType( Node node, Class<T> type )
    {
        T result = null;
        Node current = node;
        while( (current = current.getParentNode()) != null )
        {
            if( type.isAssignableFrom( current.getClass() ) )
            {
                result = (T)current;
                break;
            }

        }
        return result;
    }

    public static List findAllSubNode( Node root, NodeMatcher condition )
    {
        List<Node> nodes = new ArrayList<Node>();
        if( condition.match( root ) )
        {
            nodes.add( root );
        }
        if( root != null )
        {
            List<Node> childrenNodes = root.getChildrenNodes();
            if( childrenNodes != null )
            {
                for( Node subNode : childrenNodes )
                {
                    nodes.addAll( findAllSubNode( subNode, condition ) );
                }
            }
        }
        return nodes;
    }

    public static <T extends Node> T lastOf( List<T> nodes )
    {
        Node lastNode = nodes.get( 0 );
        for( Node node : nodes )
        {
            if( node.getBeginLine() > lastNode.getBeginLine() )
            {
                lastNode = node;
            }
        }
        return (T)lastNode;
    }

    public static boolean isVariableArgumentOfMethodCall( MethodCallExpr methodCall, String variable )
    {
        boolean result = false;
        List<Expression> args = methodCall.getArgs();
        if( args != null )
        {
            for( Expression arg : args )
            {
                if( variable.equals( arg.toString() ) )
                {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
