package com.Acrobot.ChestShop.Listeners.PostShopCreation;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;

/**
 * @author Acrobot
 */
public class SignSticker implements Listener {

    @EventHandler
    public static void onShopCreation(ShopCreatedEvent event) {
        if (!Properties.STICK_SIGNS_TO_CHESTS) {
            return;
        }

        if (ChestShopSign.isAdminShop(event.getSign().getLine(NAME_LINE))) {
            return;
        }

        stickSign(event.getSign().getBlock(), event.getSignLines());
    }

    private static void stickSign(Block signBlock, String[] lines) {
        if (signBlock.getType() != Material.SIGN_POST) {
            return;
        }

        BlockFace chestFace = null;

        for (BlockFace face : uBlock.CHEST_EXTENSION_FACES) {
            if (BlockUtil.isChest(signBlock.getRelative(face))) {
                chestFace = face;
                break;
            }
        }

        if (chestFace == null) {
            return;
        }

        org.bukkit.material.Sign signMaterial = new org.bukkit.material.Sign(Material.WALL_SIGN);
        signMaterial.setFacingDirection(chestFace.getOppositeFace());

        signBlock.setType(Material.WALL_SIGN);
        signBlock.setData(signMaterial.getData());

        Sign sign = (Sign) signBlock.getState();

        for (int i = 0; i < lines.length; ++i) {
            sign.setLine(i, lines[i]);
        }

        sign.update(true);
    }
}
