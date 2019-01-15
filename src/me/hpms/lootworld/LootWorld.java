package me.hpms.lootworld;

import java.io.BufferedWriter;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

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
		saveConfig();
	}
	
	public void instantiateLootWorld() {
		if(!(new File(getDataFolder().getPath(),"config.yml").exists())){
			saveDefaultConfig();
			saveConfig();
	    }
		saveConfig();
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		this.getCommand("lwtest").setExecutor(new OnCommand());
		
		
		nms = new NMSEntity();
		registerEntities();
		parser = new ItemParser(path);
		generator = new LocationGenerator(this);
		rarity = new ChestRarity(this);
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
	
	public List<ItemConfig> getParsedItems() {
		return parser.getParsedItems();
	}
	
	public List<ItemStack> getRankAllItems() {
		return parser.getRankAllItems();
	}
	
	public HashMap<Float,ItemStack> getRankAllItemsDistribution() {
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
