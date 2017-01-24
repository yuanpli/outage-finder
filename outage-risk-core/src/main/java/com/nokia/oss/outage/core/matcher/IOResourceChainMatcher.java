package com.nokia.oss.outage.core.matcher;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.nokia.oss.outage.core.util.ClassUtils;

/**
 * Created by harchen on 2015/10/13.
 */
public class IOResourceChainMatcher
    implements NodeMatcher
{
    private static final Logger LOG = LoggerFactory.getLogger( IOResourceChainMatcher.class );
    private String variable;
    private List<ImportDeclaration> importList;

    public IOResourceChainMatcher( String variable, List<ImportDeclaration> importList )
    {
        this.variable = variable;
        this.importList = importList;
    }

    @Override
    public boolean match( Node node )
    {
        boolean result = false;
        if( node instanceof ObjectCreationExpr )
        {
            ObjectCreationExpr creation = (ObjectCreationExpr)node;
            List<Expression> args = creation.getArgs();
            if( args != null )
            {
                if( isVariableUsed( args, variable ) )
                {
                    String name = creation.getType().getName();
                    Class type = ClassUtils.getPossibleFullClassName( name, importList );
                    result = ClassUtils.isRiskyIOType( type );
                }
            }
        }

        return result;
    }

    private boolean isVariableUsed( List<Expression> args, String variable )
    {
        boolean isUsed = false;
        if( args != null )
        {
            for( Expression arg : args )
            {
                if( variable.equals( arg.toString() ) )
                {
                    isUsed = true;
                    break;
                }
                else if( arg instanceof ObjectCreationExpr )
                {
                    ObjectCreationExpr objectCreation = (ObjectCreationExpr)arg;
                    if( isVariableUsed( objectCreation.getArgs(), variable ) )
                    {
                        isUsed = true;
                        break;
                    }
                }
                else if( arg instanceof MethodCallExpr )
                {
                    MethodCallExpr methodCall = (MethodCallExpr)arg;
                    if( isVariableUsed( methodCall.getArgs(), variable ) )
                    {
                        isUsed = true;
                        break;
                    }
                }
                else
                {
                    LOG.debug( "Skip argument at line " + arg.getBeginLine() + " column " + arg.getBeginColumn() + arg );
                }
            }
        }
        return isUsed;
    }
}
