package com.Acrobot.ChestShop;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.NameUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.Protection.ProtectBlockEvent;
import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
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

    public static boolean protect(Player player, Block block) {
        ProtectBlockEvent event = new ProtectBlockEvent(block, player);
        ChestShop.callEvent(event);

        return event.isProtected();
    }

    public static boolean canAccess(Player player, Block block) {
        return canAccess(player, block, false);
    }

    public static boolean canAccess(Player player, Block block, boolean ignoreDefaultProtection) {
        ProtectionCheckEvent event = new ProtectionCheckEvent(block, player, ignoreDefaultProtection);
        ChestShop.callEvent(event);

        return event.getResult() != Event.Result.DENY;
    }

    public static boolean canPlaceSign(Player player, Sign sign) {
        Block baseBlock = BlockUtil.getAttachedBlock(sign);

        if (!Properties.ALLOW_MULTIPLE_SHOPS_AT_ONE_BLOCK && anotherShopFound(baseBlock, sign.getBlock(), player)) {
            return false;
        }

        return canBePlaced(player, sign.getBlock());
    }

    private static boolean canBePlaced(Player player, Block sign) {
        for (BlockFace face : BLOCKS_AROUND) {
            Block block = sign.getRelative(face);

            if (!BlockUtil.isChest(block)) {
                continue;
            }
            if (!canAccess(player, block)) {
                return false;
            }
        }

        return true;
    }

    private static boolean anotherShopFound(Block baseBlock, Block signBlock, Player player) {
        String playerName = NameManager.getUsername(player.getUniqueId());
        String shortName = NameUtil.stripUsername(playerName);

        for (BlockFace face : SIGN_CONNECTION_FACES) {
            Block block = baseBlock.getRelative(face);

            if (block.equals(signBlock) || !BlockUtil.isSign(block)) {
                continue;
            }

            Sign sign = (Sign) block.getState();

            if (!ChestShopSign.isValid(sign) || !BlockUtil.getAttachedBlock(sign).equals(baseBlock)) {
                continue;
            }

            if (!sign.getLine(ChestShopSign.NAME_LINE).equals(shortName)) {
                return true;
            }
        }
        return false;
    }
}
