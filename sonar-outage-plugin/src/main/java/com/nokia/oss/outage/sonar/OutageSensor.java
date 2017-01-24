package com.nokia.oss.outage.sonar;

import com.google.common.collect.ImmutableList;
import com.nokia.oss.outage.core.OutageRiskFinder;
import com.nokia.oss.outage.core.RootConfig;
import com.nokia.oss.outage.core.bean.OutageRisk;
import com.nokia.oss.outage.core.bean.OutageRiskList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rule.RuleKey;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by harchen on 2016/6/1.
 */
public class OutageSensor
    implements Sensor
{
    private static final Logger LOG = LoggerFactory.getLogger( OutageSensor.class );
    private FileSystem fs;
    private ResourcePerspectives resourcePerspectives;
    private Map<Resource, OutageRiskStatistic> statistic = new HashMap<Resource, OutageRiskStatistic>();

    public OutageSensor( FileSystem fs, ResourcePerspectives resourcePerspectives )
    {
        this.fs = fs;
        this.resourcePerspectives = resourcePerspectives;
    }

    @Override
    public void analyse( Project module, SensorContext context )
    {
        LOG.info( "--------------------Outage Risk Scan Start--------------------" );
        try
        {
            OutageRiskList risks = findOutageRisks();
            if( risks != null )
            {
                processRisks( context, risks );
            }
        }
        catch( Exception e )
        {
            LOG.error( "Critical error happened when finding outage risks", e );
        }
        LOG.info( "--------------------Outage Risk Scan Finish--------------------" );
    }

    private OutageRiskList findOutageRisks()
    {
        OutageRiskList risks = null;
        AnnotationConfigApplicationContext appContext = null;
        try
        {
            Thread.currentThread().setContextClassLoader( OutageSensor.class.getClassLoader() );
            appContext = new AnnotationConfigApplicationContext( RootConfig.class );
            OutageRiskFinder finder = appContext.getBean( OutageRiskFinder.class );
            List<File> sourceDirectories = ImmutableList.of( getJavaSourceDir() );
            risks = finder.find( sourceDirectories );
        }
        catch( IOException e )
        {
            LOG.error( "Failed to scan the outage risk ", e );
        }
        finally
        {
            if( appContext != null )
            {
                appContext.close();
            }
        }
        return risks;
    }

    private File getJavaSourceDir()
    {
        return new File( new File( new File( fs.baseDir(), "src" ), "main" ), "java" );
    }

    private void processRisks( SensorContext context, OutageRiskList risks )
    {
        LOG.info( risks.count() + " risks found !" );
        for( OutageRisk risk : risks.getRisks() )
        {
            processRisk( context, risk );
        }
        saveMeasurement( context );
    }

    private void processRisk( SensorContext context, OutageRisk risk )
    {
        String path = null;
        try
        {
            path = risk.getPath();
            FilePredicate predicate = fs.predicates().hasAbsolutePath( path );
            if( predicate == null )
            {
                throw new IllegalStateException( "Failed to predicate relative path for " + path );
            }
            InputFile file = fs.inputFile( predicate );
            if( file == null )
            {
                throw new IllegalStateException( "Failed to get the input file predicate for " + path );
            }
            Resource resource = context.getResource( file );
            if( resource == null )
            {
                throw new IllegalStateException( "Failed to get resource file for input file " + file );
            }
            reportIssue( risk, resource );
            statistic( risk, resource );
        }
        catch( Exception e )
        {
            LOG.error( "Failed to report outage risk to sonar", e );
        }
    }

    private void saveMeasurement( SensorContext context )
    {
        for( Map.Entry<Resource, OutageRiskStatistic> entry : statistic.entrySet() )
        {
            Resource resource = entry.getKey();
            OutageRiskStatistic stats = entry.getValue();
            context.saveMeasure( resource, OutageMetrics.OUTAGE_RISKS_TOTAL, (double)stats.getTotal() );
            context.saveMeasure( resource, OutageMetrics.OUTAGE_RISKS_BLOCKER, (double)stats.getBlocker() );
            context.saveMeasure( resource, OutageMetrics.OUTAGE_RISKS_CRITICAL, (double)stats.getCritical() );
            context.saveMeasure( resource, OutageMetrics.OUTAGE_RISKS_MAJOR, (double)stats.getMajor() );
            context.saveMeasure( resource, OutageMetrics.OUTAGE_RISKS_MINOR, (double)stats.getMinor() );
            context.saveMeasure( resource, OutageMetrics.OUTAGE_RISKS_INFO, (double)stats.getInfo() );
        }
    }

    private void statistic( OutageRisk risk, Resource resource )
    {
        OutageRiskStatistic stats = statistic.get( resource );
        if( stats == null )
        {
            stats = new OutageRiskStatistic();
            statistic.put( resource, stats );
        }
        stats.statistic( risk );
    }

    private void reportIssue( OutageRisk risk, Resource resource )
    {
        Issuable issuable = resourcePerspectives.as( Issuable.class, resource );
        if( issuable != null )
        {
            Issue issue =
                issuable
                    .newIssueBuilder().ruleKey( RuleKey.of( Constants.REPOSITORY_KEY, risk.getType().getId() ) )
                    .line( risk.getRow() ).message( risk.getType().getDescription() )
                    .severity( risk.getType().getRiskLevel().toString() ).build();
            issuable.addIssue( issue );
        }
    }

    @Override
    public boolean shouldExecuteOnProject( Project project )
    {
        return fs.hasFiles( fs.predicates().hasLanguage( Constants.JAVA ) );
    }
}
