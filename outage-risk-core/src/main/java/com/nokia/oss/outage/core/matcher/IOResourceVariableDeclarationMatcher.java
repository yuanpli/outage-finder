package com.nokia.oss.outage.core.matcher;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.TryStmt;
import com.nokia.oss.outage.core.util.ClassUtils;

/**
 * Created by harchen on 2015/10/12.
 */
public class IOResourceVariableDeclarationMatcher
    implements NodeMatcher
{
    private List<ImportDeclaration> importList = new ArrayList<ImportDeclaration>();

    public IOResourceVariableDeclarationMatcher( List<ImportDeclaration> importList )
    {
        this.importList = importList;
    }

    @Override
    public boolean match( Node node )
    {
        boolean result = false;
        if( node != null && node instanceof VariableDeclarationExpr )
        {
            VariableDeclarationExpr expression = (VariableDeclarationExpr)node;
            Class type = ClassUtils.getPossibleFullClassName( expression.getType().toString(), importList );
            if(ClassUtils.isRiskyIOType(type))
            {
                if(node.getParentNode() instanceof TryStmt)
                {
                    result=false;
                }else
                {
                    result=true;
                }
            }
        }
        return result;
    }
}
