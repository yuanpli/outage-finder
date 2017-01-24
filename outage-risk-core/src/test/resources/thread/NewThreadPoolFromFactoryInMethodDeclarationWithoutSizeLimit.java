package com.nokia.oss.commons.tools.outage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by harchen on 2015/12/3.
 */
public class NewThreadPoolFromFactoryInMethodDeclarationWithoutSizeLimit
{
    private ExecutorService pool = null;

    public void init()
    {
        pool = Executors.newCachedThreadPool();
    }
}
