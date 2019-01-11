package me.hpms.lootworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.md_5.bungee.api.ChatColor;

/* TODO 
 * Add population for other worlds
 * Save Metadata for chest
 */
public class LocationGenerator {
	
	private final String PREFIX = ChatColor.GREEN + "『 LootWorld 』" + ChatColor.BLUE + "-> ";
	
	private final File locationFile;
	
	private FileConfiguration locationConfig;
	
	private FileConfiguration pluginConfig;
	
	private List<Location> location;
	
	private LootWorld plugin;
	
	private ChestRarity rarity;
	
	private final int maxChestPopulation;
	
	private final double positiveBoundary;
	
	private final double negativeBoundary;
	
	private final double maxHeight = 256;
	
	private final int maxItemAll;
	
	private final int minItemAll;
	
	private final int maxItemRank;
	
	private final int minItemRank;
	
	
	public LocationGenerator(LootWorld lw) {
		plugin = lw;
		location = new ArrayList<Location>();
		locationFile = new File(plugin.getDataFolder(), "location.yml");
		pluginConfig = plugin.getConfig();
		rarity = new ChestRarity(plugin);
		maxChestPopulation = pluginConfig.getInt("max-chest-population");
		
		positiveBoundary = pluginConfig.getInt("positive-boundary");
		negativeBoundary = pluginConfig.getInt("negative-boundary");
		
		maxItemAll = pluginConfig.getInt("max-rank-all-item");
		minItemAll = pluginConfig.getInt("min-rank-all-item");
		
		maxItemRank = pluginConfig.getInt("max-rank-item");
		minItemRank = pluginConfig.getInt("min-rank-item");
		
		
		loadConfiguration();
		readConfigurationToLocation();
		generateLocation();
		
	}
	
	public FileConfiguration getFileConfiguration() {
		return locationConfig;
	}
	
	public List<Location> getChestLocationList() {
		return location;
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
	
	public void saveConfiguration() {
		try {
			locationConfig.save(locationFile);
		} catch (IOException e) {
			e.printStackTrace();
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
				
			}catch(NumberFormatException e) {
				System.out.print(e);
			}
		
		}
		
	}
	
	public Entry<String,Float> generateChestType() {
		float t = 0;
		
		for(Entry<String, Float> entry : rarity.getRanking().entrySet()) {
			float probability = entry.getValue();
			t = t + probability;
			
			//Uniform distribution ranging from [0,1]	
			double outcome = Math.random() * rarity.getTotalProbability();
			
			if(t >= outcome) {
				return entry;
			}	
		}
		return null;
		
	}

	public void generateLocation() {
		if(location.size() == maxChestPopulation) {
			Bukkit.getConsoleSender().sendMessage(PREFIX + "Chest amount full-filled...");
			return;
		}
		Bukkit.getConsoleSender().sendMessage(PREFIX + "Generating locations for chests...");
		Bukkit.getConsoleSender().sendMessage(PREFIX + "If this is your first time this may take a while depends on maxChestPopulation");
		Bukkit.getConsoleSender().sendMessage(PREFIX + "Consider change spigot.yml \'timeout\' property to higher if your server crash...");
		ConfigurationSection section = locationConfig.getConfigurationSection("location");
		double range = (positiveBoundary - negativeBoundary) + 1;
		
		
		World world = Bukkit.getWorld("world");
		
		for(int i = 0; i < maxChestPopulation; i++) {
			
			int itemAmountRank = (int) (Math.random() * ((maxItemRank - minItemRank) + 1)) + minItemRank;
			int itemAmountAll = (int) (Math.random() * ((maxItemAll - minItemAll ) + 1)) + minItemAll;
			
			double x = (Math.random() * range) + negativeBoundary;
			double y = (Math.random() * maxHeight) + 1;
			double z = (Math.random() * range) + negativeBoundary;
			while(true) {
				Location loc = new Location(world,x,y,z);
				if(loc.subtract(0, 1, 0).getBlock().getType() != Material.AIR && loc.subtract(0, 1, 0).getBlock().getType() != Material.WATER
						&& loc.add(0, 1, 0).getBlock().getType() != Material.AIR && loc.add(0, 1, 0).getBlock().getType() != Material.WATER) {
					if(loc.subtract(1, 0, 0).getBlock().getType() != Material.AIR && loc.subtract(1, 0, 0).getBlock().getType() != Material.WATER
							&& loc.add(1, 0, 0).getBlock().getType() != Material.AIR && loc.add(1, 0, 0).getBlock().getType() != Material.WATER) {
						if(loc.subtract(0, 0, 1).getBlock().getType() != Material.AIR && loc.subtract(0, 0, 1).getBlock().getType() != Material.WATER
								&& loc.add(0,0,1).getBlock().getType() != Material.AIR && loc.add(0,0,1).getBlock().getType() != Material.WATER) {
							break;
						}else {
							double newZ = (Math.random() * range) + negativeBoundary;
							z = newZ;
						}
					}else {
						double newX = (Math.random() * range) + negativeBoundary;
						x = newX;
					}
				}else {
					double newY = (Math.random() * maxHeight) + 1;
					y = newY;
						
				}
				
			}
			Location loc = new Location(world,x,y,z);
			Entry<String,Float> entry = generateChestType();
			ChestProperty chest = new ChestProperty(plugin,loc,entry.getKey(),itemAmountAll,itemAmountRank,entry.getValue());
			String locString = loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getWorld().getName();
			section.set(chest.getRarity() + "-" + String.valueOf(i), locString);
			saveConfiguration();
			
		}
		readConfigurationToLocation();
	}
	
}
