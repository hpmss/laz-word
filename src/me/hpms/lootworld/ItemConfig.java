package me.hpms.lootworld;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.md_5.bungee.api.ChatColor;

public class ItemConfig{
	
	private File path;
	
	private ItemStack itemStack;

	private String displayName;
	
	private Material material;
	
	private int amount;
	
	private List<String> itemLore;
	
	private HashMap<String,Integer> configuredEnchantment;
	
	private HashMap<String,Integer> usedEnchantmentMap;
	
	private String rank;
	


	public ItemConfig(File path,String displayName,Material material,int amount,List<String> itemLore,HashMap<String,Integer> enchantments,String rank) {
		this.displayName = displayName;
		this.itemLore = itemLore;
		this.material = material;
		this.amount = amount;
		this.configuredEnchantment = enchantments;
		this.path = path;
		this.rank = rank;
		try {
			this.usedEnchantmentMap = initializeEnchantName();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		this.itemStack = initializeItem();

	}

	public ItemStack getItem() {
		return this.itemStack;
	}

	public String getItemName() {
		return this.displayName;
	}

	public List<String> getItemLore() {
		return this.itemLore;
	}
	
	public String getRank() {
		return this.rank;
	}
	
	public HashMap<String,Integer> getUsedEnchantment() {
		return this.usedEnchantmentMap;
	}
	
	//Class Methods
	@SuppressWarnings("unchecked")
	private HashMap<String,Integer> initializeEnchantName() throws FileNotFoundException, IOException, ParseException{

		HashMap<String,Integer> usedEnchantment = new HashMap<String,Integer>();
		
		JSONParser parser = new JSONParser();
		File file = new File(path.getPath() + "/enchantment.json");
		Object obj = parser.parse(new FileReader(file.getPath()));
		JSONObject jsonObj = (JSONObject) obj;
		
		JSONObject element = (JSONObject)jsonObj.get("enchantment");
		//Get JSON enchantment dictionary
		HashMap<String,List<String>> elementMap = (HashMap<String, List<String>>) element;
		if(this.configuredEnchantment != null ) {
			for(Entry<String, List<String>> en : elementMap.entrySet()) {
				for(Entry<String,Integer> enchantListed: this.configuredEnchantment.entrySet()) {
					String deCapName = enchantListed.getKey().toLowerCase();
					deCapName = deCapName.substring(1,deCapName.length() -1);
					if(en.getValue().contains(deCapName)) {
						usedEnchantment.put(en.getKey(), enchantListed.getValue());
					}
				}
			}
		}else {
			usedEnchantment = null;
		}

		return usedEnchantment;
	}
	
	private ItemStack initializeItem() {

		ItemStack itemStack = new ItemStack(this.material,this.amount);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',this.displayName));
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
