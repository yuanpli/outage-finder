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
public class ConcurrencyRuleTest
{
    @Autowired
    private ConcurrencyRule rule;
    @Autowired
    private RiskTypes riskTypes;

    public void setRule( ConcurrencyRule rule )
    {
        this.rule = rule;
    }

    public void setRiskTypes( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }

    @Test
    public void testCheck_Given_None_Atomic_Operation_On_Thread_Safe_Collection_Expect_Outage_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource( "concurrency/UseNoneAtomicMethodOfThreadSafeCollection.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 1, risks.size() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_CONCURRENCY_USE_NONE_ATOMIC_OPERATION_ON_THREAD_SAFE_COLLECTION), risks.get( 0 ).getType() );
    }

    @Test
    public void testCheck_Given_Lock_Is_Not_Unlocked_In_Finally_Block_Expect_Outage_Risk() throws Exception {
        File resource = TestUtils.getTestResource( "concurrency/LockReleaseExample.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 2, risks.size() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_CONCURRENCY_LOCK_IS_NOT_UNLOCKED_IN_FINALLY_BLOCK), risks.get( 0 ).getType() );
    }

    @Test
    public void testCheck_Given_Lock_Is_Unlocked_In_Finally_Block_Expect_No_Outage_Risk() throws Exception {
        File resource = TestUtils.getTestResource( "concurrency/LockReleaseGoodExample.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 0, risks.size() );
    }

    @Test
    public void testCheck_Given_Lock_And_Unlock_Are_Not_In_Try_Finally_Block_Expect_Outage_Risk() throws Exception {
        File resource = TestUtils.getTestResource( "concurrency/KoalaFileTube.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 2, risks.size() );
        assertEquals(RiskTypes.OUTAGE_CONCURRENCY_LOCK_IS_NOT_UNLOCKED_IN_FINALLY_BLOCK,risks.get(0).getType().getId());
    }

    @Test
    public void testCheck_Given_Synchronized_On_Local_Object_Expect_Outage_Risk() throws Exception {
        File resource = TestUtils.getTestResource( "concurrency/SynchronizedOnLocalVariable.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 2, risks.size() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_CONCURRENCY_SYNCHRONIZE_ON_LOCAL_VARIABLE), risks.get( 0 ).getType() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_CONCURRENCY_SYNCHRONIZE_ON_LOCAL_VARIABLE), risks.get( 1 ).getType() );
    }

    @Test
    public void testCheck_Given_Nested_Synchronization_Expect_Outage_Risk() throws Exception {
        File resource = TestUtils.getTestResource( "concurrency/NestedSynchronization.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 1, risks.size() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_CONCURRENCY_NESTED_SYNCHRONIZATION), risks.get( 0 ).getType() );
    }

    @Test
    public void testCheck_Given_Wait_And_Notify_Call_Inside_Synchronized_Method_Expect_Outage_Risk() throws Exception {
        File resource = TestUtils.getTestResource( "concurrency/WaitAndNotifyExample1.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 2, risks.size() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_CONCURRENCY_WAIT_AND_NOTIFY), risks.get( 0 ).getType() );

    }

    @Test
    public void testCheck_Given_Wait_And_Notify_Call_Inside_Synchronized_Block_Expect_Outage_Risk() throws Exception {
        File resource = TestUtils.getTestResource( "concurrency/WaitAndNotifyExample2.java" );
        List<OutageRisk> risks = rule.check( resource );
        assertEquals( 2, risks.size() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_CONCURRENCY_WAIT_AND_NOTIFY), risks.get( 0 ).getType() );

    }
}
