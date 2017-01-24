package com.nokia.oss.commons.tools.core;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by harchen on 2015/12/24.
 */
public class WaitAndNotifyExample2<T>
{
    private Queue<T> queue = new LinkedList<T>();
    private int capacity;
    private Object lock = new Object();

    public WaitAndNotifyExample2( int capacity )
    {
        this.capacity = capacity;
    }

    public void put( T element ) throws InterruptedException
    {
        synchronized( lock )
        {
            while( queue.size() == capacity )
            {
                wait();
            }

            queue.add( element );
            notify(); // notifyAll() for multiple producer/consumer threads
        }
    }

    public T take() throws InterruptedException
    {
        synchronized( lock )
        {
            while( queue.isEmpty() )
            {
                wait();
            }
            T item = queue.remove();
            notify(); // notifyAll() for multiple producer/consumer threads
            return item;
        }
    }
}
