package me.hpms.lootworld;

import java.io.BufferedWriter;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.hpms.lootworld.util.NMSEntity;

public class LootWorld extends JavaPlugin{
	
	private File path = new File(getDataFolder() + "");
	
	private EventListener listener;
	
	private ItemParser parser;
	
	private LocationGenerator generator;
	
	private ChestRarity rarity;
	
	private NMSEntity nms;
	
	
	@Override
	public void onEnable() {
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
		this.getCommand("lwtest").setExecutor(new OnCommand());
		
		parser = new ItemParser(path);
		nms = new NMSEntity();
		registerEntities();
		rarity = new ChestRarity(this);
		generator = new LocationGenerator(this);
		listener = new EventListener(this);
		getServer().getPluginManager().registerEvents(listener, this);
	}
	
	public void registerEntities() {
		try {
			nms.registerEntity(EntityType.ZOMBIE, "ranged_zombie", CustomEntityMob.class);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<String,ArrayList<ItemConfig>> getParsedItems() {
		return parser.getParsedItems();
	}
	
	public List<ItemStack> getRankAllItems() {
		return parser.getRankAllItems();
	}
	
	public HashMap<ItemStack,Float> getRankAllItemsDistribution() {
		return parser.getRankAllItemsDistribution();
	}
	
	public ItemParser getItemParser() {
		return parser;
	}
	
	public LocationGenerator getGenerator() {
		return generator;
	}
	
	public ChestRarity getChestRarity() {
		return rarity;
	}
	
	public NMSEntity getNMSEntity() {
		return nms;
	}
	
	public void parseSourceFromJar(String pathToJar,BufferedWriter writer) {
		parser.getJSONData(pathToJar, writer);
	}
	
	
}
