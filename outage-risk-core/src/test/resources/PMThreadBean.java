public class PMThreadBean
    extends Thread
{
    public void run()
    {
        Object lock=new Object();

        while( true )
        {
            try
            {
                Omes1DataVO omes1DataVO = null;

                synchronized( lock )
                {
                    if( omes1DataVOList.size() > 0 )
                    {
                        LOGGER.info( "PM files queue size:" + omes1DataVOList.size() );
                        omes1DataVO = omes1DataVOList.get( 0 );
                        omes1DataVOList.remove( 0 );
                    }
                }

                try
                {
                    Thread.sleep( 50 );
                }
                catch( InterruptedException e )
                {
                    LOGGER.error( "Could not Sleep for PM Thread:", e );
                }

            }
            catch( Exception e )
            {
                LOGGER.error( "Error found:", e );
            }
        }
    }
}
