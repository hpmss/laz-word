package me.hpms.lootworld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class ChestProperty {
	
	private LootWorld plugin;
	
	private Location loc;
	
	private Chest chest;
	
	private String rarity;
	
	private List<ItemStack> content;
	
	private int itemAmount;
	
	private int itemAmountRank;
	
	private float probabilityDistribution;
	
	private FixedMetadataValue metaData;

	
	public ChestProperty(LootWorld lw,Location loc,String rarity,int itemAmount,int itemAmountRank,float probabilityDistribution) {
		plugin = lw;
		this.loc = loc;
		this.rarity = rarity;
		this.itemAmount = itemAmount;
		this.itemAmountRank = itemAmountRank;
		this.probabilityDistribution = probabilityDistribution;
		this.metaData = new FixedMetadataValue(plugin,rarity);
		
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
		
		double t = 0;
		
		for(int i = 0; i < itemAmount; i ++) {
			Collections.shuffle(plugin.getRankAllItems());
			for(Entry<Float, ItemStack> item : plugin.getRankAllItemsDistribution().entrySet()) {
				t += item.getKey();
				double outcome = Math.random();
				if(t >= outcome) {
					itemList.add(item.getValue());
				}
			}
			t = 0;
		}
		
		t = 0;
		for(int i = 0; i < itemAmountRank ; i ++) {
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
