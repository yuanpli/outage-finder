package com.nokia.oss.outage.sonar;

import com.google.common.collect.ImmutableList;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.List;

/**
 * Created by harchen on 2016/6/1.
 */
public class OutageMetrics
    implements Metrics
{

    public static final Metric OUTAGE_RISKS_TOTAL = new Metric.Builder(
        Constants.OUTAGE_RISKS_TOTAL, "Outage Issues", Metric.ValueType.INT )
        .setDescription( "Number of Outage Issues" ).setDirection( Metric.DIRECTION_WORST ).setQualitative( true )
        .setDomain( Constants.ISSUES_DOMAIN ).create();

    public static final Metric OUTAGE_RISKS_BLOCKER = new Metric.Builder(
        Constants.OUTAGE_RISKS_BLOCKER, "Outage Issues with BLOCKER severity", Metric.ValueType.INT )
        .setDescription( "Number of Outage Issues with BLOCKER severity" ).setDirection( Metric.DIRECTION_WORST )
        .setQualitative( true ).setDomain( Constants.ISSUES_DOMAIN ).create();

    public static final Metric OUTAGE_RISKS_CRITICAL = new Metric.Builder(
        Constants.OUTAGE_RISKS_CRITICAL, "Outage Issues with CRITICAL severity", Metric.ValueType.INT )
        .setDescription( "Number of Outage Issues with CRITICAL severity" ).setDirection( Metric.DIRECTION_WORST )
        .setQualitative( true ).setDomain( Constants.ISSUES_DOMAIN ).create();

    public static final Metric OUTAGE_RISKS_MAJOR = new Metric.Builder(
        Constants.OUTAGE_RISKS_MAJOR, "Outage Issues with MAJOR severity", Metric.ValueType.INT )
        .setDescription( "Number of Outage Issues with MAJOR severity" ).setDirection( Metric.DIRECTION_WORST )
        .setQualitative( true ).setDomain( Constants.ISSUES_DOMAIN ).create();

    public static final Metric OUTAGE_RISKS_MINOR = new Metric.Builder(
        Constants.OUTAGE_RISKS_MINOR, "Outage Issues with MINOR severity", Metric.ValueType.INT )
        .setDescription( "Number of Outage Issues with MINOR severity" ).setDirection( Metric.DIRECTION_WORST )
        .setQualitative( true ).setDomain( Constants.ISSUES_DOMAIN ).create();

    public static final Metric OUTAGE_RISKS_INFO = new Metric.Builder(
        Constants.OUTAGE_RISKS_INFO, "Outage Issues with INFO severity", Metric.ValueType.INT )
        .setDescription( "Number of Outage Issues with INFO severity" ).setDirection( Metric.DIRECTION_WORST )
        .setQualitative( true ).setDomain( Constants.ISSUES_DOMAIN ).create();

    @Override
    public List<Metric> getMetrics()
    {
        return ImmutableList.of(
            OUTAGE_RISKS_TOTAL, OUTAGE_RISKS_BLOCKER, OUTAGE_RISKS_CRITICAL, OUTAGE_RISKS_MAJOR, OUTAGE_RISKS_MINOR,
                OUTAGE_RISKS_INFO);
    }
}
