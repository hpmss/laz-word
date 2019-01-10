package me.hpms.lootworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Log;

import com.google.common.collect.Multiset.Entry;

public class LocationGenerator {
	
	private final File locationFile;
	
	private FileConfiguration locationConfig;
	
	private List<Location> location;
	
	private LootWorld plugin;
	
	private final int maxChestPopulation = 1000;
	
	private List<Float> weight;
	
	private final float xOffset = 1000f;
	
	private final float zOffset = 1000f;
	
	
	
	public LocationGenerator(LootWorld lw) {
		plugin = lw;
		location = new ArrayList<Location>();
		weight = new ArrayList<Float>();
		locationFile = new File(plugin.getDataFolder(), "location.yml");
		
		loadConfiguration();
		readConfigurationToLocation();
		getWeight();
	}
	
	public void loadConfiguration() {
		if(!locationFile.exists()) {
			try {
				locationFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		locationConfig = YamlConfiguration.loadConfiguration(locationFile);
		if(locationConfig.getConfigurationSection("location") == null) {
			locationConfig.createSection("location");
			saveConfiguration();
		}
	}
	
	public void readConfigurationToLocation() {
		ConfigurationSection section = locationConfig.getConfigurationSection("location");
		Map<String,Object> keyValue = section.getValues(false);
		
		for(java.util.Map.Entry<String, Object> set : keyValue.entrySet()) {
			String value = (String) set.getValue();
			String[] locationSplit = value.split(",");
			try {
				double x = Double.parseDouble(locationSplit[0]);
				double y = Double.parseDouble(locationSplit[1]);
				double z = Double.parseDouble(locationSplit[2]);
				World world = Bukkit.getWorld(locationSplit[3]);
				Location loc = new Location(world,x,y,z);
				location.add(loc);
				Log.info(loc);
				
			}catch(NumberFormatException e) {
				System.out.print(e);
			}
		
		}
		
	}
	
	public void saveConfiguration() {
		try {
			locationConfig.save(locationFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getWeight() {
		
		Class<ChestRarity> c = ChestRarity.class;
		ChestRarity[] constant = (ChestRarity[]) c.getEnumConstants();
		
		for(ChestRarity chest : constant) {
			weight.add(chest.getChestProbability());
		}
		
	}
	
	
	public void generateLocation() {
		
	}
	
	public FileConfiguration getFileConfiguration() {
		return locationConfig;
	}
	
	public List<Location> getChestLocationList() {
		return location;
	}

}
