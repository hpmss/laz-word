package me.hpms.lootworld;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class LootWorld extends JavaPlugin implements Listener{
	
	private File path = new File(this.getDataFolder() + "");
	
	@Override
	public void onEnable() {
		getDataFolder().mkdir();
		
	}
	
	@Override 
	public void onDisable() {
		
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		
		List<String> lst = new ArrayList<String>();
		HashMap<String,Integer> enchantments = new HashMap<String,Integer>();
		lst.add("Test");
		enchantments.put("FiRe AsPect",2);
		ItemConfig item = new ItemConfig(path,Material.DIAMOND_SWORD,1,0.5f,ChatColor.DARK_PURPLE + "Hifumi",lst,enchantments);
		Player p = e.getPlayer();
		
		if(p != null && p.getName().equalsIgnoreCase("Arest")) {
			p.getInventory().addItem(item.getItem());
			p.sendMessage("-> hifumi added...");
		}
	}
	
	
}
