package me.hpms.lootworld;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ItemConfig{

	private File path;
	private ItemStack itemStack;

	private String displayName;
	private List<String> itemLore;
	private HashMap<String,Integer> configuredEnchantment;
	private HashMap<String,Integer> usedEnchantmentMap;
	private Material material;
	private int amount;
	private float probability;


	public ItemConfig(File path,Material material,int amount,float probability,String displayName,List<String> itemLore,HashMap<String,Integer> enchantments) {
		this.displayName = displayName;
		this.itemLore = itemLore;
		this.material = material;
		this.amount = amount;
		this.configuredEnchantment = enchantments;
		this.path = path;
		this.probability = probability;
		this.usedEnchantmentMap = initializeEnchantName();
		this.itemStack = initializeItem();

	}
	public ItemConfig(File path,Material material,int amount,float probability) {
		this.path = path;
		this.material = material;
		this.amount = amount;
		this.displayName = material.name();
		this.itemLore = new ArrayList<String>();
		this.usedEnchantmentMap = null;
		this.probability = probability;
		this.itemStack = initializeItem();
	}
	
	public ItemConfig(Material material) {
		this.material = material;
		this.amount = 1;
		this.displayName = material.name();
		this.itemLore = new ArrayList<String>();
		this.usedEnchantmentMap = null;
		this.probability = 1f;
		this.itemStack = initializeItem();
	}
	
	//Class properties

	public ItemStack getItem() {
		return this.itemStack;
	}

	public String getItemName() {
		return this.displayName;
	}

	public List<String> getItemLore() {
		return this.itemLore;
	}
	
	public float getProbability() {
		return this.probability;
	}
	
	public HashMap<String,Integer> getUsedEnchantment() {
		return this.usedEnchantmentMap;
	}
	
	//Class Methods

	private HashMap<String,Integer> initializeEnchantName(){

		HashMap<String,Integer> usedEnchantment = new HashMap<String,Integer>();
		
		JSONParser parser = new JSONParser();

		for (File file : path.listFiles()) {
			try {
				String[] name = file.getName().split("[.]");
				if(name[1].equalsIgnoreCase("json")) {

					Object obj = parser.parse(new FileReader(file.getPath()));
					JSONObject jsonObj = (JSONObject) obj;
					JSONArray element = (JSONArray)jsonObj.get("enchantment");
					
					@SuppressWarnings("unchecked")
					//Get JSON enchantment dictionary
					HashMap<String,List<String>> elementMap = (HashMap<String, List<String>>) element.get(0);
					if(this.configuredEnchantment != null ) {
						for(Entry<String, List<String>> en : elementMap.entrySet()) {
							for(Entry<String,Integer> enchantListed: this.configuredEnchantment.entrySet()) {
								String deCapName = enchantListed.getKey().toLowerCase();
								if(en.getValue().contains(deCapName)) {
									usedEnchantment.put(en.getKey(), enchantListed.getValue());
								}
							}
						}
					}else {
						usedEnchantment = null;
					}
				
				}
			}catch(IOException e) {
				Log.info("-> enchantment.json file does not found...");

			}catch(ParseException e) {
				Log.info("-> enchantment.json file cannot be parsed...");
			}

		}
		return usedEnchantment;
	}
	

	private ItemStack initializeItem() {

		ItemStack itemStack = new ItemStack(this.material,this.amount);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(this.displayName);
		meta.setLore(itemLore);
		if(this.usedEnchantmentMap != null) {
			for(Entry<String,Integer> e: usedEnchantmentMap.entrySet()) {
				meta.addEnchant(Enchantment.getByName(e.getKey()),e.getValue(),true);
			}
		}
		itemStack.setItemMeta(meta);

		return itemStack;
	}


}
