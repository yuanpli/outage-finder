package com.nokia.oss.outage.core.bean;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by harchen on 2015/10/10.
 */
@XmlRootElement
public class OutageRisk
    implements Comparable<OutageRisk>
{

    private String id;
    private String path;
    private String className;
    private RiskType type;
    private int row;
    private String sample;

    public String getPath()
    {
        return path;
    }

    public void setPath( String path )
    {
        this.path = path;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName( String className )
    {
        this.className = className;
    }

    public int getRow()
    {
        return row;
    }

    public void setRow( int row )
    {
        this.row = row;
    }

    public RiskType getType()
    {
        return type;
    }

    public void setType( RiskType type )
    {
        this.type = type;
    }

    public String getSample()
    {
        return sample;
    }

    public void setSample( String sample )
    {
        this.sample = sample;
    }

    @Override
    public boolean equals( Object o )
    {
        if( this == o )
            return true;
        if( !(o instanceof OutageRisk) )
            return false;
        OutageRisk that = (OutageRisk)o;
        return getRow() == that.getRow() && Objects.equals( id, that.id ) &&
            Objects.equals( getPath(), that.getPath() ) && Objects.equals( getClassName(), that.getClassName() ) &&
            Objects.equals( getType(), that.getType() ) && Objects.equals( getSample(), that.getSample() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, getPath(), getClassName(), getType(), getRow(), getSample() );
    }

    @Override
    public int compareTo( OutageRisk risk )
    {
        return this.getType().getRiskLevel().getLevel() - risk.getType().getRiskLevel().getLevel();
    }
}
