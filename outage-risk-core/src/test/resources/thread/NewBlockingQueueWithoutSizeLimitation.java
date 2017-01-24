package com.nokia.oss.commons.tools.outage;

import java.util.concurrent.*;

/**
 * Created by harchen on 2015/12/3.
 */
public class NewBlockingQueueWithoutSizeLimitation
{
    private ExecutorService pool = new ThreadPoolExecutor(
        5, 10, 100, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>() );

    public NewBlockingQueueWithoutSizeLimitation()
    {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    }

    public void init()
    {
        BlockingQueue myQueue=new LinkedBlockingDeque();
    }
}
