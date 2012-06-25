package com.Acrobot.ChestShop.DB;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;

import javax.persistence.OptimisticLockException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Acrobot
 */
public class Queue implements Runnable {
    private static final ConcurrentLinkedQueue<Transaction> queue = new ConcurrentLinkedQueue<Transaction>();

    public static void addToQueue(Transaction t) {
        queue.add(t);
    }

    public synchronized void run() {
        if (Config.getInteger(Property.RECORD_TIME_TO_LIVE) != -1)
            deleteOld();

        ChestShop.getDB().save(queue);
        queue.clear();
    }

    public synchronized static boolean deleteOld() {
        try {
            ChestShop.getDB().delete(getOld());
            return true;
        } catch (OptimisticLockException ex) {
            return false;
        }
    }

    public static List getOld() throws OptimisticLockException {
        return ChestShop
                .getDB()
                .find(Transaction.class)
                .where()
                .lt("sec", (System.currentTimeMillis() / 1000L) - Config.getInteger(Property.RECORD_TIME_TO_LIVE))
                .findList();
    }
}
