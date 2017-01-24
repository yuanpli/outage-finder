package com.nokia.oss.commons.tools.outage;

/**
 * Created by harchen on 2015/12/4.
 */
public class CatchInterruptedExceptionButDoNothing
    implements Runnable
{
    @Override
    public void run()
    {
        System.out.println( "Hello World!" );
        try
        {
            Thread.sleep( 10 * 1000 );
        }
        catch( InterruptedException e )
        {
            e.printStackTrace();
        }

    }
}
