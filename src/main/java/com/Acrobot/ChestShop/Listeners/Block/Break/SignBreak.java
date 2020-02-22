package com.Acrobot.ChestShop.Listeners.Block.Break;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Listeners.Block.Break.Attached.PhysicsBreak;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.Acrobot.Breeze.Utils.BlockUtil.getAttachedBlock;
import static com.Acrobot.Breeze.Utils.BlockUtil.isSign;
import static com.Acrobot.ChestShop.Permission.OTHER_NAME_DESTROY;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;

/**
 * @author Acrobot
 */
public class SignBreak implements Listener {
    private static final BlockFace[] SIGN_CONNECTION_FACES = {BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP};
    public static final String METADATA_NAME = "shop_destroyer";

    public SignBreak() {
        try {
            Class.forName("com.destroystokyo.paper.event.block.BlockDestroyEvent");
            ChestShop.getPlugin().registerEvent((Listener) Class.forName("com.Acrobot.ChestShop.Listeners.Block.Break.Attached.PaperBlockDestroy").newInstance());
            ChestShop.getBukkitLogger().info("Using Paper's BlockDestroyEvent instead of the BlockPhysicsEvent!");
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            ChestShop.getPlugin().registerEvent(new PhysicsBreak());
        }
    }

    public static void handlePhysicsBreak(Block block) {
        if (!BlockUtil.isSign(block)) {
            return;
        }

        Sign sign = (Sign) block.getState();
        Block attachedBlock = BlockUtil.getAttachedBlock(sign);

        if (attachedBlock.getType() == Material.AIR && ChestShopSign.isValid(sign)) {
            sendShopDestroyedEvent(sign, block.hasMetadata(METADATA_NAME)
                    ? (Player) block.getMetadata(METADATA_NAME).get(0).value()
                    : null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onSignBreak(BlockBreakEvent event) {
        if (!canBlockBeBroken(event.getBlock(), event.getPlayer())) {
            event.setCancelled(true);
            if (isSign(event.getBlock())) {
                event.getBlock().getState().update();
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public static void onBrokenSign(BlockBreakEvent event) {
        if (ChestShopSign.isValid(event.getBlock())) {
            sendShopDestroyedEvent((Sign) event.getBlock().getState(), event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (!canBlockBeBroken(block, null)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onBlockPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (!canBlockBeBroken(block, null)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onExplosion(EntityExplodeEvent event) {
        if (event.blockList() == null || !Properties.USE_BUILT_IN_PROTECTION) {
            return;
        }

        for (Block block : event.blockList()) {
            if (!canBlockBeBroken(block, null)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onIgnite(BlockBurnEvent event) {
        if (!canBlockBeBroken(event.getBlock(), null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!canBlockBeBroken(event.getBlock(), null)) {
            event.setCancelled(true);
        }
    }

    public static boolean canBlockBeBroken(Block block, Player breaker) {
        List<Sign> attachedSigns = getAttachedSigns(block);
        List<Sign> brokenBlocks = new LinkedList<Sign>();

        boolean canBeBroken = true;

        for (Sign sign : attachedSigns) {

            if (!canBeBroken || !ChestShopSign.isValid(sign)) {
                continue;
            }

            if (Properties.TURN_OFF_SIGN_PROTECTION || canDestroyShop(breaker, sign.getLine(NAME_LINE))) {
                brokenBlocks.add(sign);
            } else {
                canBeBroken = false;
            }
        }

        if (!canBeBroken) {
            return false;
        }

        for (Sign sign : brokenBlocks) {
            sign.setMetadata(METADATA_NAME, new FixedMetadataValue(ChestShop.getPlugin(), breaker));
        }

        return true;
    }

    private static boolean canDestroyShop(Player player, String name) {
        return player != null && NameManager.canUseName(player, OTHER_NAME_DESTROY, name);
    }

    public static void sendShopDestroyedEvent(Sign sign, Player player) {
        Container connectedContainer = null;

        if (!ChestShopSign.isAdminShop(sign)) {
            connectedContainer = uBlock.findConnectedContainer(sign.getBlock());
        }

        Event event = new ShopDestroyedEvent(player, sign, connectedContainer);
        ChestShop.callEvent(event);
    }

    private static List<Sign> getAttachedSigns(Block block) {
        if (block == null) {
            return new ArrayList<>();
        }

        if (isSign(block)) {
            return Collections.singletonList((Sign) block.getState());
        } else {
            List<Sign> attachedSigns = new LinkedList<Sign>();

            for (BlockFace face : SIGN_CONNECTION_FACES) {
                Block relative = block.getRelative(face);

                if (!isSign(relative)) {
                    continue;
                }

                Sign sign = (Sign) relative.getState();

                if (getAttachedBlock(sign).equals(block)) {
                    attachedSigns.add(sign);
                }
            }

            return attachedSigns;
        }
    }
}
