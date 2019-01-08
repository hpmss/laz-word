package me.hpms.lootworld;

import java.io.File;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class LootWorld extends JavaPlugin implements Listener{
		
	private File path = new File(this.getDataFolder() + "");
	private ItemParser parser;
	@Override
	public void onEnable() {
		
		parser = new ItemParser(path);
		getServer().getPluginManager().registerEvents(this, this);
		getDataFolder().mkdir();
		
	}
	
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		
		List<ItemStack> items = parser.getParsedItems();
		
		Player p = e.getPlayer();
		
		if(p.getName().equalsIgnoreCase("Arest")) {
			p.getInventory().addItem(items.get(0));
			p.sendMessage("-> hifumi added...");
		}
	}
	
	

	
}
