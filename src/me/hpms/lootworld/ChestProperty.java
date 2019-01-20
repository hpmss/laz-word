package me.hpms.lootworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class ChestProperty implements Comparable<ChestProperty>{
	
	private final String PREFIX = ChatColor.GREEN + "『 LootWorld 』" + ChatColor.BLUE + "-> ";
	
	private LootWorld plugin;
	
	private Location loc;
	
	private Chest chest;
	
	private String rarity;
	
	private List<ItemStack> content;
	
	private int itemAmount;
	
	private int itemAmountRank;
	
	private float probabilityDistribution;
	
	private FixedMetadataValue metaData;
	
	private int id;

	
	public ChestProperty(LootWorld lw,int id,Location loc,String rarity,int itemAmount,int itemAmountRank,float probabilityDistribution) {
		plugin = lw;
		this.loc = loc;
		this.rarity = rarity;
		this.itemAmount = itemAmount;
		this.itemAmountRank = itemAmountRank;
		this.probabilityDistribution = probabilityDistribution;
		this.metaData = new FixedMetadataValue(plugin,rarity);
		this.id = id;
		
		this.loc.getBlock().setType(Material.CHEST);
		
		this.chest = (Chest) loc.getBlock().getState();
		
		chest.setMetadata(rarity, metaData);
		
		this.content = populateChestItem();
	}
	public ChestProperty(LootWorld lw,Location loc,String rarity,int itemAmount,int itemAmountRank) {
		plugin = lw;
		this.loc = loc;
		this.rarity = rarity;
		this.itemAmount = itemAmount;
		this.itemAmountRank = itemAmountRank;
		this.metaData = new FixedMetadataValue(plugin,rarity);
		
		this.chest = (Chest) loc.getBlock().getState();
		
		chest.setMetadata(rarity, metaData);
		
		this.content = populateChestItem();
	}
	
	public ChestProperty(LootWorld lw,int id,Location loc,String rarity,float probabilityDistribution) {
		this.plugin = lw;
		this.loc = loc;
		this.rarity = rarity;
		this.id = id;
		this.probabilityDistribution = probabilityDistribution;
	}
	
	
	public Chest getChestBlock() {
		return chest;
	}
	
	public int getId() {
		return this.id;
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
	
	public Location getLocation() {
		return this.loc;
	}
	
	public String toIdString() {
		return rarity + "-" + id;
	}
	
	@Override
	public String toString() {
		String s = loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getWorld().getName();
		return s;
	}
	
	public void reloadChest() {
		this.metaData = new FixedMetadataValue(plugin,rarity);
		this.chest = (Chest) loc.getBlock().getState();
		chest.setMetadata(rarity, metaData);
		this.content = Arrays.asList(chest.getInventory().getContents());
	}
	
	private ArrayList<ItemStack> populateChestItem() {
		
		ArrayList<ItemStack> itemList = new ArrayList<ItemStack>();
		ArrayList<ItemConfig> rankItem = plugin.getParsedItems().get(rarity);		
		
		float probabilityRank = 1f;
		try {
			if(rankItem.size() != 0) {
				probabilityRank = probabilityRank / (float)rankItem.size();
			}
		}catch(NullPointerException e) {
			Bukkit.getConsoleSender().sendMessage(PREFIX + "Unknown rank: " + ChatColor.RED + rarity);
			Bukkit.getConsoleSender().sendMessage(PREFIX + "Does item with rank " + ChatColor.RED + rarity + ChatColor.BLUE + " exists in items.json ?");
		}
		
		
		for(int i = 0; i < itemAmount; i ++) {
			double t = 0;
			for(Entry<ItemStack, Float> item : plugin.getRankAllItemsDistribution().entrySet()) {
				t += item.getValue();
				double outcome = Math.random();
				if(t >= outcome) {
					itemList.add(item.getKey());
				}
			}
		}
		
		HashMap<ItemStack,Float> mapRankItem = new HashMap<ItemStack,Float>();
		for(ItemConfig item : rankItem) {
			mapRankItem.put(item.getItem(),probabilityRank);
		}
		
		for(int i = 0; i < itemAmountRank ; i ++) {
			double t = 0;
			
			for(Entry<ItemStack,Float> item : mapRankItem.entrySet()) {
				t += item.getValue();
				double outcome = Math.random();
				if(t >= outcome) {			
					itemList.add(item.getKey());
				}
			}
		}
		
		for(ItemStack item : itemList) {
			chest.getInventory().addItem(item);
		}
		
		return itemList;
		
	}

	@Override
	public int compareTo(ChestProperty o) {
		return (int) (this.probabilityDistribution - o.getChestDistributionRate());
	}
	

}
