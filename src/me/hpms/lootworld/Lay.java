package me.hpms.lootworld;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.ChatMessage;
import net.minecraft.server.v1_12_R1.DataWatcher;
import net.minecraft.server.v1_12_R1.DataWatcherObject;
import net.minecraft.server.v1_12_R1.DataWatcherRegistry;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EnumGamemode;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.MathHelper;
import net.minecraft.server.v1_12_R1.PacketPlayOutBed;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;

public class Lay {
	
	public Lay(Player p) {
		try {
			sendPacketLayDown(p);
			p.sendMessage(ChatColor.RED + "Packet sent...");
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public int getNextEntityId() {
		try {
			Field entityCount = Entity.class.getDeclaredField("entityCount");
			entityCount.setAccessible(true);
			int id = entityCount.getInt(null);
			entityCount.setInt(null, id + 1);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return (int) Math.round(Math.random() * Integer.MAX_VALUE * 0.25);
		}
	}
	
	public DataWatcher clonePlayerDataWatcher(Player p,int currentEndId) {
		CraftWorld w = (CraftWorld) p.getWorld();
		CraftPlayer pc = (CraftPlayer) p;
		
		EntityHuman e = new EntityHuman(w.getHandle(),pc.getProfile()) {
			
			public void sendMessage(IChatBaseComponent arg0) {
				return;
			}
			
			public boolean a(int arg0,String arg1) {
				return false;
			}

			public boolean isSpectator() {
				return false;
			}

			@Override
			public boolean z() {
				return false;
			}
			
		};
		e.f(currentEndId);
		return e.getDataWatcher();
		
	}
	
	@SuppressWarnings("deprecation")
	public void sendPacketLayDown(Player p) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		BlockPosition pos = new BlockPosition(p.getLocation().getBlockX(), 0, p.getLocation().getBlockZ());
		
		CraftPlayer pc = (CraftPlayer) p;
		int entityId = getNextEntityId();
		GameProfile gp = ((CraftPlayer) p).getProfile();
		DataWatcher dw = clonePlayerDataWatcher(p,entityId);
		DataWatcherObject<Integer> obj = new DataWatcherObject<Integer>(10,DataWatcherRegistry.b);
		dw.set(obj, (int) 0);
		
		
		PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
		PacketPlayOutPlayerInfo.PlayerInfoData data = info.new PlayerInfoData(gp, 0,EnumGamemode.SURVIVAL, new ChatMessage("", new Object[0]));
		List<PacketPlayOutPlayerInfo.PlayerInfoData> dataList = Lists.newArrayList();
		dataList.add(data);
		changeFieldValue("b",info,dataList);
		
		
		PacketPlayOutNamedEntitySpawn entity = new PacketPlayOutNamedEntitySpawn();
		changeFieldValue("a",entity,entityId);
		changeFieldValue("b",entity,gp.getId());
		changeFieldValue("c",entity,MathHelper.floor(((EntityHuman) pc.getHandle()).locX * 32D));
		changeFieldValue("d",entity,MathHelper.floor(((EntityHuman) pc.getHandle()).locY * 32D));
		changeFieldValue("e",entity,MathHelper.floor(((EntityHuman) pc.getHandle()).locZ * 32D));
		changeFieldValue("f",entity,(byte) ((int) (((EntityHuman) pc.getHandle()).yaw * 256.0F / 360.0F)));
		changeFieldValue("g",entity,(byte) ((int) (((EntityHuman) pc.getHandle()).pitch * 256.0F / 360.0F)));
		changeFieldValue("h",entity,dw);
		 
		PacketPlayOutBed bed = new PacketPlayOutBed();
		changeFieldValue("a",bed,entityId);
		changeFieldValue("b",bed, pos);
		
		PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport();
		changeFieldValue("a",teleport,entityId);
		changeFieldValue("b",teleport,MathHelper.floor(((EntityHuman) pc.getHandle()).locX * 32D));
		changeFieldValue("c",teleport,MathHelper.floor(((EntityHuman) pc.getHandle()).locY * 32D));
		changeFieldValue("d",teleport,MathHelper.floor(((EntityHuman) pc.getHandle()).locZ * 32D));
		changeFieldValue("e",teleport,(byte) ((int) (((EntityHuman) pc.getHandle()).yaw * 256.0F / 360.0F)));
		changeFieldValue("f",teleport,(byte) ((int) (((EntityHuman) pc.getHandle()).pitch * 256.0F / 360.0F)));
		changeFieldValue("g",teleport, true);
		
		PacketPlayOutEntityTeleport teleportDown = new PacketPlayOutEntityTeleport();
		changeFieldValue("a",teleportDown,entityId);
		changeFieldValue("b",teleportDown,MathHelper.floor(((EntityHuman) pc.getHandle()).locX * 32D));
		changeFieldValue("c",teleportDown,MathHelper.floor(((EntityHuman) pc.getHandle()).locY * 32D));
		changeFieldValue("d",teleportDown,MathHelper.floor(((EntityHuman) pc.getHandle()).locZ * 32D));
		changeFieldValue("e",teleportDown,(byte) ((int) (((EntityHuman) pc.getHandle()).yaw * 256.0F / 360.0F)));
		changeFieldValue("f",teleportDown,(byte) ((int) (((EntityHuman) pc.getHandle()).pitch * 256.0F / 360.0F)));
		changeFieldValue("g",teleportDown, true);
		
		for (Player player : Bukkit.getOnlinePlayers()) {
		      Location loc = p.getLocation().clone();
		      player.sendBlockChange(loc.subtract(0, loc.getY(), 0), Material.BED_BLOCK, (byte) 0);

		      CraftPlayer pl = ((CraftPlayer) player);
		      pl.getHandle().playerConnection.sendPacket(info);
		      pl.getHandle().playerConnection.sendPacket(entity);
		      pl.getHandle().playerConnection.sendPacket(teleportDown);
		      pl.getHandle().playerConnection.sendPacket(bed);
		      pl.getHandle().playerConnection.sendPacket(teleport);
		}
		 
		 
	}
	
	public void changeFieldValue(String fieldName,Object clazz,Object value) {
		
		Field f;
		try {
			f = clazz.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(clazz, value);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}

}
