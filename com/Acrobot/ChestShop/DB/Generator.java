package com.Acrobot.ChestShop.DB;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Logging.Logging;
import com.Acrobot.ChestShop.Utils.uSign;
import com.avaje.ebean.ExpressionList;
import org.bukkit.Material;

import java.io.*;
import java.util.List;

/**
 * @author Acrobot
 */
public class Generator implements Runnable {
    private final File pagePath;

    private static String header;
    private static String row;
    private static String footer;

    private static BufferedWriter buf;


    public Generator(File pagePath) {
        this.pagePath = pagePath;
    }

    public void run() {
        header = fileToString("header");
        row = fileToString("row");
        footer = fileToString("footer");

        if (row.isEmpty()) {
            ChestShop.getBukkitLogger().severe("You lack the necessary HTML files in your plugins/ChestShop/HTML folder!");
            return;
        }

        generateStats();
    }

    private void fileStart() throws IOException {
        FileWriter fw = new FileWriter(pagePath);
        fw.write(header);
        fw.close();
    }

    private void fileEnd(long generationTime) throws IOException {
        FileWriter fw = new FileWriter(pagePath, true);
        fw.write(footer.replace("%time", String.valueOf(generationTime)));
        fw.close();
    }

    private static String fileToString(String fileName) {
        try {
            File htmlFolder = new File(ChestShop.getFolder(), "HTML");
            File fileToRead = new File(htmlFolder, fileName + ".html");

            FileReader rd = new FileReader(fileToRead);
            char[] buf = new char[(int) fileToRead.length()];
            rd.read(buf);
            return new String(buf);
        } catch (Exception e) {
            return "";
        }
    }

    private static double generateItemTotal(int itemID, boolean bought, boolean sold) {
        List<Transaction> list;
        ExpressionList<Transaction> checkIf = ChestShop.getDB().find(Transaction.class).where();

        if (bought || sold) {
            list = checkIf.eq("buy", bought ? 1 : 0).eq("itemID", itemID).findList();
        } else {
            list = checkIf.eq("itemID", itemID).findList();
        }

        return countTransactionAmount(list);
    }

    private static double countTransactionAmount(List<Transaction> list) {
        double amount = 0;

        for (Transaction transaction : list) {
            amount += transaction.getAmount();
        }

        return amount;
    }

    private static double generateTotalBought(int itemID) {
        return generateItemTotal(itemID, true, false);
    }

    private static double generateTotalSold(int itemID) {
        return generateItemTotal(itemID, false, true);
    }

    private static double generateItemTotal(int itemID) {
        return generateItemTotal(itemID, false, false);
    }

    private static float generateAveragePrice(int itemID) {
        float price = 0;
        List<Transaction> prices = ChestShop.getDB().find(Transaction.class).where().eq("itemID", itemID).eq("buy", true).findList();

        for (Transaction t : prices) {
            price += t.getAveragePricePerItem();
        }

        float toReturn = price / prices.size();
        return (!Float.isNaN(toReturn) ? toReturn : 0);
    }

    private static float generateAverageBuyPrice(int itemID) {
        return generateAveragePrice(itemID);
    }

    private static void generateItemStats(int itemID) throws IOException {
        double total = generateItemTotal(itemID);

        if (total == 0) return;

        double bought = generateTotalBought(itemID);
        double sold = generateTotalSold(itemID);

        Material material = Material.getMaterial(itemID);
        String matName = uSign.capitalizeFirstLetter(material.name(), '_');

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

    private void generateStats() {
        try {
            File parentFolder = pagePath.getParentFile();
            if (!parentFolder.exists()) {
                parentFolder.mkdir();
            }

            fileStart();

            buf = new BufferedWriter(new FileWriter(pagePath, true));

            long genTime = System.currentTimeMillis();
            for (Material m : Material.values()) {
                generateItemStats(m.getId());
            }

            buf.close();

            long generationTime = (System.currentTimeMillis() - genTime) / 1000;

            fileEnd(generationTime);
        } catch (Exception e) {
            Logging.log("Couldn't generate statistics page!");
            e.printStackTrace();
        }
    }
}
