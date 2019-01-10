package me.hpms.lootworld;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import net.md_5.bungee.api.ChatColor;

public class EventListener implements Listener {
	
	private final String PREFIX = ChatColor.GREEN + "『 LootWorld 』" + ChatColor.BLUE + "-> ";

	private LootWorld plugin;
	
	
	public EventListener(LootWorld lw) {
		plugin = lw;
		
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if(e.getClickedBlock().getType() == Material.CHEST) {
			Chest chest = (Chest) e.getClickedBlock().getState();
			if(chest.hasMetadata("Common")) {
				e.getPlayer().sendMessage(PREFIX + "Chest opened..");
			}
		}
	}

}
