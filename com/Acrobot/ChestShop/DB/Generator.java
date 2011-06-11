package com.Acrobot.ChestShop.DB;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Logging.Logging;
import org.bukkit.Material;

import java.io.*;
import java.util.List;

/**
 * @author Acrobot
 */
public class Generator implements Runnable {
    private static String filePath = Config.getString(Property.STATISTICS_PAGE_PATH);

    private static double generationTime;

    private static String header = fileToString("header");
    private static String row = fileToString("row");
    private static String footer = fileToString("footer");

    private static BufferedWriter buf;

    public void run() {
        generateStats();
    }

    public static void fileStart() throws IOException {
        FileWriter fw = new FileWriter(filePath);
        fw.write(header);
        fw.close();
    }

    public static void fileEnd() throws IOException {
        FileWriter fw = new FileWriter(filePath, true);
        fw.write(footer.replace("%time", String.valueOf(generationTime)));
        fw.close();
    }

    public static String fileToString(String fileName) {
        try {
            File f = new File(ChestShop.folder + "/HTML/" + fileName + ".html");
            FileReader rd = new FileReader(f);
            char[] buf = new char[(int) f.length()];
            rd.read(buf);
            return new String(buf);
        } catch (Exception e) {
            return "";
        }
    }

    public static double generateItemTotal(int itemID, boolean bought, boolean sold) {
        double amount = 0;
        List<Transaction> list;
        if (bought) {
            list = ChestShop.getDB().find(Transaction.class).where().eq("buy", 1).eq("itemID", itemID).findList();
        } else if (sold) {
            list = ChestShop.getDB().find(Transaction.class).where().eq("buy", 0).eq("itemID", itemID).findList();
        } else {
            list = ChestShop.getDB().find(Transaction.class).where().eq("itemID", itemID).findList();
        }
        for (Transaction t : list) {
            amount += t.getAmount();
        }
        return amount;
    }

    public static double generateTotalBought(int itemID) {
        return generateItemTotal(itemID, true, false);
    }

    public static double generateTotalSold(int itemID) {
        return generateItemTotal(itemID, false, true);
    }

    public static double generateItemTotal(int itemID) {
        return generateItemTotal(itemID, false, false);
    }

    public static float generateAveragePrice(int itemID, boolean buy) {
        float price = 0;
        List<Transaction> prices = ChestShop.getDB().find(Transaction.class).where().eq("itemID", itemID).eq("buy", buy).findList();
        for (Transaction t : prices) {
            price += t.getAveragePricePerItem();
        }
        float toReturn = price / prices.size();
        return (!Float.isNaN(toReturn) ? toReturn : 0);
    }

    /*public static float generateAverageSellPrice(int itemID){
        return generateAveragePrice(itemID, false);
    }*/

    public static float generateAverageBuyPrice(int itemID) {
        return generateAveragePrice(itemID, true);
    }

    public static void generateItemStats(int itemID) throws IOException {
        double total = generateItemTotal(itemID);

        if (total == 0) {
            return;
        }

        double bought = generateTotalBought(itemID);
        double sold = generateTotalSold(itemID);

        Material material = Material.getMaterial(itemID);
        String matName = material.name().replace("_", " ").toLowerCase();

        int maxStackSize = material.getMaxStackSize();

        float buyPrice = generateAverageBuyPrice(itemID);

        buf.write(row.replace("%material", matName)
                .replace("%total", String.valueOf(total))
                .replace("%bought", String.valueOf(bought))
                .replace("%sold", String.valueOf(sold))
                .replace("%maxStackSize", String.valueOf(maxStackSize))
                .replace("%pricePerStack", String.valueOf((buyPrice * maxStackSize)))
                .replace("%pricePerItem", String.valueOf(buyPrice)));
    }

    public static void generateStats() {
        try {
            fileStart();

            buf = new BufferedWriter(new FileWriter(filePath, true));

            long genTime = System.currentTimeMillis();

            for (Material m : Material.values()) {
                generateItemStats(m.getId());
            }
            buf.close();

            generationTime = (System.currentTimeMillis() - genTime) / 1000;
            fileEnd();
        } catch (Exception e) {
            Logging.log("Couldn't generate statistics page!");
        }
    }
}
