package com.nokia.oss.outage.sonar;

import com.google.common.collect.ImmutableList;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

import java.util.List;

/**
 * Created by harchen on 2016/6/1.
 */
public class SonarOutagePlugin
    extends SonarPlugin
{
    @Override
    public List getExtensions()
    {
        ImmutableList.Builder<Object> extensions = ImmutableList.builder();
        extensions.add( OutageMetrics.class );
        extensions.add( OutageRulesDefinition.class );
        extensions.add( OutageSensor.class );
        return extensions.build();
    }
}
