package com.nokia.oss.outage.core.bean;

/**
 * Created by harchen on 2015/10/12.
 */
public class OutageRiskBuilder
{
    private OutageRisk risk = new OutageRisk();

    public OutageRiskBuilder path( String path )
    {
        risk.setPath( path );
        return this;
    }

    public OutageRiskBuilder className( String className )
    {
        risk.setClassName( className );
        return this;
    }

    public OutageRiskBuilder type( RiskType type )
    {
        risk.setType( type );
        return this;
    }

    public OutageRiskBuilder row( int row )
    {
        risk.setRow( row );
        return this;
    }

    public OutageRiskBuilder sample( String sample )
    {
        risk.setSample( sample );
        return this;
    }

    public OutageRisk build()
    {
        return risk;
    }
}
