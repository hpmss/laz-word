package me.hpms.lootworld;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class ChestProperty {
	
	private LootWorld plugin;
	
	private Block chest;
	
	private String rarity;
	
	private List<ItemStack> content;
	
	private float probabilityDistribution;
	
	private FixedMetadataValue metaData;
	
	
	public ChestProperty(LootWorld lw,Block chest,String rarity,List<ItemStack> content,float probabilityDistribution,FixedMetadataValue metaData ) {
		plugin = lw;
		this.chest = chest;
		this.rarity = rarity;
		this.content = content;
		this.probabilityDistribution = probabilityDistribution;
		this.metaData = metaData;
	}
	
	public Block getChestBlock() {
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
	
	
	
	

}
