package com.Acrobot.ChestShop.Listeners.Block;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Listeners.Block.Break.SignBreak;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import static com.Acrobot.ChestShop.Permission.OTHER_NAME_DESTROY;

/**
 * @author Acrobot
 */
public class SignCreate implements Listener {

    private static boolean HAS_SIGN_SIDES;

    static {
        try {
            SignChangeEvent.class.getMethod("getSide");
            HAS_SIGN_SIDES = true;
        } catch (NoSuchMethodException e) {
            HAS_SIGN_SIDES = false;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onSignChange(SignChangeEvent event) {
        Block signBlock = event.getBlock();

        if (!BlockUtil.isSign(signBlock)) {
            return;
        }

        Sign sign = (Sign) signBlock.getState();

        if (HAS_SIGN_SIDES && event.getSide() != Side.FRONT) {
            if (ChestShopSign.isValid(sign)) {
                event.setCancelled(true);
                Messages.CANNOT_CHANGE_SIGN_BACKSIDE.sendWithPrefix(event.getPlayer());
            }
            return;
        }

        if (ChestShopSign.isValid(event.getLines()) && !NameManager.canUseName(event.getPlayer(), OTHER_NAME_DESTROY, ChestShopSign.getOwner(event.getLines()))) {
            event.setCancelled(true);
            sign.update();
            ChestShop.logDebug("Shop sign creation at " + sign.getLocation() + " by " + event.getPlayer().getName() + " was cancelled as they weren't able to create a shop for the account '" + ChestShopSign.getOwner(event.getLines()) + "'");
            return;
        }

        String[] lines = StringUtil.stripColourCodes(event.getLines());

        if (!ChestShopSign.isValidPreparedSign(lines)) {
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

        ShopCreatedEvent postEvent = new ShopCreatedEvent(preEvent.getPlayer(), preEvent.getSign(), uBlock.findConnectedContainer(preEvent.getSign()), preEvent.getSignLines(), preEvent.getOwnerAccount());
        ChestShop.callEvent(postEvent);
    }
}
