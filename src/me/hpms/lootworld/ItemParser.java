package me.hpms.lootworld;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ItemParser {
	
	private static String stringEnchantment;
	
	private static Material material;
	
	private static String name;

	private static int amount;
	
	private static List<String> lore;
	
	private static HashMap<String,Integer> enchantment;
	
	private static String rank;
	
	private static HashMap<String,ArrayList<ItemConfig>> items;
	
	private static ArrayList<ItemStack> rankAllItems;
	
	private static int rankAllCounter;
	
	private static HashMap<ItemStack,Float> itemsMap;
	
	static {
		
		 material = Material.BED;
		 name = material.toString();
		 amount = 1;
		 lore = new ArrayList<String>();
		 enchantment = null;
		 rank = "all";
		 items = new HashMap<String,ArrayList<ItemConfig>>();
		 rankAllItems = new ArrayList<ItemStack>();
		 rankAllCounter = 0;
		 itemsMap = new HashMap<ItemStack,Float>();
		
	}
	
	private File getDataFolder;
	
	public ItemParser(File getDataFolder) {
		this.getDataFolder = getDataFolder;
		try {
			generateRequiredJSONFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		parseCustomItem(getDataFolder);
		if(rankAllCounter != 0) {
			float prob = 1.0f / (float)rankAllCounter;
			
			for(ItemStack item : rankAllItems) {
				itemsMap.put(item, prob);
			}
		}
	}
	
	public HashMap<String,ArrayList<ItemConfig>> getParsedItems() {
		return items;
	}
	
	public List<ItemStack> getRankAllItems() {
		return rankAllItems;
	}
	
	public HashMap<ItemStack,Float> getRankAllItemsDistribution() {
		return itemsMap;
	}
	
	@SuppressWarnings("unchecked")
	public void parseCustomItem(File file) {
		
		JSONParser parser = new JSONParser();
		
		File itemPath = new File(file.getPath() + "/items.json");
		
			try {
				JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(itemPath));
				
				for(Object key : jsonObj.keySet()) {
					JSONObject keyObject = (JSONObject) jsonObj.get(key);
					resetDefault();
					HashMap<String,Object> itemMap = (HashMap<String,Object>) keyObject;
					for(String keySet : itemMap.keySet()) {
						Object itemValue = itemMap.get(keySet);
						try {
							switch(keySet) {
							case "name": name = itemValue.toString();break;
							case "material": material = Material.valueOf(itemValue.toString());break;
							case "amount": amount = Integer.parseInt(itemValue.toString());break;
							case "lore": lore = convertColor((List<String>) itemValue);break;
							case "enchantment": stringEnchantment = itemValue.toString();enchantment = convertStringToHashMap(stringEnchantment);break;
							case "rank": rank = StringUtils.capitalize(itemValue.toString());break;
							}
						}catch(NumberFormatException e) {
							Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "either \'amount\' have incorrect format...");
							return;
							
						}catch(ClassCastException e) {
							Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "make sure \'material\' is valid...");
							Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "make sure \'enchantment\' has key(enchantment_name),value(level) format...");
							Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "make sure \'lore\' is a list of strings");
							e.printStackTrace();
							return;
						}
					}
					
					ItemConfig item = new ItemConfig(file,name,material,amount,lore,enchantment,rank);
					if(rank.equalsIgnoreCase("all")) {
						rankAllItems.add(item.getItem());
						rankAllCounter +=  1;
					}else {
						if(!items.containsKey(item.getRank())) {
							items.put(item.getRank(), new ArrayList<ItemConfig>());
						}
						items.get(item.getRank()).add(item);
					}
					
					
				}
				
			} catch (FileNotFoundException e) {
				Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "items.json is not found...");
				return;
			} catch (IOException e) {
				Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "items.json seems to be corrupted...");
				return;
			} catch (ParseException e) {
				Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "items.json is unparsable...");
				return;
			}
			
			return;
			
		}
	
	private static void resetDefault() {
		material = Material.BED;
		name = material.name();
		amount = 1;
		lore = new ArrayList<String>();
		enchantment = null;
		rank = "all";
	}
	
	private static HashMap<String,Integer> convertStringToHashMap(String stringHashMap) {
		HashMap<String,Integer> enchantment = new HashMap<String,Integer>();
		
		stringHashMap = stringHashMap.substring(1,stringHashMap.length() - 1);
		String[] keyValue = stringHashMap.split(",");
		if(keyValue.length != 0) {
			for(String pair : keyValue) {
				String[] entry = pair.split(":");
				if(!(pair.endsWith(":")) && entry.length != 0) {
					try {
						enchantment.put(entry[0].trim(), Integer.parseInt(entry[1].trim()));
					}catch(NumberFormatException e) {
						Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "make sure level specified is a valid integer...");
						e.printStackTrace();
					}
				}else {
					Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "level for \'" + entry[0] + "\' not specified ? ...");
				}
			}
		}
		return enchantment;
	}
	
	public static List<String> convertColor(List<String> list) {
		List<String> converted = new ArrayList<String>();
		for(String s : list) {
			converted.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		return converted;
	}
		
	private void generateRequiredJSONFile() throws IOException {
		File fileEnchantment = new File(getDataFolder.getPath() + "/enchantment.json");
		File fileItem = new File(getDataFolder.getPath() + "/items.json");
		BufferedWriter writer = null;
		
		if(fileEnchantment.exists()) {
			return;
		}else {
			try {
				fileEnchantment.createNewFile();
				writer = new BufferedWriter(new FileWriter(fileEnchantment.getPath()));
				getJSONData("enchantment.json",writer);
				Bukkit.getConsoleSender().sendMessage( LootWorld.PREFIX + "new enchantment.json file created...");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				if(writer != null) {
					writer.close();
				}
			}
		}
		if(fileItem.exists()) {
			return;
		}else {
			try {
				fileItem.createNewFile();
				writer = new BufferedWriter(new FileWriter(fileItem.getPath()));
				getJSONData("items.json",writer);
				Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "new items.json file created...");
				
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			finally {
				if(writer != null) {
					writer.close();
				}
			}
		}
	}
	
	public void getJSONData(String fileToReadFromJar,BufferedWriter writer) {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("res/" + fileToReadFromJar);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		try {
			while((line = reader.readLine()) != null) {
				writer.write(line);
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
