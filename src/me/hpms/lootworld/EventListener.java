package me.hpms.lootworld;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.ChatColor;

public class EventListener implements Listener {
	
	private final String PREFIX = ChatColor.GREEN + "『 LootWorld 』" + ChatColor.BLUE + "-> ";
	
	private LootWorld plugin;
	private List<ItemConfig> items;
	
	
	public EventListener(LootWorld lw) {
		plugin = lw;
		items = plugin.getParsedItems();
		
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		
		Player p = e.getPlayer();
		Location l = p.getLocation();
		
		plugin.getGenerator().getFileConfiguration().set("location", l.toString());
		plugin.getGenerator().saveConfiguration();
		
	}

}
