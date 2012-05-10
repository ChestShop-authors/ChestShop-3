package com.Acrobot.ChestShop;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Events.ProtectBlockEvent;
import com.Acrobot.ChestShop.Events.ProtectionCheckEvent;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uName;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * @author Acrobot
 */
public class Security {
    private static final BlockFace[] faces = {BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
    private static final BlockFace[] blockFaces = {BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};

    public static boolean protect(String playerName, Block block) {
        ProtectBlockEvent event = new ProtectBlockEvent(block, playerName);
        ChestShop.callEvent(event);

        return event.isProtected();
    }

    public static boolean canAccess(Player player, Block block) {
        ProtectionCheckEvent event = new ProtectionCheckEvent(block, player);
        ChestShop.callEvent(event);

        return event.getResult() != Event.Result.DENY;
    }

    public static boolean canPlaceSign(Player p, Sign sign) {
        return !anotherShopFound(uBlock.getAttachedFace(sign), sign.getBlock(), p) && canBePlaced(p, sign.getBlock());
    }

    private static boolean canBePlaced(Player player, Block signBlock) {
        for (BlockFace bf : blockFaces) {
            Block block = signBlock.getRelative(bf);

            if (block.getType() != Material.CHEST) {
                continue;
            }
            if (!canAccess(player, block)) {
                return false;
            }
        }

        return true;
    }

    private static boolean anotherShopFound(Block baseBlock, Block signBlock, Player p) {
        String shortName = uName.stripName(p.getName());
        if (Config.getBoolean(Property.ALLOW_MULTIPLE_SHOPS_AT_ONE_BLOCK)) return false;

        for (BlockFace bf : faces) {
            Block block = baseBlock.getRelative(bf);

            if (!uSign.isSign(block)) continue;

            Sign s = (Sign) block.getState();
            if (uSign.isValid(s) && !block.equals(signBlock) && uBlock.getAttachedFace(s).equals(baseBlock) && !s.getLine(0).equals(shortName))
                return true;
        }
        return false;
    }
}
