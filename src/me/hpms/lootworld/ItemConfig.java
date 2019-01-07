package me.hpms.lootworld;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
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
	private Material material;
	private int amount;
	private List<String> enchantments;
	private HashMap<List<String>,String> enchantMap = new HashMap<List<String>,String>();


	enum Enchantment {
		ARROW_DAMAGE,ARROW_FIRE,ARROW_INFINITE,ARROW_KNOCKBACK,BINDING_CURSE,
		CHANNELING,DAMAGE_ALL,DAMAGE_ARTHROPODS,DAMAGE_UNDEAD,DEPTH_STRIDER,
		DIG_SPEED,DURABILITY,FIRE_ASPECT,FROST_WALKER,IMPALING,KNOCKBACK,LOOT_BONUS_BLOCKS,
		LOOT_BONUS_MOBS,LOYALTY,LUCK,FIRE,MENDING,OXYGEN,PROTECTION_ENVIRONMENTAL,PROTECTION_EXPLOSIONS,
		PROTECTION_FALL,PROTECTION_FIRE,PROTECTION_PROJECTILE,RIPTIDE,SILK_TOUCH,SWEEPING_EDGE,THORNS,
		VANISHING_CURSE,WATER_WORKER;
	}

	public ItemConfig(File path,Material material,int amount,String displayName,List<String> itemLore,List<String> enchantments) {
		this.displayName = displayName;
		this.itemLore = itemLore;
		this.material = material;
		this.amount = amount;
		this.enchantments = enchantments;
		this.path = path;

		this.itemStack = iniItem();
		this.enchantMap = initEnchantName();

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

	private HashMap<List<String>,String> initEnchantName(){

		HashMap<List<String>,String> map = new HashMap<List<String>,String>();
		Enchantment[] enchantment = Enchantment.values();
		JSONParser parser = new JSONParser();

		for (File file : path.listFiles()) {
			try {
				String[] name = file.getName().split("[.]");
				if(name[1].equalsIgnoreCase("json")) {

					Object obj = parser.parse(new FileReader(file.getPath()));
					JSONObject jsonObj = (JSONObject) obj;

					for(Enchantment enchantKey : enchantment) {
						JSONArray enchantArray = (JSONArray) jsonObj.get(enchantKey);
						for(Object enName : enchantArray) {
							String s = (String) enName;
							Log.info(s);
						}
					}
				}

			}catch(IOException e) {
				Log.info("-> enchantment.json file does not found...");

			}catch(ParseException e) {
				Log.info("-> enchantment.json file cannot be parsed...");
			}

		}

		return map;
	}

	private ItemStack iniItem() {

		ItemStack itemStack = new ItemStack(this.material,this.amount);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(this.displayName);
		meta.setLore(itemLore);

		return itemStack;
	}


}
