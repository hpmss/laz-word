package me.hpms.lootworld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import net.md_5.bungee.api.ChatColor;

public class EventListener implements Listener {
	
	/* TODO 
	 * Add effects and check for distance
	 * 
	 */
	
	private final String PREFIX = ChatColor.GREEN + "『 LootWorld 』" + ChatColor.BLUE + "-> ";

	private LootWorld plugin;
	
	private List<String> rank;
	
	
	public EventListener(LootWorld lw) {
		plugin = lw;
		rank = new ArrayList<>(plugin.getChestRarity().getRanking().keySet());
		
	}
	
	public static boolean isEmpty(Inventory inv) {
	    for (ItemStack item : inv.getContents()) {
	      if (item != null) {
	        return false;
	      }
	    }
	    return true;
	  }
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if(e.getClickedBlock() == null) {
			return;
		}
		Player p = e.getPlayer();
		if(e.getClickedBlock().getType() == Material.GRASS) {
			Collections.shuffle(plugin.getRankAllItems());
			p.getInventory().addItem(plugin.getRankAllItems().get(0));
		}
		if(e.getClickedBlock().getType() == Material.CHEST) {
			Chest chest = (Chest) e.getClickedBlock().getState();
			if(chest.hasMetadata("Common")) {
				p.sendMessage(PREFIX + "Chest opened..");
				plugin.getNMSEntity().spawnEntity(e.getClickedBlock().getWorld(), e.getClickedBlock().getLocation());
			}
		}
	}

	
	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent e) {
		
		Inventory inv = e.getInventory();
		
		if(inv.getHolder() instanceof Chest) {
			for(String s : rank) {
				if(inv.getLocation().getBlock().hasMetadata(s) && isEmpty(inv)) {
					inv.getLocation().getBlock().setType(Material.AIR);
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		if(e.getItemInHand() instanceof Chest) return;
		if(e.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("lwtest")) {
			e.getBlockPlaced().setMetadata("Common", new FixedMetadataValue(plugin,"Common"));
		}
	}
	
	

}
