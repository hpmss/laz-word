package me.hpms.lootworld;

import java.util.List;

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
		
//		ItemStack item = items.get(0).getItem();
//		
//		Player p = e.getPlayer();
//		Location l = p.getLocation();
//		String location = l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getWorld().getName();
//		p.getInventory().addItem(item);
//		ConfigurationSection section = plugin.getGenerator().getFileConfiguration().getConfigurationSection("location");
//		section.set(p.getUniqueId().toString(), location);
//		plugin.getGenerator().saveConfiguration();
//		p.sendMessage(PREFIX + "location saved...");
		
	}

}
