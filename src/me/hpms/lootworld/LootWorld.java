package me.hpms.lootworld;

import java.io.BufferedWriter;
import java.io.File;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

public class LootWorld extends JavaPlugin{
	
	
	private File path = new File(getDataFolder() + "");
	
	private EventListener listener;
	
	private ItemParser parser;
	
	
	@Override
	public void onEnable() {
		getDataFolder().mkdir();
		
		parser = new ItemParser(path);
		listener = new EventListener(this);
		
		getServer().getPluginManager().registerEvents(listener, this);	
		
	}
	
	public List<ItemConfig> getParsedItems() {
		return parser.getParsedItems();
	}
	
	public void parseSourceFromJar(String pathToJar,BufferedWriter writer) {
		parser.getJSONData(pathToJar, writer);
	}
	
	

	
}
