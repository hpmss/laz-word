package me.hpms.lootworld;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

@SuppressWarnings("serial")
public class ChestProperty implements Serializable{
	
	private LootWorld plugin;
	
	private Location loc;

	private ConfigurationSection section;
	
	private Chest chest;
	
	private String rarity;
	
	private String currentChest;
	
	private List<ItemStack> content;
	
	private int itemAmount;
	
	private int itemAmountRank;
	
	private float probabilityDistribution;
	
	private FixedMetadataValue metaData;

	
	public ChestProperty(LootWorld lw,Location loc,String rarity,int itemAmount,int itemAmountRank,float probabilityDistribution,ConfigurationSection section,String currentChest) {
		plugin = lw;
		this.loc = loc;
		this.rarity = rarity;
		this.itemAmount = itemAmount;
		this.itemAmountRank = itemAmountRank;
		this.probabilityDistribution = probabilityDistribution;
		this.metaData = new FixedMetadataValue(plugin,rarity);
		this.section = section;
		this.currentChest = currentChest;
		
		this.loc.getBlock().setType(Material.CHEST);
		
		this.chest = (Chest) loc.getBlock().getState();
		
		chest.setMetadata(rarity, metaData);
		
		this.content = populateChestItem();
		
		
	}
	
	public Chest getChestBlock() {
		return chest;
	}
	
	public String getRarity() {
		return rarity;
	}
	
	public List<ItemStack> getChestContent() {
		return content;
	}
	
	public float getChestDistributionRate() {
		return probabilityDistribution;
	}
	
	public FixedMetadataValue getFixedMetadata() {
		return metaData;
	}
	
	public LootWorld getPlugin() {
		return this.plugin;
	}
	
	public void saveChest() {
		String locString = loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getWorld().getName();
		section.set(getRarity() + "-" + currentChest, locString);
		
	}
	
	private List<ItemStack> populateChestItem() {
		
		List<ItemStack> itemList = new ArrayList<ItemStack>();
		
		List<ItemStack> rankItem = new ArrayList<ItemStack>();
		
		
		
		for(ItemConfig item : plugin.getParsedItems()) {
			if(item.getRank().equalsIgnoreCase(this.rarity)) {
				rankItem.add(item.getItem());
			}
		}
		
		float probabilityRank = 1f;
		if(rankItem.size() != 0) {
			probabilityRank = probabilityRank / (float)rankItem.size();
		}
		
		
		
		for(int i = 0; i < itemAmount; i ++) {
			double t = 0;
			Collections.shuffle(plugin.getRankAllItems());
			for(Entry<Float, ItemStack> item : plugin.getRankAllItemsDistribution().entrySet()) {
				t += item.getKey();
				double outcome = Math.random();
				if(t >= outcome) {
					itemList.add(item.getValue());
				}
			}
		}
		
		for(int i = 0; i < itemAmountRank ; i ++) {
			double t = 0;
			HashMap<Float,ItemStack> mapRankItem = new HashMap<Float,ItemStack>();
			Collections.shuffle(rankItem);
			for(ItemStack item : rankItem) {
				mapRankItem.put(probabilityRank, item);
			}
			for(Entry<Float,ItemStack> item : mapRankItem.entrySet()) {
				t += item.getKey();
				double outcome = Math.random();
				if(t >= outcome) {			
					itemList.add(item.getValue());
				}
			}
		}
		
		for(ItemStack item : itemList) {
			chest.getInventory().addItem(item);
		}
		
		return itemList;
		
	}
	

}
