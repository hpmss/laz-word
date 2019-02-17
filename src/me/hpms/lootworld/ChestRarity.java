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

public class ChestRarity {
	
	private static FileConfiguration pluginConfig;
	
	private static LinkedHashMap<String,Float> rank;
	
	private static float totalProbability;
	
	public static void init() {
		pluginConfig = LootWorld.plugin.getConfig();
		rank = new LinkedHashMap<String,Float>();
		loadRank();
		rank =  (LinkedHashMap<String, Float>) sortByValue(rank);
	}
	
	public static HashMap<String,Float> getRanking() {
		return rank;
	}
	
	public static float getTotalProbability() {
		return totalProbability;
	}
	
	public static float getProbabilityByName(String name) {
		float prob;
		if(!rank.containsKey(name)) {
			throw new NullPointerException("Rank not found...");
		}
		prob = rank.get(name);
		return prob;
	}
	
	private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());
        Collections.reverse(list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
	
	private static void loadRank() {
		
		ConfigurationSection section = pluginConfig.getConfigurationSection("rank");
		Map<String,Object> rankMap = section.getValues(false);
		for(Map.Entry<String,Object> entry : rankMap.entrySet()) {
			try {
				float prob = Float.parseFloat(entry.getValue().toString());
				if(String.valueOf(prob).startsWith("0.")) {
					prob = prob * 100;
				}
				totalProbability += prob;
				rank.put(entry.getKey().toString(), prob);
			}catch(NumberFormatException e ) {
				Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "Cannot parse probability for rank \'" + entry.getKey() + "\'");
			}	
		}
		
	}
	
}
	
