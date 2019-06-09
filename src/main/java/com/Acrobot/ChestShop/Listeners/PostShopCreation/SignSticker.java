package com.Acrobot.ChestShop.Listeners.PostShopCreation;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
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

        if (ChestShopSign.isAdminShop(event.getSignLine(NAME_LINE))) {
            return;
        }

        stickSign(event.getSign().getBlock(), event.getSignLines());
    }

    private static void stickSign(Block signBlock, String[] lines) {
        if (!(signBlock.getBlockData() instanceof Sign)) {
            return;
        }

        BlockFace shopBlockFace = null;

        for (BlockFace face : uBlock.CHEST_EXTENSION_FACES) {
            if (uBlock.couldBeShopContainer(signBlock.getRelative(face))) {
                shopBlockFace = face;
                break;
            }
        }

        if (shopBlockFace == null) {
            return;
        }


        signBlock.setType(Material.WALL_SIGN);

        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signBlock.getState();

        WallSign signMaterial = (WallSign) Bukkit.createBlockData(Material.WALL_SIGN);
        signMaterial.setFacing(shopBlockFace.getOppositeFace());
        sign.setBlockData(signMaterial);

        for (int i = 0; i < lines.length; ++i) {
            sign.setLine(i, lines[i]);
        }

        sign.update(true);
    }
}
