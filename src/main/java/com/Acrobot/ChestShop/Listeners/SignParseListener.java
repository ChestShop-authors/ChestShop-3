package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.MaterialParseEvent;
import com.Acrobot.ChestShop.Events.SignValidationEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignParseListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public static void onItemParse(ItemParseEvent event) {
        if (event.getItem() == null) {
            event.setItem(MaterialUtil.getItem(event.getItemString()));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public static void onMaterialParse(MaterialParseEvent event) {
        if (event.getMaterial() == null) {
            event.setMaterial(MaterialUtil.getMaterial(event.getMaterialString()));
        }
    }

    @EventHandler
    public void onSignValidation(SignValidationEvent event) {
        String ownerName = event.getOwner();
        String[] lines = event.getLines();

        // If the shop owner is not blank (auto-filled) or the admin shop string, we need to validate it
        if ((!ChestShopSign.isAdminShop(ownerName)) && (!ownerName.isEmpty())) {

            // Prepare regexp patterns
            Pattern playernamePattern = Pattern.compile(Properties.VALID_PLAYERNAME_REGEXP); // regexp from config file
            Matcher playernameWithIdMatcher = Pattern.compile("^(.+):[A-Za-z0-9]+$").matcher(ownerName); // regexp to match ':' and a base62 encoded string
            // Check if the owner name has an ID. This can happen on duplicate or too long names
            if (playernameWithIdMatcher.matches()) {
                // Owner name matches the id pattern, so validate everything before the last ':'
                ownerName = playernameWithIdMatcher.group(1);
            }

            // If the owner name doesn't match, this is not a valid sign
            if (!playernamePattern.matcher(ownerName).matches()) {
                event.setValid(false);
                return;
            }
        }

        // The owner name on the first line is valid. Now validate the last 3 lines against the predefined regexp patterns.
        for (int i = 0; i < 3; i++) {
            boolean matches = false;
            for (Pattern pattern : ChestShopSign.SHOP_SIGN_PATTERN[i]) {
                if (pattern.matcher(StringUtil.strip(StringUtil.stripColourCodes(lines[i + 1]))).matches()) {
                    matches = true;
                    break;
                }
            }
            if (!matches) {
                event.setValid(false);
                return;
            }
        }

        // All lines are looking good. If the price line contains only one ':', then this is a valid prepared sign.
        String priceLine = ChestShopSign.getPrice(lines);
        event.setValid(priceLine.indexOf(':') == priceLine.lastIndexOf(':'));
    }
}
