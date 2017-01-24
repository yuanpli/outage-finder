package com.nokia.oss.outage.core.memory;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.nokia.oss.outage.core.bean.RiskTypes;
import com.nokia.oss.outage.core.matcher.MethodCallOnVariableMatcher;
import com.nokia.oss.outage.core.matcher.NodeMatcher;
import com.nokia.oss.outage.core.util.SourceCodeVisitorSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by harchen on 2016/9/14.
 */
public class MemoryLeakVisitor
    extends SourceCodeVisitorSupport
{
    private static final Logger LOG = LoggerFactory.getLogger( MemoryLeakVisitor.class );
    private RiskTypes riskTypes;

    public MemoryLeakVisitor( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }

    @Override
    public void visit( MethodCallExpr methodCallExpr, Object arg )
    {
        NodeMatcher matcher = new MethodCallOnVariableMatcher( "Runtime.getRuntime()", "exec" );
        if( matcher.match( methodCallExpr ) )
        {
            reportRisk( riskTypes.get( RiskTypes.OUTAGE_MEMORY_DO_NOT_START_PROCESS_IN_JAVA), methodCallExpr );
        }
        super.visit( methodCallExpr, arg );
    }

    @Override
    public void visit( VariableDeclarationExpr expr, Object arg )
    {
        if( "JAXBContext".equals( expr.getType().toString() ) )
        {
            reportRisk( riskTypes.get( RiskTypes.OUTAGE_MEMORY_JAXB_CONTEXT_RE_CREATION ), expr );
        }
        super.visit( expr, arg );
    }
}
