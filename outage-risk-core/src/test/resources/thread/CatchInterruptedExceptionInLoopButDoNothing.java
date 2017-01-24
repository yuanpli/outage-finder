package com.nokia.oss.commons.tools.outage;

import java.util.concurrent.TimeUnit;

/**
 * Created by harchen on 2015/12/4.
 */
public class CatchInterruptedExceptionInLoopButDoNothing
    implements Runnable
{
    @Override
    public void run()
    {

        while( true )
        {
            System.out.println( "Hello World!" );
            try
            {
                Thread.sleep( 10 * 1000 );
            }
            catch( InterruptedException e )
            {
                System.out.println(e.printStackTrace());
            }
        }
    }
}
