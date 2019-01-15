package me.hpms.lootworld;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.DataWatcher;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.World;

public class Toy extends EntityHuman{
	
	public Toy(World world, GameProfile gameprofile) {
		super(world, gameprofile);
	}
	
	void playFakeBed(Player p,BlockPosition pos ) {
		PacketPlayOutNamedEntitySpawn packetEntitySpawn = new PacketPlayOutNamedEntitySpawn();
		
		CraftPlayer pc = (CraftPlayer) p;
		
		double y = this.locY;
		
		DataWatcher data = this.getDataWatcher();
		
		
	}

	@Override
	public boolean isSpectator() {
		return false;
	}

	@Override
	public boolean z() {
		return false;
	}
}
