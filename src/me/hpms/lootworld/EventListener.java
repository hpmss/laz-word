package me.hpms.lootworld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

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
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if(e.getClickedBlock() == null) {
			return;
		}
		Player p = e.getPlayer();
		if(e.getClickedBlock().getType() == Material.GRASS) {
			Collections.shuffle(plugin.getRankAllItems());
			e.getPlayer().getInventory().addItem(plugin.getRankAllItems().get(0));
		}
		if(e.getClickedBlock().getType() == Material.CHEST) {
			Chest chest = (Chest) e.getClickedBlock().getState();
			if(chest.hasMetadata("Common")) {
				e.getPlayer().sendMessage(PREFIX + "Chest opened..");
			}
		}
	}
	
	

}
