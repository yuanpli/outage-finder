package com.nokia.oss.outage.core.rule;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.javaparser.ast.CompilationUnit;
import com.nokia.oss.outage.core.bean.OutageRisk;
import com.nokia.oss.outage.core.bean.RiskTypes;
import com.nokia.oss.outage.core.io.ResourceCloseVisitor;

/**
 * Created by harchen on 2015/10/10.
 */
@Component
public class IOResourceClosingRiskRule
    extends AbstractOutageRiskRule
{
    private static final Logger LOG = LoggerFactory.getLogger( IOResourceClosingRiskRule.class );
    @Autowired
    private RiskTypes riskTypes;

    @Override
    protected List<OutageRisk> checkForRisks( CompilationUnit cu )
    {
        LOG.debug( "Check IO Resource ......" );
        ResourceCloseVisitor visitor = new ResourceCloseVisitor( riskTypes );
        visitor.visit( cu, null );
        return visitor.getOutageRisks();
    }

    public void setRiskTypes( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }
}
