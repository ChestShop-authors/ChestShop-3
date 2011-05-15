package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Messaging.Message;
import com.Acrobot.ChestShop.Protection.Security;
import com.Acrobot.ChestShop.Utils.Config;
import com.Acrobot.ChestShop.Utils.SignUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * @author Acrobot
 */
public class playerInteract extends PlayerListener{

    public void onPlayerInteract(PlayerInteractEvent event){
        Action action = event.getAction();
        Player player = event.getPlayer();

        if(action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK){
            return;
        }

        Block block = event.getClickedBlock(); 

        if(block.getType() == Material.CHEST){
            if(Security.isProtected(block) && !Security.canAccess(player, block)){
                Message.sendMsg(player, "ACCESS_DENIED");
                event.setCancelled(true);
                return;
            }
        }

        if(!SignUtil.isSign(block)){
            return;
        }
        Sign sign = (Sign) block.getState();
        if(!SignUtil.isValid(sign)){
            return;
        }

        Action buy = (Config.getBoolean("reverse_buttons") ? Action.LEFT_CLICK_BLOCK : Action.RIGHT_CLICK_BLOCK);

        
        if(action == buy){
            player.sendMessage("You are buying!");
        } else{
            player.sendMessage("You are selling!");
        }
    }
}
