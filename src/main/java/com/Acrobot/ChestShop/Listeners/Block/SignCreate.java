package com.Acrobot.ChestShop.Listeners.Block;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import static com.Acrobot.ChestShop.Permission.OTHER_NAME_DESTROY;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;

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

        Sign sign = (Sign) signBlock.getState();

        if (ChestShopSign.isValid(sign) && !NameManager.canUseName(event.getPlayer(), OTHER_NAME_DESTROY, StringUtil.stripColourCodes(sign.getLine(NAME_LINE)))) {
            event.setCancelled(true);
            sign.update();
            return;
        }

        String[] lines = StringUtil.stripColourCodes(event.getLines());

        if (!ChestShopSign.isValidPreparedSign(lines)) {
            return;
        }

        PreShopCreationEvent preEvent = new PreShopCreationEvent(event.getPlayer(), sign, lines);
        ChestShop.callEvent(preEvent);

        if (preEvent.getOutcome().shouldBreakSign()) {
            signBlock.breakNaturally();
            return;
        }

        for (byte i = 0; i < preEvent.getSignLines().length && i < 4; ++i) {
            event.setLine(i, preEvent.getSignLine(i));
        }

        if (preEvent.isCancelled()) {
            return;
        }

        ShopCreatedEvent postEvent = new ShopCreatedEvent(preEvent.getPlayer(), preEvent.getSign(), uBlock.findConnectedContainer(preEvent.getSign()), preEvent.getSignLines(), preEvent.getOwnerAccount());
        ChestShop.callEvent(postEvent);
    }
}
