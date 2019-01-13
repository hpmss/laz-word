package me.hpms.lootworld;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityIronGolem;
import net.minecraft.server.v1_12_R1.EntitySpider;
import net.minecraft.server.v1_12_R1.EntityZombie;
import net.minecraft.server.v1_12_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_12_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_12_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_12_R1.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_12_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_12_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_12_R1.World;

public class CustomEntityMob extends EntityZombie{
	
	
	public enum EntityTypes
	{
	    //NAME("Entity name", Entity ID, yourcustomclass.class);
	    CUSTOM_ENTITY("Zombie", 54, CustomEntityMob.class); //You can add as many as you want.

	    private EntityTypes(String name, int id, Class<? extends Entity> custom)
	    {
	        addToMaps(custom, name, id);
	    }

	  public static void spawnEntity(Entity entity, Location loc)
	   {
	     entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	     ((World) loc.getWorld()).addEntity(entity);
	   }
		@SuppressWarnings("rawtypes")
		public static Object getField(String f,Class cl , Object object) {
			
			Field field;
			Object obj = null;
			try {
				field = cl.getDeclaredField(f);
				field.setAccessible(true);
				obj = field.get(object);
			}catch(NoSuchFieldException e) {
				e.printStackTrace();
			}catch(IllegalAccessException e) {
				e.printStackTrace();
			}
			return obj;
		}

	    @SuppressWarnings({ "unchecked", "rawtypes" })
		private static void addToMaps(Class clazz, String name, int id)
	    {
	        //getPrivateField is the method from above.
	        //Remove the lines with // in front of them if you want to override default entities (You'd have to remove the default entity from the map first though).
	        ((Map)getField("c", net.minecraft.server.v1_12_R1.EntityTypes.class, null)).put(name, clazz);
	        ((Map)getField("d", net.minecraft.server.v1_12_R1.EntityTypes.class, null)).put(clazz, name);
	        //((Map)getPrivateField("e", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(Integer.valueOf(id), clazz);
	        ((Map)getField("f", net.minecraft.server.v1_12_R1.EntityTypes.class, null)).put(clazz, Integer.valueOf(id));
	        //((Map)getPrivateField("g", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(name, Integer.valueOf(id));
	    }
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CustomEntityMob(World world) {
		super(world);
		List goalB = (List)getField("b",PathfinderGoalSelector.class,goalSelector);
		goalB.clear();
		List goalC = (List)getField("c",PathfinderGoalSelector.class,goalSelector);
		goalC.clear();
		List targetB = (List)getField("b",PathfinderGoalSelector.class,targetSelector);
		targetB.clear();
		List targetC = (List)getField("c",PathfinderGoalSelector.class,targetSelector);
		targetC.clear();
		
		this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, false));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntitySpider.class, true));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityIronGolem.class, false));
		
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getField(String f,Class cl , Object object) {
		
		Field field;
		Object obj = null;
		try {
			field = cl.getDeclaredField(f);
			field.setAccessible(true);
			obj = field.get(object);
		}catch(NoSuchFieldException e) {
			e.printStackTrace();
		}catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		return obj;
	}

}
