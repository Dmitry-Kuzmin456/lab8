package org.server.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Класс, хранящий историю запросов
 */
public class History {
    private static Queue<String> history = new LinkedList<>();
    private final static ReadWriteLock lock = new ReentrantReadWriteLock();

    public static void add(String data){
        lock.writeLock().lock();
        history.add(data);
        if (history.size() > 12){
            history.remove();
        }
        lock.writeLock().unlock();
    }

    public static String getHistory(){
        lock.readLock().lock();
        StringBuilder history = new StringBuilder();
        short k = 0;
        for (String s : History.history){
            k++;
            history.append(k).append(": ").append(s).append(" ").append("\n");
        }
        try{
            return history.toString();
        } finally {
            lock.readLock().unlock();
        }

    }

}
