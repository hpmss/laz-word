package me.hpms.lootworld;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.hpms.lootworld.util.NMSEntity;
import net.md_5.bungee.api.ChatColor;

public class LootWorld extends JavaPlugin{
	
	private File path = new File(getDataFolder() + "");
	
	private static EventListener listener;
	
	private static ItemParser parser;
	
	public static final String PREFIX = ChatColor.GREEN + "『 LootWorld 』" + ChatColor.BLUE + "-> ";
	
	public static JavaPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("LootWorld");
		instantiateLootWorld();
	}
	
	@Override
	public void onDisable() {
		saveDefaultConfig();
	}
	
	public void instantiateLootWorld() {
		if(!(new File(getDataFolder().getPath(),"config.yml").exists())){
			saveDefaultConfig();
	    }
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		parser = new ItemParser(path);
		ChestRarity.init();
		new LocationGenerator();
		registerEntities();
		listener = new EventListener(this);
		getServer().getPluginManager().registerEvents(listener, this);
		this.getCommand("lwtest").setExecutor(new OnCommand());
	}
	
	public static void registerEntities() {
		try {
			NMSEntity.registerEntity(EntityType.ZOMBIE, "ranged_zombie", CustomEntityMob.class);
		}catch(Exception e) {e.printStackTrace();}
	}
	
	public static HashMap<String,ArrayList<ItemConfig>> getParsedItems() {
		return parser.getParsedItems();
	}
	
	public static List<ItemStack> getRankAllItems() {
		return parser.getRankAllItems();
	}
	
	public static HashMap<ItemStack,Float> getRankAllItemsDistribution() {
		return parser.getRankAllItemsDistribution();
	}
	
	public static ItemParser getItemParser() {
		return parser;
	}
	
	public static void parseSourceFromJar(String pathToJar,BufferedWriter writer) {
		parser.getJSONData(pathToJar, writer);
	}
	
	
}
