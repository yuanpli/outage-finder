package com.nokia.oss.commons.tools.outage;

/**
 * Created by harchen on 2015/10/22.
 */
public class ThreadCreationInInitializerBlock
{
    static
    {
        Thread t = new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                hello();
            }
        } );
    }

    private static void hello()
    {
        System.out.println( "Hello World!" );
    }

}
