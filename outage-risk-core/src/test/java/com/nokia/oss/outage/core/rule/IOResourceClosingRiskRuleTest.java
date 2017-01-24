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

/**
 * Created by harchen on 2015/10/10.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = RootConfig.class )
public class IOResourceClosingRiskRuleTest
{
    @Autowired
    private IOResourceClosingRiskRule rule;
    @Autowired
    private RiskTypes riskTypes;

    public void setRule( IOResourceClosingRiskRule rule )
    {
        this.rule = rule;
    }

    public void setRiskTypes( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }

    @Test
    public void testCheck_Given_Unclosed_InputStream_Expect_Outage_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource( "io/RealignHelper.java" );
        List<OutageRisk> riskList = rule.check( resource );
        OutageRisk risk = riskList.get( 0 );
        assertEquals( resource.getAbsolutePath(), risk.getPath() );
        assertEquals( "com.nokia.oss.commons.tools.outage.RealignHelper", risk.getClassName() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_IO_UNCLOSED_RESOURCE), risk.getType() );
        assertEquals( 18, risk.getRow() );
    }

    @Test
    public void testCheck_Given_Resource_Not_Closed_In_Finally_Block_Expect_Exception() throws Exception
    {
        File resource = TestUtils.getTestResource( "io/NSSCapacityLogger.java" );
        List<OutageRisk> riskList = rule.check( resource );
        OutageRisk risk = riskList.get( 0 );
        assertEquals( resource.getAbsolutePath(), risk.getPath() );
        assertEquals( "com.nokia.oss.commons.tools.outage.NSSCapacityLogger", risk.getClassName() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_IO_UNCLOSED_RESOURCE), risk.getType() );
        assertEquals( 22, risk.getRow() );
    }

    @Test
    public void testCheck_Given_Resource_Closed_Both_In_Try_And_Finally_Block_Expect_No_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource( "io/IOCloseBestPractise.java" );
        List<OutageRisk> riskList = rule.check( resource );
        assertEquals( 0, riskList.size() );
    }

    @Test
    public void testCheck_Given_Two_Resources_Closing_In_Same_Try_Clause_Expect_OutageRisk() throws Exception
    {
        File resource = TestUtils.getTestResource( "io/XoHFileTransferMarshaller.java" );
        List<OutageRisk> riskList = rule.check( resource );
        OutageRisk risk = riskList.get( 0 );
        assertEquals( resource.getAbsolutePath(), risk.getPath() );
        assertEquals( "com.nokia.oss.commons.tools.outage.XoHFileTransferMarshaller", risk.getClassName() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_IO_RESOURCE_MAY_FAIL_TO_CLOSE), risk.getType() );
        assertEquals( 71, risk.getRow() );
    }

    @Test
    public void testCheck_Given_Two_Resources_Closing_In_Different_Block_Within_Same_Try_Clause_Expect_OutageRisk()
        throws Exception
    {
        File resource = TestUtils.getTestResource( "io/ObservationBean.java" );
        List<OutageRisk> riskList = rule.check( resource );
        OutageRisk risk = riskList.get( 0 );
        assertEquals( resource.getAbsolutePath(), risk.getPath() );
        assertEquals( "com.nokia.oss.commons.tools.outage.ObservationBean", risk.getClassName() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_IO_RESOURCE_MAY_FAIL_TO_CLOSE), risk.getType() );
        assertEquals( 43, risk.getRow() );

    }

    @Test
    public void testCheck_Given_Two_Resources_Closing_In_Same_Finally_Clause_Without_Try_Expect_OutageRisk()
        throws Exception
    {
        File resource = TestUtils.getTestResource( "io/FileOperation2.java" );
        List<OutageRisk> riskList = rule.check( resource );
        OutageRisk risk = riskList.get( 0 );
        assertEquals( resource.getAbsolutePath(), risk.getPath() );
        assertEquals( "com.nokia.oss.commons.tools.outage.FileOperation2", risk.getClassName() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_IO_RESOURCE_MAY_FAIL_TO_CLOSE), risk.getType() );
        assertEquals( 37, risk.getRow() );

    }

    @Test
    public void testCheck_Given_Two_Resources_Closing_In_Same_Finally_Clause_Without_Try_Expect_OutageRisk_2()
        throws Exception
    {
        File resource = TestUtils.getTestResource( "io/SimpleFTPClient.java" );
        List<OutageRisk> riskList = rule.check( resource );
        OutageRisk risk = riskList.get( 0 );
        assertEquals( resource.getAbsolutePath(), risk.getPath() );
        assertEquals( "com.nokia.oss.commons.tools.outage.SimpleFTPClient", risk.getClassName() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_IO_RESOURCE_MAY_FAIL_TO_CLOSE), risk.getType() );
        assertEquals( 35, risk.getRow() );

    }

    @Test
    public void testCheck_Given_Resource_As_Input_Parameter_Expect_Outage_Risk_Of_Waring_Level() throws Exception
    {
        File resource = TestUtils.getTestResource( "io/IOResourceAsInputParameter.java" );
        List<OutageRisk> riskList = rule.check( resource );
        OutageRisk risk = riskList.get( 0 );
        assertEquals( resource.getAbsolutePath(), risk.getPath() );
        assertEquals( "com.nokia.oss.commons.tools.outage.IOResourceAsInputParameter", risk.getClassName() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_IO_RESOURCE_AS_METHOD_INPUT_PARAMETER), risk.getType() );
        assertEquals( 10, risk.getRow() );
    }

    @Test
    public void testCheck_Given_Resource_Pass_To_Method_Call_Expect_Outage_Risk_Of_Warning_Level() throws Exception
    {
        File resource = TestUtils.getTestResource( "io/IOResourcePassedAsParameter.java" );
        List<OutageRisk> riskList = rule.check( resource );
        OutageRisk risk = riskList.get( 0 );
        assertEquals( resource.getAbsolutePath(), risk.getPath() );
        assertEquals( "com.nokia.oss.commons.tools.outage.IOResourcePassedAsParameter", risk.getClassName() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_IO_RESOURCE_PASSED_TO_METHOD_AS_PARAMETER), risk.getType() );
        assertEquals( 14, risk.getRow() );
    }

    @Test
    public void testCheck_Given_Resource_Pass_To_IOUtils_CloseQuietly_Call_Expect_No_Outage_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource( "io/IOResoucePassedIntoCommonIOUtilMethod.java" );
        List<OutageRisk> riskList = rule.check( resource );
        assertEquals( 0, riskList.size() );
    }

    @Test
    public void testCheck_Given_Resource_Pass_As_Parameter_Of_New_IO_Resource_Expect_No_Outage_Risk_Of_The_Passed_In_Resource()
        throws Exception
    {
        File resource = TestUtils.getTestResource( "io/IOResourceChain.java" );
        List<OutageRisk> riskList = rule.check( resource );
        OutageRisk risk = riskList.get( 0 );
        assertEquals( resource.getAbsolutePath(), risk.getPath() );
        assertEquals( "com.nokia.oss.commons.tools.outage.IOResourceChain", risk.getClassName() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_IO_RESOURCE_AS_RETURN_VALUE), risk.getType() );
        assertEquals( 19, risk.getRow() );
    }

    @Test
    public void testCheck_Given_Resource_As_Return_Value_Expect_Outage_Risk_Of_Warning_Type() throws Exception
    {
        File resource = TestUtils.getTestResource( "io/IOResourceAsReturnValue.java" );
        List<OutageRisk> riskList = rule.check( resource );
        OutageRisk risk = riskList.get( 0 );
        assertEquals( resource.getAbsolutePath(), risk.getPath() );
        assertEquals( "com.nokia.oss.commons.tools.outage.IOResourceAsReturnValue", risk.getClassName() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_IO_RESOURCE_AS_RETURN_VALUE), risk.getType() );
        assertEquals( 12, risk.getRow() );
    }

    @Test
    public void testCheck_Given_New_Resource_As_Return_Value_Expect_Outage_Risk_Of_Warning_Type() throws Exception
    {
        File resource = TestUtils.getTestResource( "io/NewIOResourceAsReturnValue.java" );
        List<OutageRisk> riskList = rule.check( resource );
        OutageRisk risk = riskList.get( 0 );
        assertEquals( resource.getAbsolutePath(), risk.getPath() );
        assertEquals( "com.nokia.oss.commons.tools.outage.NewIOResourceAsReturnValue", risk.getClassName() );
        assertEquals( riskTypes.get( RiskTypes.OUTAGE_IO_RESOURCE_AS_RETURN_VALUE), risk.getType() );
        assertEquals( 12, risk.getRow() );
    }

    @Test
    public void testCheck_Given_Resource_Closing_In_Embeded_Try_Block_Expect_No_Outage_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource( "io/IOResourceClosingInImbededTryBLock.java" );
        List<OutageRisk> riskList = rule.check( resource );
        assertEquals( 0, riskList.size() );

    }

    @Test
    public void testCheck_Given_Resources_Declare_As_InputStream_Assign_With_ByteArrayInputStream_Expect_No_OutageRisk()
        throws Exception
    {
        File resource = TestUtils.getTestResource( "io/IOResourceAssignedWithPureInMemoryTypeInstance.java" );
        List<OutageRisk> riskList = rule.check( resource );
        assertEquals( 0, riskList.size() );
    }

    @Test
    public void testCheck_Given_Two_Resources_Closing_In_Different_Try_Block_With_Same_Finally_Clause_Expect_No_OutageRisk()
        throws Exception
    {
        File resource = TestUtils.getTestResource( "io/FileOperation.java" );
        List<OutageRisk> riskList = rule.check( resource );
        assertEquals( 0, riskList.size() );
    }

    @Test
    public void testCheck_Given_Unclosed_In_Memory_Resource_Expect_No_Outage_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource( "io/InMemoryIOOperation.java" );
        List<OutageRisk> riskList = rule.check( resource );
        assertEquals( 0, riskList.size() );
    }

    @Test
    public void testCheck_Given_InputStream_Close_In_The_Next_Statement_Of_Declaration_Expect_NO_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource( "io/FileInputFactory.java" );
        List<OutageRisk> riskList = rule.check( resource );
        assertEquals( 0, riskList.size() );
    }

    @Test
    public void testCheck_Given_Reader_And_Writer_Created_From_HttpServletRequest_Param_Without_Closing_Expect_No_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource("io/IOResourceCreateFromHTTPServletRequest.java");
        List<OutageRisk> riskList = rule.check( resource );
        assertEquals( 0, riskList.size() );
    }

    @Test
    public void testCheck_Given_InputStream_Closing_Utility_Method_Expect_No_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource("io/IOResourceClosingUtilityMethod.java");
        List<OutageRisk> riskList = rule.check( resource );
        assertEquals( 0, riskList.size());
    }

    @Test
    public void testCheck_Given_InputStream_Closed_In_Try_Block_Expect_Risk() throws Exception
    {
        File resource = TestUtils.getTestResource("io/ZipStreamUtilities.java");
        List<OutageRisk> riskList = rule.check( resource );
        assertEquals( 1, riskList.size());
    }

    @Test
    public void testCheck_Given_Java_7_Try_With_Resource_Statement_Expect_No_Risk() throws Exception {
        File resource = TestUtils.getTestResource("io/Java7TryWithResourceStatement.java");
        List<OutageRisk> riskList = rule.check( resource );
        assertEquals( 0, riskList.size());
    }
}
