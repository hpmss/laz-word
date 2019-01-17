package me.hpms.lootworld;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
import org.bukkit.scheduler.BukkitScheduler;

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
		if(e.getClickedBlock() == null || e.getClickedBlock().getType() == Material.AIR) {
			return;
		}
		Player p = e.getPlayer();
		if(e.getClickedBlock().getType() == Material.CHEST) {
			Chest chest = (Chest) e.getClickedBlock().getState();
			for(String s : rank) {
				if(chest.hasMetadata(s)) {
					p.sendMessage(PREFIX + "Chest loot successfully");
					List<Location> loc = plugin.getGenerator().getChestLocationList().get(p.getWorld().getName());
					if(loc.contains(new Location(e.getClickedBlock().getLocation().getWorld(),
							(double)(int)e.getClickedBlock().getLocation().getX(),
							(double)(int)e.getClickedBlock().getLocation().getY(),
							(double)(int)e.getClickedBlock().getLocation().getZ()))) {
						p.sendMessage(PREFIX + "Chest exists in location.yml");
					}
//					plugin.getNMSEntity().spawnEntity(e.getClickedBlock().getWorld(), e.getClickedBlock().getLocation());
				}
			}
			
		}
	}

	
	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent e) {
		
		Inventory inv = e.getInventory();
		
		if(inv.getHolder() instanceof Chest) {
			for(String s : rank) {
				if(inv.getLocation().getBlock().hasMetadata(s) && isEmpty(inv)) {
					Location loc = inv.getLocation();
					BukkitScheduler scheduler = plugin.getServer().getScheduler();
			        scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
						float r = 3;
						double theta = 0F;
						double vel = 1.2;
			            @Override
			            public void run() {
			            	theta += Math.PI / 16;
			            	for(double phi = 0;phi <= Math.PI ; phi += Math.PI / 16) {
			            		for(int i = 0; i <= 3 ; i ++) {
			            			double x = r * Math.cos(theta + phi) * Math.sin(phi);
					            	double y = r * Math.sin(theta + phi) * Math.sin(phi);
					            	double z = r * Math.cos(phi);
					            	if( i == 1) {
					            		x = r * Math.cos(theta +phi + Math.PI / 2) * Math.sin(phi);
						            	y = r * Math.sin(theta + phi + Math.PI / 2) * Math.sin(phi);
						            	z = r * Math.cos(phi);
					            	}
					            	if( i == 2) {
					            		x = r * Math.cos(theta +phi + Math.PI ) * Math.sin(phi);
						            	y = r * Math.sin(theta + phi +Math.PI) * Math.sin(phi);
						            	z = r * Math.cos(phi);
					            	}
					            	if( i == 3) {
					            		x = r * Math.cos(theta +phi + Math.PI + Math.PI / 2 ) * Math.sin(phi);
						            	y = r * Math.sin(theta + phi +Math.PI + Math.PI / 2) * Math.sin(phi);
						            	z = r * Math.cos(phi);
					            	}
					             	Location sphere = new Location(loc.getWorld(),loc.getX() + x + 0.5,loc.getY() + z + 0.5 ,loc.getZ() + 0.5 +y);
					            	inv.getLocation().getWorld().spawnParticle(Particle.CLOUD,
					            			sphere.getX(),sphere.getY(),sphere.getZ(),1,0D,0D,0D,0D);
			            		}
			            	
			            	}
			            	
			            	if(theta > 2* Math.PI) {
			            		theta = 0;
			            	}
			           
			            }
			        }, 0L, 3);
					
					
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
