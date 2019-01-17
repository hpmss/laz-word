package me.hpms.lootworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.metadata.FixedMetadataValue;

import net.md_5.bungee.api.ChatColor;

/* TODO 
 * Update location.yml on chest open
 */
public class LocationGenerator {
	
	private final String PREFIX = ChatColor.GREEN + "『 LootWorld 』" + ChatColor.BLUE + "-> ";
	
	private final File locationFile;
	
	private FileConfiguration locationConfig;
	
	private FileConfiguration pluginConfig;
	
	private int currentCounter = 1;
	
	private HashMap<String,List<Location>> location;
	
	private List<ChestProperty> writeLocation;
	
	private LootWorld plugin;
	
	private ChestRarity rarity;
	
	private final Map<String, Object> worlds;
	
	private Set<String> world;
	
	private int maxChestPopulation;
	
	private final double positiveBoundary;
	
	private final double negativeBoundary;
	
	private double maxHeight = 256;
	
	private double minHeight = 1;
	
	private final int maxItemAll;
	
	private final int minItemAll;
	
	private final int maxItemRank;
	
	private final int minItemRank;
	
	private Random random = new Random();
	
	public LocationGenerator(LootWorld lw) {
		plugin = lw;
		location = new HashMap<String,List<Location>>();
		writeLocation = new ArrayList<ChestProperty>();
		locationFile = new File(plugin.getDataFolder(), "location.yml");
		pluginConfig = plugin.getConfig();
		rarity = plugin.getChestRarity();
		random.setSeed(random.nextLong());
		
		worlds = pluginConfig.getConfigurationSection("worlds").getValues(false);
		world = worlds.keySet();
		
		positiveBoundary = pluginConfig.getInt("positive-boundary");
		negativeBoundary = pluginConfig.getInt("negative-boundary");
		
		maxItemAll = pluginConfig.getInt("max-rank-all-item");
		minItemAll = pluginConfig.getInt("min-rank-all-item");
		
		maxItemRank = pluginConfig.getInt("max-rank-item");
		minItemRank = pluginConfig.getInt("min-rank-item");
		
		Bukkit.getConsoleSender().sendMessage(PREFIX + "Reading location.yml...");
		loadConfiguration();
		readConfiguration();
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
		for(String worldName : world) {
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
	
	public void readConfiguration() {
		ConfigurationSection section;
		Map<String,Object> keyValue;
		World worldz;
		double x;
		double y;
		double z;
		String value;
		String[] keySplit;
		String[] locationSplit;
		FixedMetadataValue meta;
		Location loc;
		for(String worldName : world) {
			section = locationConfig.getConfigurationSection("location-" + worldName);
			keyValue = section.getValues(false);
			for(java.util.Map.Entry<String, Object> set : keyValue.entrySet()) {
				value = (String) set.getValue();
				keySplit = set.getKey().split("-");
				locationSplit = value.split(",");
				try {
					x = Double.parseDouble(locationSplit[0]);
					y = Double.parseDouble(locationSplit[1]);
					z = Double.parseDouble(locationSplit[2]);
					worldz = Bukkit.getWorld(locationSplit[3]);
					meta = new FixedMetadataValue(plugin,keySplit[0]);
					loc = new Location(worldz,x,y,z);
					if(!location.get(worldName).contains(loc)) {
						loc.getBlock().setMetadata(keySplit[0], meta);
						location.get(worldName).add(loc);
					}
				}catch(NumberFormatException e) {
					System.out.print(e);
				}
			
			}
		}
		
	}
	
	public void saveLocation() {
		ConfigurationSection section;
		for(String w : world) {
			section = locationConfig.getConfigurationSection("location-" + w);
			if(writeLocation.size() != 0) {
				Collections.sort(writeLocation);
				Collections.reverse(writeLocation);
				currentCounter -= writeLocation.size();
				for(ChestProperty c : writeLocation) {
					location.get(w).add(c.getLocation());
					String loc = c.getLocation().getX() + "," +
					c.getLocation().getY() + "," + c.getLocation().getZ() + "," + c.getLocation().getWorld().getName();
					section.set(c.getRarity() + "-" + currentCounter, loc);
					currentCounter += 1;
				}
				
			}
			saveConfiguration();
		}
	}
	
	public Entry<String,Float> generateChestType() {
		float t = 0;
		float probability;
		for(Entry<String, Float> entry : rarity.getRanking().entrySet()) {
			probability = entry.getValue();
			t = t + probability;
			//Uniform distribution ranging from [0,1]	
			double outcome = random.nextFloat() * rarity.getTotalProbability();
			if(t > outcome) {
				return entry;
			}	
		}
		return null;
	}

	public void generateLocation() {
		
		Bukkit.getConsoleSender().sendMessage(PREFIX + "Generating locations for chests...");
		Bukkit.getConsoleSender().sendMessage(PREFIX + "If this is your first time this may take a while depends on max chest population for each world");
		Bukkit.getConsoleSender().sendMessage(PREFIX + "Consider change spigot.yml \'timeout\' property to higher if your server crash...");
		
		double range = (positiveBoundary - negativeBoundary) + 1;
		World w = null;
		List<Location> currentWorld = null;
		double x;
		double y;
		double z;
		double newX;
		double newY;
		double newZ;
		Location loc;
		int itemAmountRank;
		int itemAmountAll;
		Entry<String,Float> entry;
		for(Entry<String,Object> set : worlds.entrySet()) {
			try {
				currentWorld = location.get(set.getKey());
				maxChestPopulation = Integer.parseInt(set.getValue().toString());
				w = Bukkit.getServer().getWorld(set.getKey());
			}catch(NumberFormatException e) {
				Bukkit.getConsoleSender().sendMessage(PREFIX + "Cannot parse amount of chest population for world \'" + set.getKey() + "\'");
				return;
			}catch(NullPointerException e) {
				Bukkit.getConsoleSender().sendMessage(PREFIX + "Make sure \'" + set.getKey() + "\' exists...");
				Bukkit.getConsoleSender().sendMessage(PREFIX + "Make sure you dont delete lines in location.yml");
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
					Bukkit.getConsoleSender().sendMessage(PREFIX  + "GENERATING CHESTS FOR THE_END MAY TAKES MORE TIME !!!");
				}
				Bukkit.getConsoleSender().sendMessage(PREFIX + "Generating chests for world \'" + w.getName() + "\'");
				currentCounter = currentWorld.size();
				for(int i = 0; i < maxChestPopulation; i++) {
					if(currentWorld.size() >= maxChestPopulation) {
						Bukkit.getConsoleSender().sendMessage(PREFIX + "Chests generation for world " + "\'" + w.getName() + "\' full-filled...");
						break;
					}	
					itemAmountRank = (int) (Math.random() * ((maxItemRank - minItemRank) + 1)) + minItemRank;
					itemAmountAll = (int) (Math.random() * ((maxItemAll - minItemAll ) + 1)) + minItemAll;
					
					x = (random.nextFloat() * range) + negativeBoundary;
					y = (random.nextFloat() * ((maxHeight - minHeight) + 1)) + minHeight;
				    z = (random.nextFloat() * range) + negativeBoundary;
					if(w.getEnvironment() == Environment.THE_END) {
						if(currentWorld.size() >= maxChestPopulation) {
							Bukkit.getConsoleSender().sendMessage(PREFIX + "Chests generation for world " + "\'" + w.getName() + "\' full-filled...");
							continue;
						}
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
							newX = (random.nextFloat() * range) + negativeBoundary;
							x = newX;
							newY = (random.nextFloat() * maxHeight) + 1;
							y = newY;
							newZ = (random.nextFloat() * range) + negativeBoundary;
							z = newZ;
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
										newZ = (random.nextFloat() * range) + negativeBoundary;
										z = newZ;
									}
								}else {
									newX = (random.nextFloat() * range) + negativeBoundary;
									x = newX;
								}	
							}else {
								newY = (random.nextFloat() * maxHeight) + 1;
								y = newY;
							}
						}
					}
					loc = new Location(w,(double)(int)x,(double)(int)y,(double)(int)z);
					currentCounter += 1;
					if(currentCounter % 100 == 0) {
						Bukkit.getConsoleSender().sendMessage(PREFIX + " " + currentCounter + " chests generated...");
					}
					entry = generateChestType();
					writeLocation.add(new ChestProperty(plugin,loc,entry.getKey(),itemAmountAll,itemAmountRank,entry.getValue()));
				}
				saveLocation();
				writeLocation.clear();
			}
		}
		
	}
	
	
}
