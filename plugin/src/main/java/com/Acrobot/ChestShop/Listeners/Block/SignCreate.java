package com.Acrobot.ChestShop.Listeners.Block;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.ImplementationAdapter;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Events.ShopEditedEvent;
import com.Acrobot.ChestShop.Events.SignValidationEvent;
import com.Acrobot.ChestShop.Listeners.Block.Break.SignBreak;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Arrays;

import static com.Acrobot.ChestShop.Permission.OTHER_NAME_DESTROY;

/**
 * @author Acrobot
 */
public class SignCreate implements Listener {

    @EventHandler(ignoreCancelled = true)
    public static void onSignChange(SignChangeEvent event) {
        Block signBlock = event.getBlock();

        if (!BlockUtil.isSign(signBlock)) {
            return;
        }

        Sign sign = (Sign) ImplementationAdapter.getState(signBlock, false);

        boolean shopExisted = ChestShopSign.isValid(sign);
        if (shopExisted && !ChestShopSign.canAccess(event.getPlayer(), sign)) {
            // There was already a shop here, but the player does not have permission to change it
            event.setCancelled(true);
            sign.update();
            ChestShop.logDebug("Shop sign creation at " + sign.getLocation() + " by " + event.getPlayer().getName() + " was cancelled as there already was a shop here and the player did not have permission to change it");
            return;
        }

        if (ChestShopSign.isValid(event.getLines()) && !NameManager.canUseName(event.getPlayer(), OTHER_NAME_DESTROY, ChestShopSign.getOwner(event.getLines()))) {
            event.setCancelled(true);
            sign.update();
            ChestShop.logDebug("Shop sign creation at " + sign.getLocation() + " by " + event.getPlayer().getName() + " was cancelled as they weren't able to create a shop for the account '" + ChestShopSign.getOwner(event.getLines()) + "'");
            return;
        }

        // Make sure the sign actually changed before running any further logic
        if (shopExisted && Arrays.equals(event.getLines(), sign.getLines())) {
            ChestShop.logDebug("Shop sign modification at " + sign.getLocation() + " by " + event.getPlayer().getName() + " was ignored as the new lines match the already existing sign");
            return;
        }

        String[] lines = StringUtil.stripColourCodes(event.getLines());

        SignValidationEvent signValidationEvent = new SignValidationEvent(lines);
        ChestShop.callEvent(signValidationEvent);

        if (!signValidationEvent.isValid()) {
            // Check if a valid shop already existed previously
            if (ChestShopSign.isValid(sign)) {
                SignBreak.sendShopDestroyedEvent(sign, event.getPlayer());
            }
            return;
        }

        PreShopCreationEvent preEvent = new PreShopCreationEvent(event.getPlayer(), sign, lines);
        ChestShop.callEvent(preEvent);

        if (preEvent.getOutcome().shouldBreakSign()) {
            event.setCancelled(true);
            signBlock.breakNaturally();
            ChestShop.logDebug("Shop sign creation at " + sign.getLocation() + " by " + event.getPlayer().getName() + " was cancelled (creation outcome: " + preEvent.getOutcome() + ") and the sign broken");
            return;
        }

        for (byte i = 0; i < preEvent.getSignLines().length && i < 4; ++i) {
            event.setLine(i, preEvent.getSignLine(i));
        }

        if (preEvent.isCancelled()) {
            ChestShop.logDebug("Shop sign creation at " + sign.getLocation() + " by " + event.getPlayer().getName() + " was cancelled (creation outcome: " + preEvent.getOutcome() + ") and sign lines were set to " + String.join(", ", preEvent.getSignLines()));
            return;
        }

        Container container = uBlock.findConnectedContainer(preEvent.getSign());

        if (shopExisted) {
            ChestShop.callEvent(new ShopEditedEvent(preEvent.getPlayer(), preEvent.getSign(), container, sign.getLines(), preEvent.getSignLines(), preEvent.getOwnerAccount()));
        }

        ChestShop.callEvent(new ShopCreatedEvent(preEvent.getPlayer(), preEvent.getSign(), container, preEvent.getSignLines(), preEvent.getOwnerAccount()));
    }
}
