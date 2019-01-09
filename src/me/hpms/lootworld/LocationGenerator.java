package me.hpms.lootworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LocationGenerator {
	
	private final File locationFile;
	
	private FileConfiguration locationConfig;
	
	private List<Location> location;
	
	private LootWorld plugin;
	
	private final float xOffset = 1000f;
	
	private final float zOffset = 1000f;
	
	
	
	public LocationGenerator(LootWorld lw) {
		plugin = lw;
		location = new ArrayList<Location>();
		locationFile = new File(plugin.getDataFolder(), "location.yml");
		
		loadConfiguration();
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
	}
	
	public void readConfigurationToLocation() {
		List<String> locationList = locationConfig.getStringList("location");
		
	}
	
	public void saveConfiguration() {
		try {
			locationConfig.save(locationFile);
		} catch (IOException e) {
			e.printStackTrace();
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
