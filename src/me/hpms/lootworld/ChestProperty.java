package me.hpms.lootworld;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class ChestProperty {
	
	private LootWorld plugin;
	
	private Chest chest;
	
	private String rarity;
	
	private List<ItemStack> content;
	
	private int itemAmount;
	
	private float probabilityDistribution;
	
	private FixedMetadataValue metaData;
	
	
	public ChestProperty(LootWorld lw,Chest chest,String rarity,int itemAmount,float probabilityDistribution) {
		plugin = lw;
		this.chest = chest;
		this.rarity = rarity;
		this.itemAmount = itemAmount;
		this.probabilityDistribution = probabilityDistribution;
		this.metaData = new FixedMetadataValue(plugin,rarity);
		
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
		
		for(int i = 0; i < itemAmount ; i ++) {
			
		}
		
		return itemList;
		
	}
	
	
	

}
