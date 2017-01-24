package com.nokia.oss.outage.core.report;

import java.io.Writer;

import com.nokia.oss.outage.core.bean.OutageRiskList;

/**
 * Created by harchen on 2015/10/12.
 */
public interface OutageRiskReporter
{
    void report(Writer writer, OutageRiskList risks);
}
