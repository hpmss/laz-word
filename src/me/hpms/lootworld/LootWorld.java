package me.hpms.lootworld;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class LootWorld extends JavaPlugin{
	
	private File path = new File(this.getDataFolder() + "");
	
	@Override
	public void onEnable() {
		getDataFolder().mkdir();
		List<String> lst = new ArrayList<String>();
		List<String> enchantments = new ArrayList<String>();
		lst.add("Test");
		enchantments.add("Test");
		ItemConfig item = new ItemConfig(path,Material.BOOK,1,"Book",lst,enchantments);
	}
	
	@Override 
	public void onDisable() {
		
	}
}
