package me.hpms.lootworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class ChestProperty implements Comparable<ChestProperty>{
	
	private Location loc;
	
	private Chest chest;
	
	private String rarity;
	
	private int itemAmount;
	
	private int itemAmountRank;
	
	private float probabilityDistribution;
	
	private FixedMetadataValue metaData;
	
	private int id;

	
	public ChestProperty(int id,Location loc,String rarity,int itemAmount,int itemAmountRank,float probabilityDistribution) {
		this.loc = loc;
		this.rarity = rarity;
		this.itemAmount = itemAmount;
		this.itemAmountRank = itemAmountRank;
		this.probabilityDistribution = probabilityDistribution;
		this.metaData = new FixedMetadataValue(LootWorld.plugin,rarity + "-" + String.valueOf(id));
		this.id = id;
		
		this.loc.getBlock().setType(Material.CHEST);
		
		this.chest = (Chest) loc.getBlock().getState();
		
		chest.setMetadata("LootWorld", metaData);
		
		populateChestItem();
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getRarity() {
		return rarity;
	}
	
	public float getChestDistributionRate() {
		return probabilityDistribution;
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
	
	public static void reloadChest(int id,Location loc,String rarity) {
		loc.getBlock().setMetadata("LootWorld", new FixedMetadataValue(LootWorld.plugin,rarity + "-" + id));
	}
	
	private ArrayList<ItemStack> populateChestItem() {
		
		ArrayList<ItemStack> itemList = new ArrayList<ItemStack>();
		ArrayList<ItemConfig> rankItem = LootWorld.getParsedItems().get(rarity);		
		
		float probabilityRank = 1f;
		try {
			if(rankItem.size() != 0) {
				probabilityRank = probabilityRank / (float)rankItem.size();
			}
		}catch(NullPointerException e) {
			Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "Unknown rank: " + ChatColor.RED + rarity);
			Bukkit.getConsoleSender().sendMessage(LootWorld.PREFIX + "Does item with rank " + ChatColor.RED + rarity + ChatColor.BLUE + " exists in items.json ?");
		}
		
		
		for(int i = 0; i < itemAmount; i ++) {
			double t = 0;
			for(Entry<ItemStack, Float> item : LootWorld.getRankAllItemsDistribution().entrySet()) {
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
		return (int) (this.getId() - o.getId());
	}
	

}
