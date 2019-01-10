package me.hpms.lootworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import net.md_5.bungee.api.ChatColor;

public class LocationGenerator {
	
	private final String PREFIX = ChatColor.GREEN + "『 LootWorld 』" + ChatColor.BLUE + "-> ";
	
	private final File locationFile;
	
	private FileConfiguration locationConfig;
	
	private List<Location> location;
	
	private LootWorld plugin;
	
	private final int maxChestPopulation = 1000;
	
	private final double positiveBoundary = 99984;
	
	private final double negativeBoundary = -99984;
	
	private final double xOffset = 1000f;
	
	private final double zOffset = 1000f;
	
	
	
	public LocationGenerator(LootWorld lw) {
		plugin = lw;
		location = new ArrayList<Location>();
		locationFile = new File(plugin.getDataFolder(), "location.yml");
		
		loadConfiguration();
		readConfigurationToLocation();
		generateLocation();
		readConfigurationToLocation();
		
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
	
	
	public ChestRarity generateChestType() {
		Class<ChestRarity> rarity = ChestRarity.class;
		ChestRarity[] chest = rarity.getEnumConstants();
		float t = 0;
		for(ChestRarity c : chest) {
			
			float probability = c.getChestProbability();
			t = t + probability;
			
			//Uniform distribution ranging from [0,1]
			double outcome = Math.random() * 100;
			
			if(t >= outcome) {
				return c;
			}	
			
		}
		return null;
		
	}

	public void generateLocation() {
		
		if(location.size() == maxChestPopulation) {
			Log.info(PREFIX + "Chest amount full-filled...");
			return;
		}
		Log.info(PREFIX + "Generating locations for chests...");
		Log.info(PREFIX + "If this is your first time this may take a while depends on maxChestPopulation");
		ConfigurationSection section = locationConfig.getConfigurationSection("location");
		double range = (positiveBoundary - negativeBoundary) + 1;
		double maxHeight = 256;
		
		World world = Bukkit.getWorld("world");
		for(int i = 0; i < maxChestPopulation; i++) {
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
			loc.getBlock().setType(Material.CHEST);
			Block b = loc.getBlock();
			FixedMetadataValue meta = new FixedMetadataValue(plugin,loc);
			ChestRarity entry = generateChestType();
			
			ChestProperty chest = new ChestProperty(plugin,b,entry.getChestName(),new ArrayList<ItemStack>(),entry.getChestProbability(),meta);
			String locString = loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getWorld().getName();
			section.set(chest.getRarity()+ "-" + String.valueOf(i), locString);
			saveConfiguration();
			
		}
		
		
	}
	
	

}
