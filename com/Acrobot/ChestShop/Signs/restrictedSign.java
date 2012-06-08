package com.Acrobot.ChestShop.Signs;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Permission;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * @author Acrobot
 */
public class RestrictedSign implements Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public static void onSignChange(SignChangeEvent event) {
        String[] lines = event.getLines();
        Player player = event.getPlayer();

        if (isRestricted(lines)) {
            if (!hasPermission(player, lines)) {
                player.sendMessage(Config.getLocal(Language.ACCESS_DENIED));
                dropSignAndCancelEvent(event);
                return;
            }
            Block connectedSign = event.getBlock().getRelative(BlockFace.DOWN);

            if (!Permission.has(player, Permission.ADMIN) || !BlockUtil.isSign(connectedSign) || !ChestShopSign.isValid(connectedSign)) {
                dropSignAndCancelEvent(event);
                return;
            }

            Sign sign = (Sign) connectedSign.getState();

            if (!ChestShopSign.isValid(sign) || !ChestShopSign.canAccess(player, sign)) {
                dropSignAndCancelEvent(event);
            }
        }
    }
    public static boolean isRestrictedShop(Sign sign) {
        Block blockUp = sign.getBlock().getRelative(BlockFace.UP);
        return BlockUtil.isSign(blockUp) && isRestricted(((Sign) blockUp.getState()).getLines());
    }

    public static boolean isRestricted(String[] lines) {
        return lines[0].equalsIgnoreCase("[restricted]");
    }

    public static boolean isRestricted(Sign sign) {
        return sign.getLine(0).equalsIgnoreCase("[restricted]");
    }

    public static boolean canAccess(Sign sign, Player player) {
        Block blockUp = sign.getBlock().getRelative(BlockFace.UP);
        return !BlockUtil.isSign(blockUp) || hasPermission(player, ((Sign) blockUp.getState()).getLines());

    }

    public static boolean canDestroy(Player p, Sign sign) {
        Sign shopSign = getAssociatedSign(sign);
        return ChestShopSign.canAccess(p, shopSign);
    }

    public static Sign getAssociatedSign(Sign restricted) {
        Block down = restricted.getBlock().getRelative(BlockFace.DOWN);
        return BlockUtil.isSign(down) ? (Sign) down.getState() : null;
    }

    public static boolean hasPermission(Player p, String[] lines) {
        if (Permission.has(p, Permission.ADMIN)) {
            return true;
        }

        for (String line : lines) {
            if (p.hasPermission(Permission.GROUP.toString() + line)) {
                return true;
            }
        }
        return false;
    }

    private static void dropSignAndCancelEvent(SignChangeEvent event) {
        event.getBlock().breakNaturally();
        event.setCancelled(true);
    }
}
