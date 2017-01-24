package com.nokia.oss.commons.tools.outage;
/**
 * Created by harchen on 8/27/2015.
 */
public class UploadBeanReadSourceWrapper
{
    public Source handleUploadRequest() throws SmirpRequestException
    {
        Source result = null;
        FutureTask task = new FutureTask( new Callable()
        {
            public Source call() throws Exception
            {
                return UploadBeanReadSourceWrapper.this.processor
                    .processUploadRequest( UploadBeanReadSourceWrapper.this.filter );
            }
        } );
        (new Thread( task )).start();
    }
}
