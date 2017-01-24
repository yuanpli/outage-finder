package com.nokia.oss.outage.core.matcher;

import com.github.javaparser.ast.Node;

/**
 * Created by harchen on 2015/10/12.
 */
public interface NodeMatcher
{
    boolean match(Node node);
}
