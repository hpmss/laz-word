package me.hpms.lootworld;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayOutBed;

public class OnCommand implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
		
		if (!(sender instanceof Player)) {
			return false;
		}
		Player p = (Player) sender;
		
		ItemStack item = new ItemStack(Material.CHEST);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("lwtest");
		item.setItemMeta(meta);
		p.getInventory().addItem(item);
		
		Packet packet = new PacketPlayOutBed();
		CraftPlayer pc = (CraftPlayer) p;
		pc.getHandle().playerConnection.sendPacket(packet);
		
		return false;
	}

}
