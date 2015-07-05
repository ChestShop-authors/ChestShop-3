package com.Acrobot.ChestShop.Listeners.Block.Break;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.material.Directional;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.*;

import static com.Acrobot.Breeze.Utils.BlockUtil.getAttachedBlock;
import static com.Acrobot.Breeze.Utils.BlockUtil.isSign;
import static com.Acrobot.ChestShop.Permission.ADMIN;
import static com.Acrobot.ChestShop.Permission.MOD;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;
import static com.Acrobot.ChestShop.UUIDs.NameManager.canUseName;

/**
 * @author Acrobot
 */
public class SignBreak implements Listener {
    private static final BlockFace[] SIGN_CONNECTION_FACES = {BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP};
    private static final String METADATA_NAME = "shop_destroyer";

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public static void onSign(BlockPhysicsEvent event) {
        Block block = event.getBlock();

        if (!BlockUtil.isSign(block)) {
            return;
        }

        Sign sign = (Sign) block.getState();
        Block attachedBlock = BlockUtil.getAttachedBlock(sign);

        if (attachedBlock.getType() == Material.AIR && ChestShopSign.isValid(sign)) {
            if (!block.hasMetadata(METADATA_NAME)) {
                return;
            }

            sendShopDestroyedEvent(sign, (Player) block.getMetadata(METADATA_NAME).get(0).value());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onSignBreak(BlockBreakEvent event) {
        if (!canBlockBeBroken(event.getBlock(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public static void onBrokenSign(BlockBreakEvent event) {
        if (ChestShopSign.isValid(event.getBlock()) && !event.isCancelled()) {
            sendShopDestroyedEvent((Sign) event.getBlock().getState(), event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : getExtendBlocks(event)) {
            if (!canBlockBeBroken(block, null)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (!canBlockBeBroken(getRetractBlock(event), null)) {
            event.setCancelled(true);
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

    public static boolean canBlockBeBroken(Block block, Player breaker) {
        List<Sign> attachedSigns = getAttachedSigns(block);
        List<Sign> brokenBlocks = new LinkedList<Sign>();

        boolean canBeBroken = true;

        for (Sign sign : attachedSigns) {
            sign.update();

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
        return player != null && (hasShopBreakingPermission(player) || canUseName(player, name));
    }

    private static boolean hasShopBreakingPermission(Player player) {
        return Permission.has(player, ADMIN) || Permission.has(player, MOD);
    }

    private static void sendShopDestroyedEvent(Sign sign, Player player) {
        Chest connectedChest = null;

        if (!ChestShopSign.isAdminShop(sign)) {
            connectedChest = uBlock.findConnectedChest(sign.getBlock());
        }

        Event event = new ShopDestroyedEvent(player, sign, connectedChest);
        ChestShop.callEvent(event);
    }

    private static List<Sign> getAttachedSigns(Block block) {
        if (block == null) {
            return Lists.newArrayList();
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

    private static Block getRetractBlock(BlockPistonRetractEvent event) {
        Block block = getRetractLocationBlock(event);
        return (block != null && !BlockUtil.isSign(block) ? block : null);
    }

    //Those are fixes for CraftBukkit's piston bug, where piston appears not to be a piston.
    private static BlockFace getPistonDirection(Block block) {
        return block.getState().getData() instanceof PistonBaseMaterial ? ((Directional) block.getState().getData()).getFacing() : null;
    }

    private static Block getRetractLocationBlock(BlockPistonRetractEvent event) {
        BlockFace pistonDirection = getPistonDirection(event.getBlock());
        return pistonDirection != null ? event.getBlock().getRelative((pistonDirection), 2).getLocation().getBlock() : null;
    }

    private static List<Block> getExtendBlocks(BlockPistonExtendEvent event) {
        BlockFace pistonDirection = getPistonDirection(event.getBlock());

        if (pistonDirection == null) {
            return new ArrayList<Block>();
        }

        Block piston = event.getBlock();
        List<Block> pushedBlocks = new ArrayList<Block>();

        for (int currentBlock = 1; currentBlock < event.getLength() + 1; currentBlock++) {
            Block block = piston.getRelative(pistonDirection, currentBlock);
            Material blockType = block.getType();

            if (blockType == Material.AIR) {
                break;
            }

            pushedBlocks.add(block);
        }

        return pushedBlocks;
    }
}
