package me.hpms.lootworld;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
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

import com.google.common.collect.HashBiMap;

import net.md_5.bungee.api.ChatColor;

public class EventListener implements Listener {
	
	/* TODO 
	 * Cancel task with separate id
	 * Stop player from spamming effect and sound
	 */
	
	private final String PREFIX = ChatColor.GREEN + "『 LootWorld 』" + ChatColor.BLUE + "-> ";

	private LootWorld plugin;
	
	private List<String> rank;
	
	public boolean activated = false;
	
	
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
			ConfigurationSection section = plugin.getGenerator().getFileConfiguration().getConfigurationSection("location-" + p.getWorld().getName());
			HashBiMap<String,Object> set = HashBiMap.create(section.getValues(false));
			Chest chest = (Chest) e.getClickedBlock().getState();
			for(String s : rank) {
				if(chest.hasMetadata(s)) {
					Object l = chest.getLocation().getX() + "," + chest.getLocation().getY() + "," + chest.getLocation().getZ()+ "," +chest.getLocation().getWorld().getName();
					if(set.inverse().containsKey(l)) {
						set.inverse().remove(l);
						p.sendMessage(PREFIX + "Chest exists in location.yml");
						p.sendMessage(chest.getMetadata(s).toArray().toString());
						plugin.getGenerator().getFileConfiguration().createSection("location-" + p.getWorld().getName(), set);
						plugin.getGenerator().saveConfiguration();
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
				if(inv.getLocation().getBlock().hasMetadata(s)) {
					Location loc = inv.getLocation();
					if(activated == false) {
						spaceBuffer(loc);
					}
					
					activated = true;
					BukkitScheduler scheduler = plugin.getServer().getScheduler();
			        scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
						float r = 1.5f;
						double phi = 0F;
			            @Override
			            public void run() {
			            	phi += Math.PI / 16;
			            	for(double theta = 0;theta <= 2 *Math.PI ; theta += Math.PI / 16) {
			            		double x = r * Math.cos(theta) * Math.sin(phi);
				            	double y = r * Math.sin(theta) * Math.sin(phi);
				            	double z = r * Math.cos(phi);
				             	Location sphere = new Location(loc.getWorld(),loc.getX() + x + 0.5,loc.getY() + z + 0.5 ,loc.getZ() + 0.5 +y);
				            	loc.getWorld().spawnParticle(Particle.CLOUD,
				            			sphere.getX(),sphere.getY(),sphere.getZ(),2,0D,0D,0D,0D);
			            	}
			            	if(phi > 2*Math.PI) {
			            		loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 10, 10);
						        loc.getWorld().spawnParticle(Particle.HEART, loc, 10);
						        loc.getBlock().setType(Material.AIR);
						        if(!isEmpty(inv)) {
						        	for(ItemStack item : inv.getContents()) {
						        		loc.getWorld().dropItem(loc, item);
						        	}
						        }
						        activated = false;
			            		scheduler.cancelAllTasks();
			            	}
			           
			            }
			        }, 0L, 3);
					
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		if(e.getItemInHand().getType() != Material.CHEST) return;
		if(e.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("lwtest")) {
			e.getBlockPlaced().setMetadata("Common", new FixedMetadataValue(plugin,"Common"));
		}
	}
	
	public void spaceBuffer(Location loc) {
		Location pivot = loc.clone();
		for(double x = pivot.getX() - 2; x <= pivot.getX() + 2; x++ )
		{
		  for(double y = pivot.getY() - 1; y < pivot.getY() + 1; y++ )
		  {
		      for(double z = pivot.getZ() - 2; z <= pivot.getZ() + 2; z++ )
		      {
		    	  if(!(x == pivot.getX() && y == pivot.getY() && z == pivot.getZ()) &&
		    			  !(x == pivot.getX() && y == pivot.getY() - 1 && z == pivot.getZ()) ) {
		    		  new Location(pivot.getWorld(),x,y,z).getBlock().setType(Material.AIR);
		    	  }
		      }
		  }
		}
		
		pivot.getWorld().playSound(pivot, Sound.ENTITY_GENERIC_EXPLODE, 5, 5);
	}
	

}
