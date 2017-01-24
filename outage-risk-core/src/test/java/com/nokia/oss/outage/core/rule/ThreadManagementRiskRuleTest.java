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
 * Created by harchen on 2015/10/21.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(classes = RootConfig.class)
public class ThreadManagementRiskRuleTest
{
    @Autowired
    private ThreadManagementRiskRule rule;
    @Autowired
    private RiskTypes riskTypes;

    public void setRule( ThreadManagementRiskRule rule )
    {
        this.rule = rule;
    }

    @Test
    public void testCheck_Given_New_Thread_In_Method_Body_Expect_Outage_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource( "thread/UploadBeanReadSourceWrapper.java" );
        List<OutageRisk> risks = rule.check( resource );
        OutageRisk risk = risks.get( 0 );
        assertEquals( resource.getAbsolutePath(), risk.getPath() );
        assertEquals( "com.nokia.oss.commons.tools.outage.UploadBeanReadSourceWrapper", risk.getClassName() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_THREAD_NOT_MANAGED_BY_POOL), risk.getType() );
        assertEquals( 18, risk.getRow() );

    }

    @Test
    public void testCheck_Given_New_Thread_In_Field_Initialization_Expect_Outage_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource( "thread/NewThreadInFieldInitialization.java" );
        List<OutageRisk> risks = rule.check( resource );
        OutageRisk risk = risks.get( 0 );
        assertEquals( resource.getAbsolutePath(), risk.getPath() );
        assertEquals( "com.nokia.oss.commons.tools.outage.NewThreadInFieldInitialization", risk.getClassName() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_THREAD_NOT_MANAGED_BY_POOL), risk.getType() );
    }

    @Test
    public void testCheck_Given_New_Thread_In_Initializer_Expect_Outage_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource( "thread/ThreadCreationInInitializerBlock.java" );
        List<OutageRisk> risks = rule.check( resource );
        OutageRisk risk = risks.get( 0 );
        assertEquals( resource.getAbsolutePath(), risk.getPath() );
        assertEquals( "com.nokia.oss.commons.tools.outage.ThreadCreationInInitializerBlock", risk.getClassName() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_THREAD_NOT_MANAGED_BY_POOL), risk.getType() );
        assertEquals( 10, risk.getRow() );

    }

    @Test
    public void testCheck_Given_Cached_Thread_Pool_Without_Size_Limit_In_Field_Declaration_Expect_Outage_Risk()
        throws Exception
    {
        File resource =
            TestUtils.getTestResource( "thread/NewThreadPoolFromFactoryInFieldDeclarationWithoutSizeLimit.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertTrue( hasRiskType( risks, RiskTypes.OUTAGE_THREAD_POOL_WITHOUT_QUEUE_SIZE_LIMITATION) );
        assertTrue( hasRiskType( risks, RiskTypes.OUTAGE_THREAD_POOL_WITHOUT_SIZE_LIMITATION) );
    }

    private boolean hasRiskType( List<OutageRisk> risks, String type )
    {
        boolean result = false;
        for( OutageRisk risk : risks )
        {
            if( risk.getType().equals( riskTypes.get( type ) ) )
            {
                result = true;
            }
        }
        return result;
    }

    @Test
    public void testCheck_Given_Cached_Thread_Pool_Without_Size_Limit_Initialize_In_Constructor_Expect_Outage_Risk()
        throws Exception
    {
        File resource = TestUtils.getTestResource( "thread/NewThreadPoolFromFactoryInConstructorWithoutSizeLimit.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertTrue( hasRiskType( risks, RiskTypes.OUTAGE_THREAD_POOL_WITHOUT_QUEUE_SIZE_LIMITATION) );
        assertTrue( hasRiskType( risks, RiskTypes.OUTAGE_THREAD_POOL_WITHOUT_SIZE_LIMITATION) );
    }

    @Test
    public void testCheck_Given_Cached_Thread_Pool_Without_Size_Limit_Initialize_In_Method_Expect_Outage_Risk()
        throws Exception
    {
        File resource =
            TestUtils.getTestResource( "thread/NewThreadPoolFromFactoryInMethodDeclarationWithoutSizeLimit.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertTrue( hasRiskType( risks, RiskTypes.OUTAGE_THREAD_POOL_WITHOUT_QUEUE_SIZE_LIMITATION) );
        assertTrue( hasRiskType( risks, RiskTypes.OUTAGE_THREAD_POOL_WITHOUT_SIZE_LIMITATION) );
    }

    @Test
    public void testCheck_Given_Blocking_Queue_Without_Size_Limit_Expect_Outage_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource( "thread/NewBlockingQueueWithoutSizeLimitation.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 3, risks.size() );
        assertTrue( hasRiskType( risks, RiskTypes.OUTAGE_THREAD_BLOCKING_QUEUE_WITHOUT_SIZE_LIMITATION) );
    }

    @Test
    public void testCheck_Given_InterruptedException_Caught_But_Do_Nothing_Expect_Outage_Risk() throws Exception {
        File resource=TestUtils.getTestResource("thread/CatchInterruptedExceptionButDoNothing.java");
        List<OutageRisk> risks=rule.check(resource);
        assertEquals(1,risks.size());
        assertTrue(hasRiskType(risks,RiskTypes.OUTAGE_THREAD_INTERRUPTED_EXCEPTION_CAUGHT_WITHOUT_RESET_INTERRUPTION_STATUS));
    }

    @Test
    public void testCheck_Given_InterruptedException_Caught_In_Loop_But_Do_Nothing_Expect_Outage_Risk() throws Exception {
        File resource=TestUtils.getTestResource("thread/CatchInterruptedExceptionInLoopButDoNothing.java");
        List<OutageRisk> risks=rule.check(resource);
        assertEquals(2,risks.size());
        assertTrue(hasRiskType(risks,RiskTypes.OUTAGE_THREAD_INTERRUPTED_EXCEPTION_CAUGHT_WITHOUT_RESET_INTERRUPTION_STATUS));
        assertTrue(hasRiskType(risks,RiskTypes.OUTAGE_THREAD_INTERRUPTED_EXCEPTION_CAUGHT_WITHOUT_BREAK_THE_LOOP));

    }
}
