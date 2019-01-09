package me.hpms.lootworld;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class ChestProperty {
	
	private LootWorld plugin;
	
	private String rarity;
	
	private List<ItemStack> content;
	
	private float probabilityDistribution;
	
	private FixedMetadataValue metaData;
	
	
	
	public ChestProperty(LootWorld lw,String rarity,List<ItemStack> content,float probabilityDistribution,FixedMetadataValue metaData ) {
		plugin = lw;
		this.rarity = rarity;
		this.content = content;
		this.probabilityDistribution = probabilityDistribution;
		this.metaData = metaData;
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
	
	
	
	

}
