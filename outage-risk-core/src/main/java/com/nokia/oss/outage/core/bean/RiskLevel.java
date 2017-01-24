package com.nokia.oss.outage.core.bean;

/**
 * Created by harchen on 2016/3/29.
 */
public enum RiskLevel
{
    BLOCKER( 1 ), CRITICAL( 2 ), MAJOR( 3 ), MINOR( 4 ), INFO( 5 );

    private int level;

    RiskLevel( int level )
    {
        this.level = level;
    }

    public int getLevel()
    {
        return level;
    }
}
