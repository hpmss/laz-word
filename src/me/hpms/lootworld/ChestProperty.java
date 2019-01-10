package me.hpms.lootworld;

import java.util.ArrayList;
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
	
	private float probabilityDistribution;
	
	private FixedMetadataValue metaData;

	
	public ChestProperty(LootWorld lw,Location loc,String rarity,int itemAmount,float probabilityDistribution) {
		plugin = lw;
		this.loc = loc;
		this.rarity = rarity;
		this.itemAmount = itemAmount;
		this.probabilityDistribution = probabilityDistribution;
		this.metaData = new FixedMetadataValue(plugin,rarity);
		
		loc.getBlock().setType(Material.CHEST);
		
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
		
		HashMap<Float,ItemStack> mapRankItem = new HashMap<Float,ItemStack>();
		
		for(ItemConfig item : plugin.getParsedItems()) {
			if(item.getRank().equalsIgnoreCase(this.rarity)) {
				rankItem.add(item.getItem());
			}
		}
		
		float probabilityRank = 1;
		if(rankItem.size() != 0) {
			probabilityRank = 1 / rankItem.size();
		}
		
		for(ItemStack item : rankItem) {
			mapRankItem.put(probabilityRank, item);
		}
		
		double t = 0;
		
		for(int i = 0; i < itemAmount - 1 ; i ++) {
			for(Entry<Float, ItemStack> item : plugin.getRankAllItemsDistribution().entrySet()) {
				t += item.getKey();
				double outcome = Math.random();
				if(t >= outcome) {
					itemList.add(item.getValue());
				}
			}
		}
		
		t = 0;
		
		for(Entry<Float,ItemStack> item : mapRankItem.entrySet()) {
			t += item.getKey();
			double outcome = Math.random();
			if(t >= outcome) {
				itemList.add(item.getValue());
			}
		}
		for(ItemStack item : itemList) {
			chest.getInventory().addItem(item);
		}
		Log.info(itemList);
		
		return itemList;
		
	}
	

}
