package com.nokia.oss.commons.tools.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by harchen on 2015/12/24.
 */
public class NestedSynchronization
{
    private Object keyLock = new Object();
    private Object valueLock = new Object();

    private Set<String> keys = new HashSet<>();
    private Map<String, Set<String>> values = new HashMap<>();

    public void remove( String key, String value )
    {
        synchronized( keyLock )
        {
            if( keys.contains( key ) )
            {
                synchronized( valueLock )
                {
                    values.get( key ).remove( value );
                }
            }
        }
    }
}
