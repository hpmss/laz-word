package me.hpms.lootworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.hpms.lootworld.util.NMSEntity;
import me.hpms.lootworld.util.TaskHandler;
import net.md_5.bungee.api.ChatColor;

public class EventListener implements Listener {

	private static JavaPlugin plugin;
	
	public static List<Location> activated;
	
	private static Random rand;
	
	static {
		plugin = LootWorld.plugin;
		activated = new ArrayList<Location>();
		rand = new Random();
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
			if(chest.hasMetadata("LootWorld")) {
				String id = String.valueOf(chest.getMetadata("LootWorld").get(0).value());
				LocationGenerator.updateConfigByWorld(p.getWorld().getName(), id, LootWorld.PREFIX +
						ChatColor.RED + p.getName() + ChatColor.GREEN +" đã tìm thấy rương " + ChatColor.GOLD + id.split("-")[0] + " !");
			}
			
		}
	}
	
	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent e) {
		
		Inventory inv = e.getInventory();
		
		if(inv.getHolder() instanceof Chest) {
				if(inv.getLocation().getBlock().hasMetadata("LootWorld")) {
					Location loc = inv.getLocation();
					String s = String.valueOf(loc.getBlock().getMetadata("LootWorld").get(0).value());
					if(!activated.contains(loc)) {
						activated.add(loc);
						spaceBuffer(loc);
						ArrayList<String> set = new ArrayList<>(ChestRarity.getRanking().keySet());
						if(set.size() >= 2) {
							if(set.subList(set.size() - 2, set.size()).contains(s.split("-")[0])) {
								int a = rand.nextInt(4) + 1;
								for(int i = 0; i < a;i++) {
									NMSEntity.spawnEntity(loc.getWorld(), loc.clone().add(rand.nextInt(3) , 0, rand.nextInt(3)));
								}
							}
						}	
						TaskHandler handler = new TaskHandler(plugin,0,3) {
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
							        loc.getBlock().removeMetadata("LootWorld", plugin);
							        if(!isEmpty(inv)) {
							        	for(ItemStack item : inv.getContents()) {
							        		loc.getWorld().dropItem(loc, item);
							        	}
							        }
							        activated.remove(loc);
				            		cancelTask();
				            	}
				           
				            }
				        };
					}
					
					
				}
			}
		
	}
	
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		if(e.getItemInHand().getType() != Material.CHEST) return;
		if(!e.getItemInHand().getItemMeta().hasDisplayName()) return;
		for(Entry<String, Float> entry : ChestRarity.getRanking().entrySet()) {
			Log.info(entry.getKey());
			if(e.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + entry.getKey())) {
				int itemAmountRank = (int) (Math.random() * ((LocationGenerator.maxItemRank - LocationGenerator.minItemRank) + 1)) + LocationGenerator.minItemRank;
				int itemAmountAll = (int) (Math.random() * ((LocationGenerator.maxItemAll - LocationGenerator.minItemAll ) + 1)) + LocationGenerator.minItemAll;
				new ChestProperty(rand.nextInt(50000),e.getBlockPlaced().getLocation(),entry.getKey(),itemAmountAll,itemAmountRank,entry.getValue());
			}
		}
	}
	
	private static void spaceBuffer(Location loc) {
		Location pivot = loc.clone();
		for(double x = pivot.getX() - 2; x <= pivot.getX() + 2; x++ )
		{
		  for(double y = pivot.getY() - 1; y <= pivot.getY() + 1; y++ )
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
