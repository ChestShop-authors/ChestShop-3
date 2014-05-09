import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * @author CrowsEyes 
 */


public class Toggle implements CommandExecutor {
    
	private final static ArrayList<String> toggledPlayersS = new ArrayList<String>();
	private final static ArrayList<String> toggledPlayersO = new ArrayList<String>();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

	    if (sender instanceof Player) {
	    	
	    	if(args.length == 0) {
	    		player.sendMessage(ChatColor.GREEN + "Missing arguement. /cstoggle ?");
	    		return false;
	    	}
	    	
	        Player player = (Player) sender;
	        
	        if(args[0] == "self" || args[0] == "s")
	        	ignoreSelfMessages(player);
	        
	        else if(args[0] == "others" || args[0] == "o")
	        	ignoreOtherMessages(player);
	        
	        else if(args[0] == "?") {
	        	player.sendMessage(ChatColor.GREEN + "Usage: /cstoggle [self/others] | /cstoggle [s/o]");
	        	player.sendMessage(ChatColor.RED + "Note: The 'others/o' arguements will disable messages")
	        	player.sendMessage(ChatColor.RED + "when using other players shops. Use with caution.");
	        	
	        }
	        
	        else
	        	player.SendMessage(ChatColor.GREEN + "Invalid arguement. /cstoggle ?")
	    }
        return true;
    }

    public void ignoreSaleMessages(Player player) {
    	
        if(toggledPlayersS.contains(player.getName())) {
            toggledPlayersS.remove(player.getName());
            player.sendMessage(ChatColor.GREEN + "You will once again receive messages from your shops.");
        }
        else {
            toggledPlayersS.add(player.getName());
            player.sendMessage(ChatColor.RED + "You will no longer receive messages from your shops.");
        }
            
    }
    
    /*
     * While this is a nice theory, it would need a cleaner
     * implementation to be considered safe. Currently, this would
     * allow players to, in theory, accidentally purchase/sell items from
     * a shop and not realize right away. A 'total tracker' would clean
     * up the implementation immensely, but poses its own risks.
     * May be better to remove this option in favor of pursuing a
     * total tracker, or possibly a duration based implementation instead?
     */
    public void ignoreOtherMessages(Player player) {
    	
        if(toggledPlayersO.contains(player.getName())) {
            toggledPlayersO.remove(player.getName());
            player.sendMessage(ChatColor.GREEN + "You will once again receive messages from other players shops.");
        }
        else {
            toggledPlayersO.add(player.getName());
            player.sendMessage(ChatColor.RED + "Be Warned: You will no longer receive messages from other players shops.");
        }
            
    }

    public static boolean isIgnoringSelfMessages(Player player) {
    	
        return toggledPlayersS.contains(player.getName());
    }
    
    public static boolean isIgnoringOtherMessages(Player player) {
    	
    	return toggledPlayersO.contains(player.getName());
    }

}
