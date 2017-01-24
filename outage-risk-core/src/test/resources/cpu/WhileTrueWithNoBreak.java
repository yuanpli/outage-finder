package com.nokia.oss.commons.tools.outage;

/**
 * Created by harchen on 2015/12/7.
 */
public class WhileTrueWithNoBreak
    implements Runnable
{

    @Override
    public void run()
    {
        while( true )
        {
            System.out.println( "Hello World" );
            try
            {
                Thread.sleep( 1000 );
            }
            catch( InterruptedException e )
            {
                e.printStackTrace();
            }
        }
    }
}
