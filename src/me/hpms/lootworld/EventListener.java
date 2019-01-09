package me.hpms.lootworld;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Entity;
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
		
		if(p.getName().equalsIgnoreCase("Arest")) {
			Chunk chunk = p.getLocation().getChunk();
			Entity[] en = chunk.getEntities();
			for(Entity entity : en) {
				Log.info(entity);
			}
			p.sendMessage(PREFIX + "Items added...");
			for(ItemConfig item : items) {
				p.getInventory().addItem(item.getItem());
			}
		}
		
	}

}
