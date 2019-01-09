package me.hpms.lootworld;

import java.io.File;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

public class LootWorld extends JavaPlugin{
	
	
	private final File path = new File(getDataFolder() + "");
	
	private EventListener listener;
	
	private ItemParser parser;
	
	
	@Override
	public void onEnable() {
		
		listener = new EventListener(this);
		parser = new ItemParser(path);
		
		getServer().getPluginManager().registerEvents(listener, this);
		getDataFolder().mkdir();
		
	}
	
	public List<ItemConfig> getParsedItems() {
		return parser.getParsedItems();
	}
	

	
}
