package com.nokia.oss.outage.core.bean;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by harchen on 2015/10/10.
 */
@XmlRootElement
public class RiskType
{
    private String id;
    private RiskCategory riskCategory;
    private RiskLevel riskLevel;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RiskCategory getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(RiskCategory riskCategory) {
        this.riskCategory = riskCategory;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals( Object o )
    {
        if( this == o )
            return true;
        if( !(o instanceof RiskType) )
            return false;
        RiskType riskType = (RiskType)o;
        return Objects.equals( getId(), riskType.getId() ) && Objects.equals( getRiskCategory(), riskType.getRiskCategory() ) &&
            Objects.equals( getRiskLevel(), riskType.getRiskLevel() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getId(), getRiskCategory(), getRiskLevel() );
    }
}
