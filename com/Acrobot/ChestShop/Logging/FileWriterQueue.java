package com.Acrobot.ChestShop.Logging;

import com.Acrobot.ChestShop.ChestShop;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Acrobot
 */
public class FileWriterQueue implements Runnable {
    private static List<String> queue = new LinkedList<String>();
    public static String filePath = ChestShop.folder + "/ChestShop.log";

    public static void addToQueue(String message) {
        queue.add(message);
    }

    public void run() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));

            for (String msg : queue) {
                bw.write(msg);
                bw.newLine();
            }

            bw.close();

            queue.clear();
        } catch (Exception e) {
            Logging.log("Couldn't write to log file!");
        }
    }
}
