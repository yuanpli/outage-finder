package com.nokia.oss.commons.tools.outage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * Date: 6/17/15
 * Time: 8:52 AM
 */
public class KoalaFileTube {


    public static final int TUBE_SIZE = 50;

    public BlockingQueue<String> getAdapIdQueue() {
        return adapIdQueue;
    }


    HashMap<String, ArrayList<String>> getMap() {
        return map;
    }

    private BlockingQueue<String> adapIdQueue = new ArrayBlockingQueue<String>(TUBE_SIZE);
    private Lock locker = new ReentrantLock();
    private HashMap<String, ArrayList<String>> map = new HashMap<>();

    private static com.nsn.nac.dynamicadaptation.pdds.condense.KoalaFileTube instance = new com.nsn.nac.dynamicadaptation.pdds.condense.KoalaFileTube();

    public static com.nsn.nac.dynamicadaptation.pdds.condense.KoalaFileTube getInstance() {
        return instance;
    }

    public void put(String adapId, String koala) {
        boolean isNewId = false;
        locker.lock();
        ArrayList<String> lst = map.get(adapId);
        if (lst == null) {
            lst = new ArrayList<String>();
            lst.add(koala);
            map.put(adapId, lst);
            isNewId = true;
        } else {
            lst.add(koala);
        }

        locker.unlock();
        if (isNewId){
            adapIdQueue.add(adapId);
        }

    }

    /**
     *
     * @param timeWindowSeconds: several koala files of the same
     *                         adaptationId but different version
     *                         coming, it is better to process them
     *                         together. after the first koala comes,
     *                         this method takes timeWindowSeconds to
     *                         wait following koalas.
     * @return
     * @throws InterruptedException
     */
    public AdapIdKoalaList take(int timeWindowSeconds) throws InterruptedException {
        long window = timeWindowSeconds*1000;//5 sec
        String adapId = adapIdQueue.take();
        Thread.sleep(window);
        ArrayList<String> koalas = null;
        locker.lock();
        koalas = map.remove(adapId);
        locker.unlock();
        return new AdapIdKoalaList(adapId, koalas);
    }

}
