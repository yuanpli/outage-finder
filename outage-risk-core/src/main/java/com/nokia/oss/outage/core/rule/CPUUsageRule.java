package com.nokia.oss.outage.core.rule;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.javaparser.ast.CompilationUnit;
import com.nokia.oss.outage.core.bean.OutageRisk;
import com.nokia.oss.outage.core.bean.RiskTypes;
import com.nokia.oss.outage.core.cpu.CPUUsageVisitor;

/**
 * Created by harchen on 2015/12/7.
 */
@Component
public class CPUUsageRule
    extends AbstractOutageRiskRule
{
    private static final Logger LOG = LoggerFactory.getLogger( CPUUsageRule.class );
    @Autowired
    private RiskTypes riskTypes;

    @Override
    protected List<OutageRisk> checkForRisks(CompilationUnit cu )
    {
        LOG.debug( "Check CPU usage ......" );
        CPUUsageVisitor visitor = new CPUUsageVisitor( riskTypes );
        visitor.visit( cu, null );
        return visitor.getOutageRisks();
    }

    public void setRiskTypes( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }
}
