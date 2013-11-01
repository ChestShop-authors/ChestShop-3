package com.Acrobot.ChestShop.DB;

import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.avaje.ebean.ExpressionList;
import org.bukkit.Material;

import javax.persistence.PersistenceException;
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

    private static double generateItemTotal(Material item, boolean bought, boolean sold) {
        List<Transaction> list;
        ExpressionList<Transaction> checkIf = ChestShop.getDB().find(Transaction.class).where();

        if (bought || sold) {
            list = checkIf.eq("buy", bought ? 1 : 0).eq("itemID", item.getId()).findList();
        } else {
            list = checkIf.eq("itemID", item.getId()).findList();
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

    private static double generateTotalBought(Material item) {
        return generateItemTotal(item, true, false);
    }

    private static double generateTotalSold(Material item) {
        return generateItemTotal(item, false, true);
    }

    private static double generateItemTotal(Material item) {
        return generateItemTotal(item, false, false);
    }

    private static float generateAveragePrice(Material item) {
        float price = 0;
        List<Transaction> prices = ChestShop.getDB().find(Transaction.class).where().eq("itemID", item.getId()).eq("buy", true).findList();

        for (Transaction t : prices) {
            price += t.getAveragePricePerItem();
        }

        float toReturn = price / prices.size();
        return (!Float.isNaN(toReturn) ? toReturn : 0);
    }

    private static float generateAverageBuyPrice(Material item) {
        return generateAveragePrice(item);
    }

    private static void generateItemStats(Material material) throws IOException {
        double total = generateItemTotal(material);

        if (total == 0) return;

        double bought = generateTotalBought(material);
        double sold = generateTotalSold(material);

        String matName = StringUtil.capitalizeFirstLetter(material.name(), '_');

        int maxStackSize = material.getMaxStackSize();

        float buyPrice = generateAverageBuyPrice(material);

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
                generateItemStats(m);
            }

            buf.close();

            long generationTime = (System.currentTimeMillis() - genTime) / 1000;

            fileEnd(generationTime);
        } catch (Exception e) {
            ChestShop.getBukkitLogger().severe("Couldn't generate statistics page!");

            if (!(e instanceof PersistenceException)) {
                e.printStackTrace();
            }
        }
    }
}
