package me.hpms.lootworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class LocationGenerator {
	
	private static File locationFile;
	
	private static FileConfiguration locationConfig;
	
	private static FileConfiguration pluginConfig;
	
	private static HashMap<String,LinkedHashMap<String,String>> LOCATION_MAP;

	private static Map<String, Object> WORLD_MAP;
	
	private static int maxChestPopulation;
	
	private static Random random;
	
	private static final double positiveBoundary;
	
	private static final double negativeBoundary;
	
	public static final int maxItemAll;
	
	public static final int minItemAll;
	
	public static final int maxItemRank;
	
	public static final int minItemRank;
	
	
	static {
		LOCATION_MAP = new HashMap<String,LinkedHashMap<String,String>>();
		pluginConfig = LootWorld.plugin.getConfig();
		locationFile = new File(LootWorld.plugin.getDataFolder(), "location.yml");
		WORLD_MAP = pluginConfig.getConfigurationSection("worlds").getValues(false);
		positiveBoundary = pluginConfig.getInt("positive-boundary");
		negativeBoundary = pluginConfig.getInt("negative-boundary");
		random = new Random();
		maxItemAll = pluginConfig.getInt("max-rank-all-item");
		minItemAll = pluginConfig.getInt("min-rank-all-item");
		maxItemRank = pluginConfig.getInt("max-rank-item");
		minItemRank = pluginConfig.getInt("min-rank-item");
		random.setSeed(random.nextLong());
	}
	
	private List<ChestProperty> writeLocation;
	
	private double maxHeight = 256;
	
	private double minHeight = 5;
	
	public LocationGenerator() {
		writeLocation = new ArrayList<ChestProperty>();	
		loadConfiguration();
		generateLocation();
		readConfiguration();
	}
	
	public static FileConfiguration getFileConfiguration() {
		return locationConfig;
	}
	
	public static void updateConfigByWorld(String world,String id,String... message) {
		if(!LOCATION_MAP.containsKey(world)) return;
		if(LOCATION_MAP.get(world).containsKey(id)) {
			LOCATION_MAP.get(world).remove(id);
			if(message.length != 0) {
				locationConfig.createSection("location-" + world , LOCATION_MAP.get(world));
				for(String m : message) {
					Bukkit.getServer().broadcastMessage(m);
				}
			}
		}
		saveConfiguration();
		
	}
	
	public void loadConfiguration() {
		if(!locationFile.exists()) {
			try {
				locationFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		if(WORLD_MAP.size() == 0) {
			throw new NullPointerException(LootWorld.PREFIX + "No world found... ?");
		}
		locationConfig = YamlConfiguration.loadConfiguration(locationFile);
		for(String worldName : WORLD_MAP.keySet()) {
			LOCATION_MAP.put(worldName, new LinkedHashMap<String,String>());
			if(locationConfig.getConfigurationSection("location-" + worldName ) == null) {
				locationConfig.createSection("location-" + worldName);
				saveConfiguration();
			}
		}
		
	}
	
	public static void saveConfiguration() {
		try {
			locationConfig.save(locationFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readConfiguration() {
		Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "Wrapping up...");
		ConfigurationSection section;
		LinkedHashMap<String,Object> keyValue;

		for(String worldName : WORLD_MAP.keySet()) {
			section = locationConfig.getConfigurationSection("location-" + worldName);
			keyValue = (LinkedHashMap<String, Object>) section.getValues(false);
			if(keyValue.size() == 0||keyValue.size() == LOCATION_MAP.get(worldName).size()) {
				break;
			}
			keyValue.forEach((key,value) -> {
				String[] keySplit = key.split("-");
				String[] locationSplit = value.toString().split(",");
				try {
					double x = Double.parseDouble(locationSplit[0]);
					double y = Double.parseDouble(locationSplit[1]);
					double z = Double.parseDouble(locationSplit[2]);
					World worldz = Bukkit.getWorld(locationSplit[3]);
					ChestProperty c = new ChestProperty(Integer.parseInt(keySplit[1]),new Location(worldz,x,y,z),keySplit[0],ChestRarity.getProbabilityByName(keySplit[0]));
					c.reloadChest();
					if(!LOCATION_MAP.get(worldName).containsKey(c.toIdString())) {
						LOCATION_MAP.get(worldName).put(c.toIdString(), c.toString());
					}
				}catch(NumberFormatException e) {
					System.out.print(e);
				}
			});
			
		}
		
	}
	public void saveLocation(String w) {
		ConfigurationSection section;
		section = locationConfig.getConfigurationSection("location-" + w);
		if(writeLocation.size() != 0) {
			Collections.sort(writeLocation);
			for(ChestProperty c : writeLocation) {
				LOCATION_MAP.get(w).put(c.toIdString(), c.toString());
				section.set(c.toIdString(), c.toString());
			}
				
		}
		saveConfiguration();
		writeLocation.clear();
	}
	
	public static Entry<String,Float> generateChestType() {
		float t = 0;
		float probability;
		for(Map.Entry<String, Float> entry : ChestRarity.getRanking().entrySet()) {
			probability = entry.getValue();
			t = t + probability;
			double outcome = random.nextFloat() * ChestRarity.getTotalProbability();
			if(t > outcome) {
				return entry;
			}	
		}
		return null;
	}
	
	private int pseudoRead(String w) {
		return locationConfig.getConfigurationSection("location-" + w).getValues(false).size();
	}
	
	public void generateLocation() {
		Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "If this is your first time this may take a while depends on max chest population for each world");
		Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "Consider changing spigot.yml \'timeout\' property to higher if your server crash...");
		
		double range = (positiveBoundary - negativeBoundary) + 1;
		World w = null;
		int currentWorldCounter;
		int currentId;
		double x;
		double y;
		double z;
		Location loc;
		int itemAmountRank;
		int itemAmountAll;
		Map.Entry<String,Float> entry;
		for(Map.Entry<String,Object> set : WORLD_MAP.entrySet()) {
			try {
				currentWorldCounter = pseudoRead(set.getKey()) + 1;
				maxChestPopulation = Integer.parseInt(set.getValue().toString());
				w = Bukkit.getServer().getWorld(set.getKey());
				String last = new ArrayList<>(locationConfig.getConfigurationSection("location-" + set.getKey()).getValues(false).keySet()).get(currentWorldCounter - 2);
				currentId = (currentWorldCounter == 1) ? 1 : Integer.valueOf(last.split("-")[1]);
			}catch(NumberFormatException e) {
				Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "Cannot parse amount of chest population for world \'" + set.getKey() + "\'");
				return;
			}catch(NullPointerException e) {
				Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "Make sure \'" + set.getKey() + "\' exists...");
				Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "Make sure you dont delete lines in location.yml");
				return;
			}
			if(w != null) {
				if(w.getEnvironment() == Environment.NETHER) {
					maxHeight = 121;
				}else {
					maxHeight = 256;
				}
				if(w.getEnvironment() == Environment.THE_END) {
					minHeight = 16;
					Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX  + "GENERATING CHESTS FOR THE_END MAY TAKES MORE TIME !!!");
				}else {
					minHeight = 5;
				}
				Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "Generating chests for world \'" + w.getName() + "\'");
				for(int i = 0; i < maxChestPopulation; i++) {
					if(currentWorldCounter >= maxChestPopulation) {
						saveLocation(set.getKey());
						Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "Chests generation for world " + "\'" + w.getName() + "\' full-filled...");
						break;
					}
					itemAmountRank = (int) (Math.random() * ((maxItemRank - minItemRank) + 1)) + minItemRank;
					itemAmountAll = (int) (Math.random() * ((maxItemAll - minItemAll ) + 1)) + minItemAll;
					
					x = (random.nextFloat() * range) + negativeBoundary;
					y = (random.nextFloat() * ((maxHeight - minHeight) + 1)) + minHeight;
				    z = (random.nextFloat() * range) + negativeBoundary;
					if(w.getEnvironment() == Environment.THE_END) {
						while(true) {
							
							loc = new Location(w,x,y,z);
							if(loc.subtract(0, 1, 0).getBlock().getType() != Material.AIR && loc.subtract(0, 1, 0).getBlock().getType() != Material.WATER
									&& loc.add(0, 1, 0).getBlock().getType() != Material.AIR && loc.add(0, 1, 0).getBlock().getType() != Material.WATER) {
								if(loc.subtract(1, 0, 0).getBlock().getType() != Material.AIR && loc.subtract(1, 0, 0).getBlock().getType() != Material.WATER
										&& loc.add(1, 0, 0).getBlock().getType() != Material.AIR && loc.add(1, 0, 0).getBlock().getType() != Material.WATER) {
									if(loc.subtract(0, 0, 1).getBlock().getType() != Material.AIR && loc.subtract(0, 0, 1).getBlock().getType() != Material.WATER
											&& loc.add(0,0,1).getBlock().getType() != Material.AIR && loc.add(0,0,1).getBlock().getType() != Material.WATER) {
										break;
									}								
								}
							}
							x = (random.nextFloat() * range) + negativeBoundary;
							y = (random.nextFloat() * maxHeight) + 1;
							z = (random.nextFloat() * range) + negativeBoundary;
						}
					}else {
						while(true) {
							loc = new Location(w,x,y,z);
							if(loc.subtract(0, 1, 0).getBlock().getType() != Material.AIR && loc.subtract(0, 1, 0).getBlock().getType() != Material.WATER
									&& loc.add(0, 1, 0).getBlock().getType() != Material.AIR && loc.add(0, 1, 0).getBlock().getType() != Material.WATER) {
								if(loc.subtract(1, 0, 0).getBlock().getType() != Material.AIR && loc.subtract(1, 0, 0).getBlock().getType() != Material.WATER
										&& loc.add(1, 0, 0).getBlock().getType() != Material.AIR && loc.add(1, 0, 0).getBlock().getType() != Material.WATER) {
									if(loc.subtract(0, 0, 1).getBlock().getType() != Material.AIR && loc.subtract(0, 0, 1).getBlock().getType() != Material.WATER
											&& loc.add(0,0,1).getBlock().getType() != Material.AIR && loc.add(0,0,1).getBlock().getType() != Material.WATER) {
										break;
									}else {
										z = (random.nextFloat() * range) + negativeBoundary;
									}
								}else {
									x = (random.nextFloat() * range) + negativeBoundary;
								}	
							}else {
								y = (random.nextFloat() * maxHeight) + 1;
							}
						}
					}
					loc = new Location(w,(double)(int)x,(double)(int)y,(double)(int)z);
					currentWorldCounter += 1;
					if(currentWorldCounter % 100 == 0) {
						Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + " " + currentWorldCounter + " chests generated...");
					}
					entry = generateChestType();
					writeLocation.add(new ChestProperty(currentId,loc,entry.getKey(),itemAmountAll,itemAmountRank,entry.getValue()));
					currentId += 1;
				}
			}
		}
		
	}
	
	
}
