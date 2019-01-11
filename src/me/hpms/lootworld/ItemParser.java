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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ItemParser {
	
	private String PREFIX = "[LootWorld]";
	
	private String stringEnchantment;
	
	private Material material = Material.BED;
	
	private String name = material.toString();

	private int amount = 1;
	
	private List<String> lore = new ArrayList<String>();
	
	private HashMap<String,Integer> enchantment = null;
	
	private String rank = "all";
	
	private ArrayList<ItemConfig> items = new ArrayList<ItemConfig>();
	
	private ArrayList<ItemStack> rankAllItems = new ArrayList<ItemStack>();
	
	private int rankAllCounter = 0;
	
	
	private File getDataFolder;
	
	
	public ItemParser(File getDataFolder) {
		this.getDataFolder = getDataFolder;
		try {
			generateRequiredJSONFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		parseCustomItem(getDataFolder);
	}
	
	public List<ItemConfig> getParsedItems() {
		return this.items;
	}
	
	public List<ItemStack> getRankAllItems() {
		return this.rankAllItems;
	}
	
	public HashMap<Float,ItemStack> getRankAllItemsDistribution() {
		HashMap<Float,ItemStack> items = new HashMap<Float,ItemStack>();
		if(rankAllCounter != 0) {
			float prob = 1.0f / (float)rankAllCounter;
			
			for(ItemStack item : rankAllItems) {
				items.put(prob, item);
			}
		}
		return items;
	}
	
	//Class Methods
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
							case "name": this.name = itemValue.toString();break;
							case "material": this.material = Material.valueOf(itemValue.toString());break;
							case "amount": this.amount = Integer.parseInt(itemValue.toString());break;
							case "lore": this.lore = convertColor((List<String>) itemValue);break;
							case "enchantment": this.stringEnchantment = itemValue.toString();this.enchantment = convertStringToHashMap(stringEnchantment);break;
							case "rank": this.rank = itemValue.toString();break;
							}
						}catch(NumberFormatException e) {
							Log.info(PREFIX + "-> either \'amount\' have incorrect format...");
							return;
							
						}catch(ClassCastException e) {
							Log.info(PREFIX + "-> make sure \'material\' is valid...");
							Log.info(PREFIX + "-> make sure \'enchantment\' has key(enchantment_name),value(level) format...");
							Log.info(PREFIX + "-> make sure \'lore\' is a list of strings");
							e.printStackTrace();
							return;
						}
					}
					
					ItemConfig item = new ItemConfig(file,name,material,amount,lore,enchantment,rank);
					if(rank.equalsIgnoreCase("all")) {
						rankAllItems.add(item.getItem());
						rankAllCounter +=  1;
					}else {
						items.add(item);
					}
					
					
				}
				
			} catch (FileNotFoundException e) {
				Log.info(PREFIX + "-> items.json is not found...");
				return;
			} catch (IOException e) {
				Log.info(PREFIX + "-> items.json seems to be corrupted...");
				return;
			} catch (ParseException e) {
				Log.info(PREFIX + "-> items.json is unparsable...");
				return;
			}
			
			return;
			
		}
	
	public void resetDefault() {
		material = Material.BED;
		name = material.name();

		amount = 1;
		lore = new ArrayList<String>();
		enchantment = null;
		rank = "all";
	}
	
	public HashMap<String,Integer> convertStringToHashMap(String stringHashMap) {
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
						Log.info(PREFIX + "-> make sure level specified is a valid integer...");
						e.printStackTrace();
					}
				}else {
					Log.info(PREFIX + "-> level for \'" + entry[0] + "\' not specified ? ...");
				}
			}
		}
		return enchantment;
	}
		
	public void generateRequiredJSONFile() throws IOException {
		File fileEnchantment = new File(getDataFolder.getPath() + "/enchantment.json");
		File fileItem = new File(getDataFolder.getPath() + "/items.json");
		BufferedWriter writer = null;
		
		if(fileEnchantment.exists()) {
			Log.info( PREFIX + "-> enchantment.json already existed...");
		}else {
			try {
				fileEnchantment.createNewFile();
				writer = new BufferedWriter(new FileWriter(fileEnchantment.getPath()));
				getJSONData("enchantment.json",writer);
				Log.info( PREFIX + "-> new enchantment.json file created...");
				
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
			Log.info(PREFIX + "-> items.json already existed...");
			return;
		}else {
			try {
				fileItem.createNewFile();
				writer = new BufferedWriter(new FileWriter(fileItem.getPath()));
				getJSONData("items.json",writer);
				Log.info(PREFIX + "-> new items.json file created...");
				
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
	
	public List<String> convertColor(List<String> list) {
		List<String> converted = new ArrayList<String>();
		for(String s : list) {
			converted.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		return converted;
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
