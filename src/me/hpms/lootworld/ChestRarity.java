package me.hpms.lootworld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.api.ChatColor;

public class ChestRarity {
	
	private final String PREFIX = ChatColor.GREEN + "『 LootWorld 』" + ChatColor.BLUE + "-> ";
	
	private LootWorld plugin;
	
	private FileConfiguration pluginConfig;
	
	private LinkedHashMap<String,Float> rank;
	
	private float totalProbability;
	
	public ChestRarity(LootWorld lw) {
		plugin = lw;
		pluginConfig = plugin.getConfig();
		rank = new LinkedHashMap<String,Float>();
		loadRank();
		this.rank =  (LinkedHashMap<String, Float>) sortByValue(rank);
		
	}
	
	public HashMap<String,Float> getRanking() {
		return rank;
	}
	
	public float getTotalProbability() {
		return this.totalProbability;
	}
	
	public float getProbability(String rankName) {
		if(rank.containsKey(rankName)) {
			float prob = rank.get(rankName);
			return prob;
		}else {
			Bukkit.getConsoleSender().sendMessage(PREFIX + "Cannot parse probability for rank \'" + rankName + "\'");
			return -1;
		}
		
	}
	
	public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());
        Collections.reverse(list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
	
	private void loadRank() {
		
		ConfigurationSection section = pluginConfig.getConfigurationSection("rank");
		Map<String,Object> rankMap = section.getValues(false);
		for(Entry<String,Object> entry : rankMap.entrySet()) {
			try {
				float prob = Float.parseFloat(entry.getValue().toString());
				if(String.valueOf(prob).startsWith("0.")) {
					prob = prob * 100;
				}
				totalProbability += prob;
				rank.put(entry.getKey().toString(), prob);
			}catch(NumberFormatException e ) {
				Bukkit.getConsoleSender().sendMessage(PREFIX + "Cannot parse probability for rank \'" + entry.getKey() + "\'");
			}	
		}
		
	}
	
}
	
