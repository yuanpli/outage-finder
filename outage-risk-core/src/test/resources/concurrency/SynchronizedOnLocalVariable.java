package com.nokia.oss.commons.tools.outage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by harchen on 2015/12/7.
 */
public class SynchronizedOnLocalVariable
{
    private Map<String, String> config = new HashMap<>();

    public void update( String key, String value )
    {
        Object lock = new Object();
        synchronized( lock )
        {
            if( config.get( key ) != null )
            {
                config.put( key, value );
            }
        }
    }

    public void delete( String key )
    {
        synchronized( new Object() )
        {
            if( config.get( key ) != null )
            {
                config.remove( key );
            }
        }
    }
}
