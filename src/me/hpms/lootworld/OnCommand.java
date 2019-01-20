package me.hpms.lootworld;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class OnCommand implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
		
		if (!(sender instanceof Player)) {
			return false;
		}
		Player p = (Player) sender;
		if(p.hasPermission("lootworld.god")) {
			ItemStack item = new ItemStack(Material.CHEST);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.RED + "lwtest");
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
		}
		
	
		return false;
	}

}
