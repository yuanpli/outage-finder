package com.nokia.oss.outage.sonar;

import com.nokia.oss.outage.core.bean.OutageRisk;

/**
 * Created by harchen on 2016/6/8.
 */
public class OutageRiskStatistic
{
    private int blocker;
    private int critical;
    private int major;
    private int minor;
    private int info;

    public int getTotal()
    {
        return blocker + critical + major + minor + info;
    }

    public int getBlocker()
    {
        return blocker;
    }

    public int getCritical()
    {
        return critical;
    }

    public int getMajor()
    {
        return major;
    }

    public int getMinor()
    {
        return minor;
    }

    public int getInfo()
    {
        return info;
    }

    public void statistic( OutageRisk risk )
    {
        switch( risk.getType().getRiskLevel() )
        {
            case BLOCKER:
            {
                blocker++;
                break;
            }
            case CRITICAL:
            {
                critical++;
                break;
            }
            case MAJOR:
            {
                major++;
                break;
            }
            case MINOR:
            {
                minor++;
                break;
            }
            case INFO:
            {
                info++;
                break;
            }
        }
    }
}
