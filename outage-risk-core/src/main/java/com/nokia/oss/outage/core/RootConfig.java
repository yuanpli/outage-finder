package com.nokia.oss.outage.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.nokia.oss.outage.core.bean.RiskCategory;
import com.nokia.oss.outage.core.bean.RiskLevel;
import com.nokia.oss.outage.core.bean.RiskType;
import com.nokia.oss.outage.core.bean.RiskTypes;

/**
 * Created by harchen on 2015/10/21.
 */
@Configuration
@ComponentScan
public class RootConfig
{
    @Bean
    public RiskTypes riskTypes()
    {
        RiskTypes riskTypes = new RiskTypes();
        Map<String, RiskType> riskTypeMap=new HashMap<String, RiskType>();
        riskTypeMap.put( RiskTypes.OUTAGE_IO_UNCLOSED_RESOURCE, getIO_Unclosed_Resource() );
        riskTypeMap.put( RiskTypes.OUTAGE_IO_RESOURCE_MAY_FAIL_TO_CLOSE, getIO_Resource_May_Fail_To_Close() );
        riskTypeMap.put( RiskTypes.OUTAGE_IO_RESOURCE_AS_RETURN_VALUE, getIO_Resource_As_Return_Value() );
        riskTypeMap.put( RiskTypes.OUTAGE_IO_RESOURCE_AS_METHOD_INPUT_PARAMETER, getIO_Resource_As_Method_Input_Parameter() );
        riskTypeMap.put( RiskTypes.OUTAGE_IO_RESOURCE_PASSED_TO_METHOD_AS_PARAMETER, getIO_Resource_Passed_To_Method_As_Parameter() );
        riskTypeMap.put( RiskTypes.OUTAGE_THREAD_NOT_MANAGED_BY_POOL, getThread_Not_Managed_By_Pool() );
        riskTypeMap.put( RiskTypes.OUTAGE_THREAD_POOL_WITHOUT_SIZE_LIMITATION, getThread_Pool_Without_Size_Limitation() );
        riskTypeMap.put( RiskTypes.OUTAGE_THREAD_POOL_WITHOUT_QUEUE_SIZE_LIMITATION, getThread_Pool_Without_Queue_Size_Limitation() );
        riskTypeMap.put( RiskTypes.OUTAGE_THREAD_BLOCKING_QUEUE_WITHOUT_SIZE_LIMITATION, getThread_Blocking_Queue_Without_Size_Limitation() );
        riskTypeMap.put( RiskTypes.OUTAGE_THREAD_INTERRUPTED_EXCEPTION_CAUGHT_WITHOUT_BREAK_THE_LOOP, getThread_Interrupted_Exception_Caught_Without_Break_The_Loop() );
        riskTypeMap.put( RiskTypes.OUTAGE_THREAD_INTERRUPTED_EXCEPTION_CAUGHT_WITHOUT_RESET_INTERRUPTION_STATUS, getThread_Interrupted_Exception_Caught_Without_Reset_Interruption_Status() );
        riskTypeMap.put( RiskTypes.OUTAGE_CPU_WHILE_TRUE_LOOP_WITHOUT_QUIT_STATEMENT, getWhile_True_Loop_Without_Quit_Statement() );
        riskTypeMap.put( RiskTypes.OUTAGE_CONCURRENCY_USE_NONE_ATOMIC_OPERATION_ON_THREAD_SAFE_COLLECTION, getConcurrency_Use_None_Atomic_Operation_On_Thread_Safe_Collection() );
        riskTypeMap.put( RiskTypes.OUTAGE_CONCURRENCY_SYNCHRONIZE_ON_LOCAL_VARIABLE, getConcurrency_Synchronized_On_Local_Variable() );
        riskTypeMap.put( RiskTypes.OUTAGE_CONCURRENCY_NESTED_SYNCHRONIZATION, getConcurrency_Nested_Synchronization() );
        riskTypeMap.put( RiskTypes.OUTAGE_CONCURRENCY_WAIT_AND_NOTIFY, getConcurrency_Wait_And_Notify() );
        riskTypeMap.put( RiskTypes.OUTAGE_CONCURRENCY_LOCK_IS_NOT_UNLOCKED_IN_FINALLY_BLOCK, getConcurrency_Lock_Is_Not_Unlocked_In_Finally_Block() );
        riskTypeMap.put( RiskTypes.OUTAGE_EXCEPTION_IGNORE_EXCEPTION_IN_CATCH_STATEMENT, getException_Original_Exception_Ignored_When_Throw_New_Exception() );
        riskTypeMap.put( RiskTypes.OUTAGE_MEMORY_DO_NOT_START_PROCESS_IN_JAVA, getMemory_Do_Not_Start_Process_In_Java() );
        riskTypeMap.put( RiskTypes.OUTAGE_MEMORY_JAXB_CONTEXT_RE_CREATION, getMemory_JAXB_Context_Re_Creation() );
        riskTypes.setRiskTypes(riskTypeMap);
        return riskTypes;
    }

