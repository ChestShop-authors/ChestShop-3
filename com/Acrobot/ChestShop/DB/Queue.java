package com.Acrobot.ChestShop.DB;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Acrobot
 */
public class Queue implements Runnable {
    private static List<Transaction> queue = new LinkedList<Transaction>();

    public static void addToQueue(Transaction t) {
        queue.add(t);
    }

    public void run() {
        List<Transaction> toDelete = ChestShop.getDB().find(Transaction.class).where().lt("sec", System.currentTimeMillis() / 1000 - Config.getInteger(Property.RECORD_TIME_TO_LIVE)).findList();
        ChestShop.getDB().delete(toDelete);
        ChestShop.getDB().save(queue);
        queue.clear();
    }
}
