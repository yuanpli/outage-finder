package com.nokia.oss.commons.tools.outage;

/**
 * Created by harchen on 2015/10/22.
 */
public class NewThreadInFieldInitialization
{
    private Thread worker = new Thread( new Runnable()
    {
        @Override
        public void run()
        {
            hello();
        }
    } );

    public void hello()
    {
        System.out.println( "Hello World!" );
    }
}