    private RiskType getIO_Unclosed_Resource()
    {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_IO_UNCLOSED_RESOURCE);
        riskType.setRiskCategory( RiskCategory.IO );
        riskType.setRiskLevel( RiskLevel.BLOCKER);
        riskType.setDescription( "The resource is not closed or not closed in finally block" );
        return riskType;
    }

    private RiskType getIO_Resource_May_Fail_To_Close()
    {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_IO_RESOURCE_MAY_FAIL_TO_CLOSE);
        riskType.setRiskCategory( RiskCategory.IO );
        riskType.setRiskLevel( RiskLevel.CRITICAL);
        riskType.setDescription( "The resource may fail to close due to the exception of previous IO operation" );
        return riskType;
    }

    private RiskType getIO_Resource_As_Return_Value()
    {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_IO_RESOURCE_AS_RETURN_VALUE);
        riskType.setRiskCategory( RiskCategory.IO );
        riskType.setRiskLevel( RiskLevel.INFO);
        riskType.setDescription( "The resource is return value, which may fail to close in the subsequent methods" );
        return riskType;
    }

    private RiskType getIO_Resource_As_Method_Input_Parameter()
    {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_IO_RESOURCE_AS_METHOD_INPUT_PARAMETER);
        riskType.setRiskCategory( RiskCategory.IO );
        riskType.setRiskLevel( RiskLevel.INFO);
        riskType.setDescription( "The resource is an input parameter, and is not closed in this method, it maybe an unclosed resource" );
        return riskType;
    }

    private RiskType getIO_Resource_Passed_To_Method_As_Parameter()
    {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_IO_RESOURCE_PASSED_TO_METHOD_AS_PARAMETER);
        riskType.setRiskCategory( RiskCategory.IO );
        riskType.setRiskLevel( RiskLevel.INFO);
        riskType.setDescription( "The resource is passed into a method as parameter, and is not closed where it's opened, it may fail to close in the target method" );
        return riskType;
    }

    private RiskType getThread_Not_Managed_By_Pool() {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_THREAD_NOT_MANAGED_BY_POOL);
        riskType.setRiskCategory( RiskCategory.Thread );
        riskType.setRiskLevel( RiskLevel.MAJOR);
        riskType.setDescription( "The thread is not managed by pool" );
        return riskType;
    }

    private RiskType getThread_Pool_Without_Size_Limitation() {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_THREAD_POOL_WITHOUT_SIZE_LIMITATION);
        riskType.setRiskCategory( RiskCategory.Thread );
        riskType.setRiskLevel( RiskLevel.MAJOR);
        riskType.setDescription( "The thread pool has no size limitation" );
        return riskType;
    }

    private RiskType getThread_Pool_Without_Queue_Size_Limitation() {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_THREAD_POOL_WITHOUT_QUEUE_SIZE_LIMITATION);
        riskType.setRiskCategory( RiskCategory.Thread );
        riskType.setRiskLevel( RiskLevel.MAJOR);
        riskType.setDescription( "The thread pool use a default task queue, which has no limitation of queue size" );
        return riskType;
    }

    private RiskType getThread_Blocking_Queue_Without_Size_Limitation() {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_THREAD_BLOCKING_QUEUE_WITHOUT_SIZE_LIMITATION);
        riskType.setRiskCategory( RiskCategory.Thread );
        riskType.setRiskLevel( RiskLevel.MAJOR);
        riskType.setDescription( "The blocking queue has no size limitation" );
        return riskType;
    }

    private RiskType getThread_Interrupted_Exception_Caught_Without_Break_The_Loop() {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_THREAD_INTERRUPTED_EXCEPTION_CAUGHT_WITHOUT_BREAK_THE_LOOP);
        riskType.setRiskCategory( RiskCategory.Thread );
        riskType.setRiskLevel( RiskLevel.MAJOR);
        riskType.setDescription( "While InterruptedException is caught, the loop is not terminated!" );
        return riskType;
    }

    private RiskType getThread_Interrupted_Exception_Caught_Without_Reset_Interruption_Status() {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_THREAD_INTERRUPTED_EXCEPTION_CAUGHT_WITHOUT_RESET_INTERRUPTION_STATUS);
        riskType.setRiskCategory( RiskCategory.Thread );
        riskType.setRiskLevel( RiskLevel.MAJOR);
        riskType.setDescription( "While InterruptedException is caught, the interruption status is not reset" );
        return riskType;
    }

    private RiskType getWhile_True_Loop_Without_Quit_Statement() {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_CPU_WHILE_TRUE_LOOP_WITHOUT_QUIT_STATEMENT);
        riskType.setRiskCategory( RiskCategory.CPU );
        riskType.setRiskLevel( RiskLevel.CRITICAL);
        riskType.setDescription( "There is no 'break' or 'return' statement in 'while(true)' block, it's impossible to reach the 'exit' point" );
        return riskType;
    }

    private RiskType getConcurrency_Use_None_Atomic_Operation_On_Thread_Safe_Collection() {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_CONCURRENCY_USE_NONE_ATOMIC_OPERATION_ON_THREAD_SAFE_COLLECTION);
        riskType.setRiskCategory( RiskCategory.Concurrency );
        riskType.setRiskLevel( RiskLevel.BLOCKER);
        riskType.setDescription( "None atomic operations, such as 'put if absent' or 'replace', are performed on thread safe type. Consider using the atomic API directly" );
        return riskType;
    }


    private RiskType getConcurrency_Synchronized_On_Local_Variable() {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_CONCURRENCY_SYNCHRONIZE_ON_LOCAL_VARIABLE);
        riskType.setRiskCategory( RiskCategory.Concurrency );
        riskType.setRiskLevel( RiskLevel.CRITICAL);
        riskType.setDescription( "Synchronize on local variable is usually useless, because in most cases the local variables cannot be shared by multiple threads" );
        return riskType;
    }

    private RiskType getConcurrency_Nested_Synchronization() {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_CONCURRENCY_NESTED_SYNCHRONIZATION);
        riskType.setRiskCategory( RiskCategory.Concurrency );
        riskType.setRiskLevel( RiskLevel.INFO);
        riskType.setDescription( "Nested synchronization is error prone, use explicit lock from java.util.concurrent.locks.Lock after Java 5" );
        return riskType;
    }

    private RiskType getConcurrency_Wait_And_Notify() {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_CONCURRENCY_WAIT_AND_NOTIFY);
        riskType.setRiskCategory( RiskCategory.Concurrency );
        riskType.setRiskLevel( RiskLevel.INFO);
        riskType.setDescription( "The wait() and notify() call is difficult to use, use java.util.concurrent.BlockingQueue after Java 5" );
        return riskType;
    }

    private RiskType getConcurrency_Lock_Is_Not_Unlocked_In_Finally_Block()
    {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_CONCURRENCY_LOCK_IS_NOT_UNLOCKED_IN_FINALLY_BLOCK);
        riskType.setRiskCategory( RiskCategory.Concurrency );
        riskType.setRiskLevel( RiskLevel.BLOCKER);
        riskType.setDescription( "The lock is not unlocked in finally block which may cause dead lock if any runtime exception is thrown" );
        return riskType;
    }


    private RiskType getException_Original_Exception_Ignored_When_Throw_New_Exception() {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_EXCEPTION_IGNORE_EXCEPTION_IN_CATCH_STATEMENT);
        riskType.setRiskCategory( RiskCategory.Exception );
        riskType.setRiskLevel( RiskLevel.MAJOR);
        riskType.setDescription( "The original exception is ignored when new exception is thrown, which cause it's impossible to find the root cause" );
        return riskType;
    }

    private RiskType getMemory_Do_Not_Start_Process_In_Java() {
        RiskType riskType = new RiskType();
        riskType.setId( RiskTypes.OUTAGE_MEMORY_DO_NOT_START_PROCESS_IN_JAVA);
        riskType.setRiskCategory( RiskCategory.Memory );
        riskType.setRiskLevel( RiskLevel.MAJOR);
        riskType.setDescription( "Start process in Java make folk a new process which may cost the same memory usage, then cause OutOfMemory error" );
        return riskType;
    }

    private RiskType getMemory_JAXB_Context_Re_Creation() {
        RiskType riskType=new RiskType();
        riskType.setId(RiskTypes.OUTAGE_MEMORY_JAXB_CONTEXT_RE_CREATION);
        riskType.setRiskCategory(RiskCategory.Memory);
        riskType.setRiskLevel(RiskLevel.MAJOR);
        riskType.setDescription( "The JAXB context is re-created in each method call. By default, JAXB will optimize data binding by generation of extra classes, reloading of those classes again and again will cause an PermGen/Meta Space OOM sooner or later." );
        return riskType;
    }
}
