import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.nsn.oss.nbi.lic.cm.notification.model.CMObjectEvent;


public class MoRedundancyCache
{

    private static final Logger LOGGER = Logger.getLogger( MoRedundancyCache.class );

    private ConcurrentHashMap<String, CMObjectEvent> cachedMap = new ConcurrentHashMap<String, CMObjectEvent>();
    private LinkedBlockingQueue<CMObjectEvent> mapList = new LinkedBlockingQueue<CMObjectEvent>();

    public void addToCache( CMObjectEvent event )
    {
        if( cachedMap.get( event.getDn() ) == null )
        {
            CMObjectEvent eventtmp = mapList.poll();
            cachedMap.remove( eventtmp.getDn() );
            cachedMap.put( event.getDn(), event );
            mapList.offer( event );
        }
    }
}
