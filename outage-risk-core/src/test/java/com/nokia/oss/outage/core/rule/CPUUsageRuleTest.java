package com.nokia.oss.outage.core.rule;

import com.nokia.oss.outage.core.RootConfig;
import com.nokia.oss.outage.core.TestUtils;
import com.nokia.oss.outage.core.bean.OutageRisk;
import com.nokia.oss.outage.core.bean.RiskTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by harchen on 2015/12/7.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(classes = RootConfig.class)
public class CPUUsageRuleTest
{
    @Autowired
    private CPUUsageRule rule;
    @Autowired
    private RiskTypes riskTypes;

    public void setRule( CPUUsageRule rule )
    {
        this.rule = rule;
    }

    public void setRiskTypes( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }

    @Test
    public void testCheck_Given_While_True_Loop_Without_Break_Statement_Expect_Outage_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource( "cpu/WhileTrueWithNoBreak.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 1, risks.size() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_CPU_WHILE_TRUE_LOOP_WITHOUT_QUIT_STATEMENT), risks.get( 0 ).getType() );
    }
}
