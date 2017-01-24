package com.nokia.oss.commons.tools.outage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by harchen on 2015/12/7.
 */
public class LockReleaseGoodExample
{
    private Lock lock = new ReentrantLock();
    private Map<String, String> config = new HashMap<>();

    public void update( String key, String value )
    {
        try
        {
            lock.lock();
            if( config.get( key ) != null )
            {
                config.put( key, value );
            }
        }
        finally
        {
            lock.unlock();
        }

    }

    public void delete(String key)
    {
        lock.lock();
        try
        {
            if(config.get(key)!=null)
            {
                config.remove(key);
            }
        }finally {
            lock.unlock();
        }
    }
}
