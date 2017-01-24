package com.nokia.oss.outage.core.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by harchen on 2016/6/2.
 */
@XmlRootElement
public class OutageRiskList
{
    @XmlElementWrapper( name = "risks" )
    @XmlElementRef( )
    private List<OutageRisk> risks;

    public OutageRiskList()
    {
        risks = new ArrayList<OutageRisk>();
    }

    private void add( OutageRisk risk )
    {
        risks.add( risk );
    }

    public void addAll( Collection<OutageRisk> riskCollection )
    {
        risks.addAll( riskCollection );
    }

    public List<OutageRisk> getRisks()
    {
        return risks;
    }

    public void sort()
    {
        Collections.sort( risks );
    }

    public int count()
    {
        return risks.size();
    }

    public int countByLevel( RiskLevel level )
    {
        int count = 0;
        for( OutageRisk risk : risks )
        {
            if( risk.getType().getRiskLevel() == level )
            {
                count++;
            }
        }
        return count;
    }
}
