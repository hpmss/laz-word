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
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ItemParser {
	
	
	/* TODO
	 * -> Fix 'lore' casting to ArrayList<String>
	 * -> fix getResourceAsStream() NPE
	 * 
	 */
	
	private Material material = Material.BED;
	private String name = material.name();
	
	private int amount = 1;
	private List<String> lore = new ArrayList<String>();
	private HashMap<String,Integer> enchantment = null;
	private float probability = 1f;
	private ArrayList<ItemStack> items = null;
	
	
	private File getDataFolder;
	
	
	public ItemParser(File getDataFolder) {
		this.getDataFolder = getDataFolder;
		parseCustomItem(getDataFolder);
		getJSONData(getDataFolder);
		try {
			generateEnchantmentJSON();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<ItemStack> getParsedItems() {
		return this.items;
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean parseCustomItem(File file) {
		
		JSONParser parser = new JSONParser();
		
		File itemPath = new File(file.getPath() + "/items.json");
		
			try {
				JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(itemPath));
				
				//Get every items in items.json
				for(Object key : jsonObj.keySet()) {
					JSONObject keyObject = (JSONObject) jsonObj.get(key);
					//Loops through each item
					HashMap<String,Object> itemMap = (HashMap<String,Object>) keyObject;
					for(Entry<String, Object> set : itemMap.entrySet()) {
						String itemKey = set.getKey();
						Object itemValue = set.getValue();
						try {
							switch(itemKey) {
							case "name": this.name = itemValue.toString();
							case "material": this.material = Material.valueOf(itemValue.toString());
							case "amount": this.amount = Integer.parseInt(itemValue.toString());
							case "lore": this.lore = (ArrayList<String>) itemValue;
							case "enchantment": this.enchantment = (HashMap<String,Integer>) itemValue;
							case "probability": this.probability = Float.parseFloat(itemValue.toString());
								
							}
						}catch(NumberFormatException e) {
							System.out.print("-> either \'probability\' or \'amount\' have incorrect format...");
							return false;
							
						}catch(ClassCastException e) {
							System.out.print(itemKey);
							System.out.print(itemValue);
							System.out.print("-> make sure \'material\' is valid...");
							System.out.print("-> make sure \'enchantment\' has key(enchantment_name),value(level) format...");
							System.out.print("-> make sure \'lore\' is a list of strings");
							e.printStackTrace();
							return false;
						}
						
						
					}
					ItemConfig item = new ItemConfig(getDataFolder,name,material,amount,lore,enchantment,probability);
					items.add(item.getItem());
					
					
					
				}
				
			} catch (FileNotFoundException e) {
				Log.info("-> items.json is not found...");
				return false;
			} catch (IOException e) {
				Log.info("-> items.json seems to be corrupted...");
				return false;
			} catch (ParseException e) {
				Log.info("-> items.json is unparsable...");
				return false;
			}
			
			return true;
			
		}
		
	
	public boolean generateEnchantmentJSON() throws IOException {
		File file = new File(getDataFolder.getPath() + "/enchantment.json");
		BufferedWriter writer = null;
		
		if(file.exists()) {
			Log.info("-> enchantment.json already existed...");
			return false;
		}else {
			try {
				file.createNewFile();
				writer = new BufferedWriter(new FileWriter(file.getPath()));
				Log.info("-> new enchantment.json created...");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				if(writer != null) {
					writer.close();
				}
			}
		}
		return true;
		
	}
	
	public void getJSONData(File file) {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("res/enchantment.json");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		try {
			while((line = reader.readLine()) != null) {
				System.out.print(line); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
