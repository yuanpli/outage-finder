package com.nokia.oss.commons.tools.outage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by harchen on 2015/12/7.
 */
public class UseNoneAtomicMethodOfThreadSafeCollection
{
    private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    public void update( String key, String value )
    {
        if( this.map.get( key ) != null )
        {
            map.put( key, value );
        }
    }

    public ConcurrentHashMap<String, String> getMap() {
        return map;
    }
}
