package com.nokia.oss.outage.core.rule;

import java.io.File;
import java.util.List;

import com.nokia.oss.outage.core.bean.OutageRisk;

/**
 * Created by harchen on 2015/10/12.
 */
public interface OutageRiskRule {
    List<OutageRisk> check(File source);
}
