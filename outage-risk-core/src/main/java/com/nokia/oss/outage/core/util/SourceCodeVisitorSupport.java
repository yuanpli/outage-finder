package com.nokia.oss.outage.core.util;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.nokia.oss.outage.core.bean.OutageRisk;
import com.nokia.oss.outage.core.bean.OutageRiskBuilder;
import com.nokia.oss.outage.core.bean.RiskType;

/**
 * Created by harchen on 2015/10/12.
 */
public class SourceCodeVisitorSupport
    extends VoidVisitorAdapter
{
    protected String packageName;
    protected String className;
    protected List<ImportDeclaration> importList = new ArrayList<ImportDeclaration>();
    protected List<OutageRisk> outageRisks = new ArrayList<OutageRisk>();

    @Override
    public void visit( PackageDeclaration n, Object arg )
    {
        packageName = n.getName().toString();
        super.visit( n, arg );
    }

    @Override
    public void visit( ImportDeclaration n, Object arg )
    {
        importList.add( n );
        super.visit( n, arg );
    }

    @Override
    public void visit( ClassOrInterfaceDeclaration n, Object arg )
    {
        if( ModifierSet.isPublic( n.getModifiers() ) )
        {
            className = n.getName();
        }
        super.visit( n, arg );
    }

    public synchronized List<OutageRisk> getOutageRisks()
    {
        return new ArrayList<OutageRisk>( outageRisks );
    }

    public void reportRisk(RiskType type, Node node )
    {
        OutageRisk risk =
            new OutageRiskBuilder()
                .className( getFullClassName() ).type( type ).row( node.getBeginLine() ).sample( node.toString() )
                .build();
        outageRisks.add( risk );
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getClassName()
    {
        return className;
    }

    public List<ImportDeclaration> getImportList()
    {
        return importList;
    }

    protected String getFullClassName()
    {
        return packageName != null ? packageName + "." + className : className;
    }

    protected Node getUpperLevelNodeOfType( Node node, Class type )
    {
        Node found = null;
        Node current = node;
        while( (current = current.getParentNode()) != null )
        {
            if( type.isAssignableFrom( current.getClass() ) )
            {
                found = current;
                break;
            }
        }
        return found;
    }
}
