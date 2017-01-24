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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by harchen on 2016/9/14.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = RootConfig.class )
public class MemoryRuleTest
{
    @Autowired
    private MemoryRule rule;
    @Autowired
    private RiskTypes riskTypes;

    public void setRule( MemoryRule rule )
    {
        this.rule = rule;
    }

    public void setRiskTypes( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }

    @Test
    public void testCheck_Given_Start_Process_In_Java_Expect_Outage_Risk() throws Exception {
        File resource= TestUtils.getTestResource("memory/DoNotStartProcessInJava.java");
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 1, risks.size() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_MEMORY_DO_NOT_START_PROCESS_IN_JAVA), risks.get( 0 ).getType() );
    }

    @Test
    public void testCheck_Given_Jaxb_Context_Is_Assigned_To_Local_Variable_Expect_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource( "memory/CreateJaxbContextInMethodAndAssignToLocalVariable.java" );
        List<OutageRisk> riskList = rule.check( resource );
        assertTrue( riskList.size() > 0 );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_MEMORY_JAXB_CONTEXT_RE_CREATION ), riskList.get( 0 ).getType() );
    }

    @Test
    public void testCheck_Given_JaxbC_Context_Is_Assigned_To_Field_Expect_No_Risk() throws Exception {
        File resource = TestUtils.getTestResource( "memory/CreateJaxbContextInMethodAndAssignToField.java" );
        List<OutageRisk> riskList = rule.check( resource );
        assertEquals(0,riskList.size());
    }
}
