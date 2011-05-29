package com.Acrobot.ChestShop.DB;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Utils.Config;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Acrobot
 */
public class Queue implements Runnable {
    private static List<Transaction> queue = new LinkedList<Transaction>();

    public static void addToQueue(Transaction t){
        queue.add(t);
    }

    public static void saveQueue() {
        ChestShop.getBukkitServer().getScheduler().scheduleAsyncDelayedTask(new ChestShop(), new Queue());
        ChestShop.getBukkitServer().broadcastMessage("Successfully saved queue!");
    }

    public void run() {
        List<Transaction> toDelete = ChestShop.getDB().find(Transaction.class).where().lt("sec", System.currentTimeMillis()/1000 - Config.getInteger("DBtimeToLive")).findList();
        ChestShop.getDB().delete(toDelete);
        ChestShop.getDB().save(queue);
        queue.clear();
    }
}
