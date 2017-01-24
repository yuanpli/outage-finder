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
public class ExceptionRuleTest
{
    @Autowired
    private ExceptionRule rule;
    @Autowired
    private RiskTypes riskTypes;

    public void setRule( ExceptionRule rule )
    {
        this.rule = rule;
    }

    public void setRiskTypes( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }

    @Test
    public void testCheck_Given_Throw_New_Exception_But_Ignore_Original_Exception_Expect_Outage_Risk()
        throws Exception
    {
        File resource = TestUtils.getTestResource( "exception/IgnoreTheOriginalExceptionWhenThrowNewException.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 1, risks.size() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_EXCEPTION_IGNORE_EXCEPTION_IN_CATCH_STATEMENT), risks.get( 0 ).getType() );
    }

    @Test
    public void testCheck_Given_Throw_New_Exception_But_Ignore_The_Original_Stack_Trace_Expect_Outage_Risk()
            throws Exception
    {
        File resource = TestUtils.getTestResource( "exception/IgnoreTheOriginalStackTraceWhenThrowNewException.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 1, risks.size() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_EXCEPTION_IGNORE_EXCEPTION_IN_CATCH_STATEMENT), risks.get( 0 ).getType() );
    }

    @Test
    public void testCheck_Given_Print_Stack_Trace_Without_Throw_New_Exception_Expect_No_Risk()
            throws Exception
    {
        File resource = TestUtils.getTestResource( "exception/PrintStackTrackeOfTheOriginalExceptionAndThrowNewException.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 0, risks.size() );
    }

    @Test
    public void testCheck_Given_Exception_Is_Re_Thrown_Expect_No_Risk() throws Exception {
        File resource = TestUtils.getTestResource( "exception/ReThrowException.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 0, risks.size() );
    }

    @Test
    public void testCheck_Given_Multi_Catch_Exception_Is_Ignored_Expect_Risk() throws Exception {
        File resource = TestUtils.getTestResource( "exception/MultiCatchWithoutHanding.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 1, risks.size() );
    }
}
