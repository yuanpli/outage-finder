package com.nokia.oss.outage.core.rule;

import com.github.javaparser.ast.CompilationUnit;
import com.nokia.oss.outage.core.bean.OutageRisk;
import com.nokia.oss.outage.core.bean.RiskTypes;
import com.nokia.oss.outage.core.memory.MemoryLeakVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by harchen on 2016/9/14.
 */
@Component
public class MemoryRule
    extends AbstractOutageRiskRule
{
    private static final Logger LOG = LoggerFactory.getLogger( MemoryRule.class );
    @Autowired
    private RiskTypes riskTypes;

    @Override
    protected List<OutageRisk> checkForRisks( CompilationUnit cu )
    {
        LOG.debug( "Check Memory leak ..." );
        MemoryLeakVisitor visitor = new MemoryLeakVisitor( riskTypes );
        visitor.visit( cu, null );
        return visitor.getOutageRisks();
    }

    public void setRiskTypes( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }
}
