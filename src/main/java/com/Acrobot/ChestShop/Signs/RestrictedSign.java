package com.Acrobot.ChestShop.Signs;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Permission;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.SHOP_IS_RESTRICTED;
import static com.Acrobot.ChestShop.Permission.ADMIN;

/**
 * @author Acrobot
 */
public class RestrictedSign implements Listener {
    private static final BlockFace[] SIGN_CONNECTION_FACES = {BlockFace.SELF, BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};

    @EventHandler(ignoreCancelled = true)
    public static void onBlockDestroy(BlockBreakEvent event) {
        Block destroyed = event.getBlock();
        Sign attachedRestrictedSign = getRestrictedSign(destroyed.getLocation());

        if (attachedRestrictedSign == null) {
            return;
        }

        if (!canDestroy(event.getPlayer(), attachedRestrictedSign)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public static void onSignChange(SignChangeEvent event) {
        String[] lines = event.getLines();
        Player player = event.getPlayer();

        if (isRestricted(lines)) {
            if (!hasPermission(player, lines)) {
                Messages.ACCESS_DENIED.sendWithPrefix(player);
                dropSignAndCancelEvent(event);
                return;
            }
            Block connectedSign = event.getBlock().getRelative(BlockFace.DOWN);

            if (!Permission.has(player, ADMIN) || !ChestShopSign.isValid(connectedSign)) {
                dropSignAndCancelEvent(event);
                return;
            }

            Sign sign = (Sign) connectedSign.getState();

            if (!ChestShopSign.hasPermission(player, Permission.OTHER_NAME_DESTROY, sign)) {
                dropSignAndCancelEvent(event);
                return;
            }

            Messages.RESTRICTED_SIGN_CREATED.sendWithPrefix(player);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public static void onPreTransaction(PreTransactionEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Sign sign = event.getSign();

        if (isRestrictedShop(sign) && !canAccess(sign, event.getClient())) {
            event.setCancelled(SHOP_IS_RESTRICTED);
        }
    }

    public static Sign getRestrictedSign(Location location) {
        Block currentBlock = location.getBlock();

        if (BlockUtil.isSign(currentBlock)) {
            Sign sign = (Sign) currentBlock.getState();

            if (isRestricted(sign)) {
                return sign;
            } else {
                return null;
            }
        }

        for (BlockFace face : SIGN_CONNECTION_FACES) {
            Block relative = currentBlock.getRelative(face);

            if (!BlockUtil.isSign(relative)) {
                continue;
            }

            Sign sign = (Sign) relative.getState();

            if (!BlockUtil.getAttachedBlock(sign).equals(currentBlock)) {
                continue;
            }

            if (isRestricted(sign)) {
                return sign;
            }
        }

        return null; //No sign found
    }

    public static boolean isRestrictedShop(Sign sign) {
        Block blockUp = sign.getBlock().getRelative(BlockFace.UP);
        return BlockUtil.isSign(blockUp) && isRestricted(((Sign) blockUp.getState()).getLines());
    }

    public static boolean isRestricted(String[] lines) {
        return lines[0].equalsIgnoreCase("[restricted]");
    }

    public static boolean isRestricted(Sign sign) {
        return isRestricted(sign.getLines());
    }

    public static boolean canAccess(Sign sign, Player player) {
        Block blockUp = sign.getBlock().getRelative(BlockFace.UP);
        return !BlockUtil.isSign(blockUp) || hasPermission(player, ((Sign) blockUp.getState()).getLines());
    }

    public static boolean canDestroy(Player player, Sign sign) {
        Sign shopSign = getAssociatedSign(sign);
        return ChestShopSign.hasPermission(player, Permission.OTHER_NAME_DESTROY, shopSign);
    }

    public static Sign getAssociatedSign(Sign restricted) {
        Block down = restricted.getBlock().getRelative(BlockFace.DOWN);
        return BlockUtil.isSign(down) ? (Sign) down.getState() : null;
    }

    public static boolean hasPermission(Player p, String[] lines) {
        if (Permission.has(p, ADMIN)) {
            return true;
        }

        for (String line : lines) {
            if (Permission.has(p, Permission.GROUP + line)) {
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
