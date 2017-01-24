/**
 * Created by harchen on 8/27/2015.
 */
public class LogUploadCacheCleaner implements Runnable {

    private boolean running;

    @Override
    public void run()
    {
        LOG.activity("Starting the LogUploadCacheCleaner thread.");
        running = true;
        while( running )
        {
            try
            {

                LOG.debug("Cleaning thread removing old operations from LogUploadCache");
                try
                {
                    cache.cleanup();
                }
                catch( Exception e )
                {
                    LOG.error("Failed to clean up Log Upload Cache", e);
                }

                Thread.sleep(Config.getLong("com.nsn.oss.nwi3.logupload.cacheCleanerInterval", intervalMs));
            }
            catch( InterruptedException e )
            {
                LOG.debug("Cleaner thread interrupted");
                running = false;
            }
        }
        LOG.activity("Ending the LogUploadCacheCleaner thread.");
    }

    public void stop()
    {
        running = false;
    }
}
