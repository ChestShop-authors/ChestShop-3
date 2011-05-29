package com.Acrobot.ChestShop.Messaging;

import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class Message {
    public static void sendMsg(Player player, String msg) {
        player.sendMessage(msg);
    }
}
