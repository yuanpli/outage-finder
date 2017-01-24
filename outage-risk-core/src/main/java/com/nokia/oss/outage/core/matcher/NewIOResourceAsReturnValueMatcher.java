package com.nokia.oss.outage.core.matcher;

import java.util.List;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.nokia.oss.outage.core.util.ClassUtils;

/**
 * Created by harchen on 2015/10/13.
 */
public class NewIOResourceAsReturnValueMatcher
    implements NodeMatcher
{
    private List<ImportDeclaration> importList;

    public NewIOResourceAsReturnValueMatcher( List<ImportDeclaration> importList )
    {
        this.importList = importList;
    }

    @Override
    public boolean match( Node node )
    {
        boolean result = false;
        if( node instanceof ReturnStmt )
        {
            ReturnStmt returnStmt = (ReturnStmt)node;
            Expression expr = returnStmt.getExpr();
            if( expr instanceof ObjectCreationExpr )
            {
                ObjectCreationExpr creation = (ObjectCreationExpr)expr;
                String name = creation.getType().getName();
                Class type = ClassUtils.getPossibleFullClassName( name, importList );
                result = ClassUtils.isRiskyIOType( type );
            }
        }
        return result;
    }
}
