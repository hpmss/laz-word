package me.hpms.lootworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
	
	private HashMap<String,List<Location>> location;
	
	private LootWorld plugin;
	
	private ChestRarity rarity;
	
	private final Map<String, Object> worlds;
	
	private int maxChestPopulation;
	
	private final double positiveBoundary;
	
	private final double negativeBoundary;
	
	private final double maxHeight = 256;
	
	private final int maxItemAll;
	
	private final int minItemAll;
	
	private final int maxItemRank;
	
	private final int minItemRank;
	
	
	public LocationGenerator(LootWorld lw) {
		plugin = lw;
		location = new HashMap<String,List<Location>>();
		locationFile = new File(plugin.getDataFolder(), "location.yml");
		pluginConfig = plugin.getConfig();
		rarity = new ChestRarity(plugin);
		
		worlds = pluginConfig.getConfigurationSection("worlds").getValues(false);
		
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
	
	public HashMap<String,List<Location>> getChestLocationList() {
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
		if(worlds.size() == 0) {
			throw new NullPointerException(PREFIX + "No world found... ?");
		}
		locationConfig = YamlConfiguration.loadConfiguration(locationFile);
		List<String> worldList = new ArrayList<>(worlds.keySet());
		for(String worldName : worldList) {
			location.put(worldName, new ArrayList<Location>());
			if(locationConfig.getConfigurationSection("location-" + worldName ) == null) {
				locationConfig.createSection("location-" + worldName);
				saveConfiguration();
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
	
	public void readConfigurationToLocation() {
		List<String> worldList = new ArrayList<>(worlds.keySet());
		for(String worldName : worldList) {
			ConfigurationSection section = locationConfig.getConfigurationSection("location-" + worldName);
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
					location.get(worldName).add(loc);
					
				}catch(NumberFormatException e) {
					System.out.print(e);
				}
			
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
		
		Bukkit.getConsoleSender().sendMessage(PREFIX + "Generating locations for chests...");
		Bukkit.getConsoleSender().sendMessage(PREFIX + "If this is your first time this may take a while depends on maxChestPopulation");
		Bukkit.getConsoleSender().sendMessage(PREFIX + "Consider change spigot.yml \'timeout\' property to higher if your server crash...");
		
		double range = (positiveBoundary - negativeBoundary) + 1;
		World w = null;
		ConfigurationSection section = null;
		List<Location> currentWorld = null;
		for(Entry<String,Object> set : worlds.entrySet()) {
			try {
				currentWorld = location.get(set.getKey());
				maxChestPopulation = Integer.parseInt(set.getValue().toString());
				w = Bukkit.getServer().getWorld(set.getKey());
				section = locationConfig.getConfigurationSection("location-" + set.getKey());
			}catch(NumberFormatException e) {
				Bukkit.getConsoleSender().sendMessage(PREFIX + "Cannot parse amount of chest population for world \'" + set.getKey() + "\'");
				return;
			}catch(NullPointerException e) {
				Bukkit.getConsoleSender().sendMessage(PREFIX + "Make sure \'" + set.getKey() + "\' exists...");
				Bukkit.getConsoleSender().sendMessage(PREFIX + "Make sure you dont delete lines in location.yml");
				return;
			}
			if(w != null) {
				if(currentWorld.size() >= maxChestPopulation) {
					Bukkit.getConsoleSender().sendMessage(PREFIX + "Chests generation for world " + "\'" + w.getName() + "\' full-filled...");
					continue;
				}
				Bukkit.getConsoleSender().sendMessage(PREFIX + "Generating chests for world \'" + w.getName() + "\'");
				for(int i = 0; i < maxChestPopulation; i++) {
					
					int itemAmountRank = (int) (Math.random() * ((maxItemRank - minItemRank) + 1)) + minItemRank;
					int itemAmountAll = (int) (Math.random() * ((maxItemAll - minItemAll ) + 1)) + minItemAll;
					
					double x = (Math.random() * range) + negativeBoundary;
					double y = (Math.random() * maxHeight) + 1;
					double z = (Math.random() * range) + negativeBoundary;
					while(true) {
						Location loc = new Location(w,x,y,z);
						Bukkit.getConsoleSender().sendMessage(PREFIX + loc.toString());
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
					Location loc = new Location(w,x,y,z);
					
					Entry<String,Float> entry = generateChestType();
					ChestProperty chest = new ChestProperty(plugin,loc,entry.getKey(),itemAmountAll,itemAmountRank,entry.getValue());
					String locString = loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getWorld().getName();
					section.set(chest.getRarity() + "-" + String.valueOf(i + 1), locString);
					saveConfiguration();
					
				}
			}
			
		}
		readConfigurationToLocation();
	}
	
}
