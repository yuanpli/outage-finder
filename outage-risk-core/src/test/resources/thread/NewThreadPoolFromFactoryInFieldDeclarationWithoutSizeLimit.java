package com.nokia.oss.commons.tools.outage;

import java.util.concurrent.*;

/**
 * Created by harchen on 2015/12/3.
 */
public class NewThreadPoolFromFactoryInFieldDeclarationWithoutSizeLimit
{
    private ExecutorService pool = Executors.newCachedThreadPool();
}
