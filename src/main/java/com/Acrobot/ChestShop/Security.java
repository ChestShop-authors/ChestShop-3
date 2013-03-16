package com.Acrobot.ChestShop;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.Protection.ProtectBlockEvent;
import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uName;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * @author Acrobot
 */
public class Security {
    private static final BlockFace[] SIGN_CONNECTION_FACES = {BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
    private static final BlockFace[] BLOCKS_AROUND = {BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};

    public static boolean protect(String playerName, Block block) {
        ProtectBlockEvent event = new ProtectBlockEvent(block, playerName);
        ChestShop.callEvent(event);

        return event.isProtected();
    }

    public static boolean canAccess(Player player, Block block) {
        return canAccess(player, block, false);
    }

    public static boolean canAccess(Player player, Block block, boolean ignoreDefault) {
        ProtectionCheckEvent event = new ProtectionCheckEvent(block, player, ignoreDefault);
        ChestShop.callEvent(event);

        return event.getResult() != Event.Result.DENY;
    }

    public static boolean canPlaceSign(Player p, Sign sign) {
        return !anotherShopFound(BlockUtil.getAttachedFace(sign), sign.getBlock(), p) && canBePlaced(p, sign.getBlock());
    }

    private static boolean canBePlaced(Player player, Block signBlock) {
        for (BlockFace face : BLOCKS_AROUND) {
            Block block = signBlock.getRelative(face);

            if (!BlockUtil.isChest(block)) {
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
        if (Properties.ALLOW_MULTIPLE_SHOPS_AT_ONE_BLOCK) return false;

        for (BlockFace bf : SIGN_CONNECTION_FACES) {
            Block block = baseBlock.getRelative(bf);

            if (!BlockUtil.isSign(block)) {
                continue;
            }

            Sign s = (Sign) block.getState();
            if (ChestShopSign.isValid(s) && !block.equals(signBlock) && BlockUtil.getAttachedFace(s).equals(baseBlock) && !s.getLine(0).equals(shortName))
                return true;
        }
        return false;
    }
}
