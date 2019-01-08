package me.hpms.lootworld;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
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
		getJSONData();
		getServer().getPluginManager().registerEvents(this, this);
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
		
		if(p.getName().equalsIgnoreCase("Arest")) {
			p.getInventory().addItem(item.getItem());
			p.sendMessage("-> hifumi added...");
		}
	}
	
	
	public boolean generateEnchantmentJSON() throws IOException {
		File file = null;
		BufferedWriter writer = null;
		
		
		for(File f: getDataFolder().listFiles()) {
			if(f.getName().equalsIgnoreCase("test.json")) {
				Log.info("-> file already exists...");
				return false;
			}
			else {
				file = new File(getDataFolder().getPath() + "/test.json");
				try {
					file.createNewFile();
					Log.info("-> test.json created...");
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(file != null) {
			try {
				writer = new BufferedWriter(new FileWriter(file.getPath()));
				writer.write("Hello World !");
				writer.write("This is a new line!");
				
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
	
	public void getJSONData() {
		InputStream in = getClass().getClassLoader().getResourceAsStream("res/text.txt");
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
