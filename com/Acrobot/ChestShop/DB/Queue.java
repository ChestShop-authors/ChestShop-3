package com.Acrobot.ChestShop.DB;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;

import java.util.ArrayList;

/**
 * @author Acrobot
 */
public class Queue implements Runnable {
    private static final ArrayList<Transaction> queue = new ArrayList<Transaction>();

    public static void addToQueue(Transaction t) {
        queue.add(t);
    }

    public void run() {
        ChestShop.getDB().delete(ChestShop.getDB().find(Transaction.class).where().lt("sec", System.currentTimeMillis() / 1000 - Config.getInteger(Property.RECORD_TIME_TO_LIVE)).findList());

        ChestShop.getDB().save(queue);
        queue.clear();
    }
}
