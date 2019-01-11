package me.hpms.lootworld;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OnCommand implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
		
		if (!(sender instanceof Player)) {
			return false;
		}
		
		
		
		return false;
	}

}
