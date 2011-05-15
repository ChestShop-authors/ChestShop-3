package com.Acrobot.iConomyChestShop.Utils;

import com.Acrobot.iConomyChestShop.Logging.Logging;
import org.bukkit.util.config.Configuration;

import java.io.File;

/**
 * @author Acrobot
 */
public class Config {
    private static File configFile = new File("plugins/iConomyChestShop/config.yml");
    private static Configuration config = new Configuration(configFile);



    public static void setUp(){
        if(!configFile.exists()){
            try {
                configFile.createNewFile();
                Logging.log("Successfully created blank configuration file");
            } catch (Exception e) {
                Logging.log("Couldn't create configuration file!");
            }
        }
        load();
    }

    public static void load(){
        config.load();
    }

    public static boolean getBoolean(String node){
        return config.getBoolean(node, false);
    }

    public static int getInteger(String node){
        return config.getInt(node, 0);
    }

    public static double getDouble(String node){
        return config.getDouble(node, -1);
    }
}
