package me.hpms.lootworld;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

@SuppressWarnings("rawtypes")
public final class NMSEntity {
	
	/**
	 * @author hpms
	 * -> This class is written using only reflection for multiple versions compatibility
	 * -> Feel free to use how ever you like
	 */
	
	private final String NMSString;
	
	private final String OGCString;
	
	private static Class minecraftKey;
	
	private static Class entityTypes;
	
	private static Class genericAttributes;
	
	private static Class enumItemSlot;
	
	private static Class craftWorld;
	
	public NMSEntity() {
		
		String packageNameNMS = "net.minecraft.server.v";
		String packageNameOGC = "org.bukkit.craftbukkit.v";
		String[] bukkitVersion = Bukkit.getBukkitVersion().split("-");
		String version = bukkitVersion[0].substring(0,4);
		packageNameNMS += version.replace(".", "_") + "_R" + bukkitVersion[1].substring(bukkitVersion[1].length() - 1);
		packageNameOGC += version.replace(".", "_") + "_R" + bukkitVersion[1].substring(bukkitVersion[1].length() - 1);
		
		this.NMSString = packageNameNMS;
		this.OGCString = packageNameOGC;
		
		try {
			craftWorld = Class.forName(OGCString + ".CraftWorld");
			minecraftKey = Class.forName(NMSString  + ".MinecraftKey");
			entityTypes = Class.forName(NMSString + ".EntityTypes");
			genericAttributes = Class.forName(NMSString + ".GenericAttributes");
			enumItemSlot = Class.forName(NMSString + ".EnumItemSlot");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 *  -----
	 * 	Register custom entity to the server.
	 *  -----
	 *  @param key -> create new instance of MinecraftKey for registering the entity
	 * 	@param a -> access RegistryMaterials<MinecraftKey, Class<? extends Entity>> field in @EntityTypes
	 *  @a.getClass() -> access a() method to invoke on RegistryMaterials @b in @EntityTypes
	 * 	@param register -> create new registry for entity with custom class
	 * 	@param minecraftKeySet -> add key to set if not existed
	 */
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void registerEntity(final EntityType type,final String name,final Class<?> clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException, NoSuchFieldException, ClassNotFoundException {
		Object key = null;
		Object a = null;
		Method register = null;
		int id = type.getTypeId();
		Constructor keyConstructor = minecraftKey.getConstructor(String.class);
		key = keyConstructor.newInstance(name);
		a = getFieldObject("b",entityTypes,a);
		Object b_registryMaterials = entityTypes.getField("b").get(entityTypes);
		register = a.getClass().getMethod("a",int.class,Object.class ,Object.class);
		register.invoke(b_registryMaterials ,id, key,clazz);
		
		Set minecraftKeySet = (Set) entityTypes.getField("d").get(entityTypes);
		if(!minecraftKeySet.contains(key)) {
			minecraftKeySet.add(key);
		}
		
	}
	/**
	 * 
	 * @param w -> casted to CraftWorld call getHandle() which returns WorldServer which extends World
	 * @param loc -> used to spawn where specified
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @castCraftWorld -> return WorldServer for using in a new instance of Custom Entity <T> should be NMS_World
	 */
	
	@SuppressWarnings("unchecked")
	private <T> T castCraftWorld(World w) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object castedCraftWorld = craftWorld.cast(w);
		Method getHandle = castedCraftWorld.getClass().getDeclaredMethod("getHandle");
		Object worldServer = getHandle.invoke(castedCraftWorld);
		return (T) worldServer;
	}
	public void spawnEntity(World w,Location loc) {
		CustomEntityMob entity;
		Object castedCraftWorld = craftWorld.cast(w);
		try {	
			
			entity = new CustomEntityMob(castCraftWorld(w));
			entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			Method addEntity = castedCraftWorld.getClass().getMethod("addEntity", Class.forName(NMSString + ".Entity"),SpawnReason.class);
			addEntity.invoke(castedCraftWorld, entity,CreatureSpawnEvent.SpawnReason.CUSTOM);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * -----
	 * Enum for accessing attributes of an entity for manipulating.
	 * -----
	 * @getValue() -> @return Object casted to IAttribute for manipulating Entity attributes
	 */
	
	public enum NMSAttributes {
		MAX_HEALTH("maxHealth","Max Health"),
		FOLLOW_RANGE("FOLLOW_RANGE","Follow Range"),
		KNOCKBACK_RESISTANCE("c","Knockback Resistance"),
		MOVEMENT_SPEED("MOVEMENT_SPEED","Movement Speed"),
		FLYING_SPEED("e","Flying Speed"),
		ATTACK_DAMAGE("ATTACK_DAMAGE","Attack Damage"),
		ATTACK_SPEED("g","Attack Speed"),
		ARMOR("h","Armor"),
		ARMOR_DURABILITY("i","Armor Durability"),
		LUCK("j","Luck");
		
		private String att_map;
		
		private String name;
		
		public String getAttributeName() {
			return this.name;
		}
		
		NMSAttributes(String att_map,String name) {
			this.att_map = att_map;
			this.name = name;
		}
		
		@SuppressWarnings("unchecked")
		public <T> T getValue() {
			Object attr = null;
			attr = getFieldObject(att_map,genericAttributes,genericAttributes);
			return (T) attr;
		}
	}
	
	/**
	 * -----
	 * Enum for accessing Entity wearing slots.
	 * -----
	 * @getValue() -> @return Entity's slot for manipulating wears <T> should be EnumItemSlot.
	 */
	public enum NMSEntitySlot {
		MAIN_HAND("MAINHAND"),
		OFF_HAND("OFFHAND"),
		FEET("FEET"),
		LEGS("LEGS"),
		CHEST("CHEST"),
		HEAD("HEAD");
		
		private String enumMap;
		
		NMSEntitySlot(String enumMap) {
			this.enumMap = enumMap;
		}
		
		@SuppressWarnings("unchecked")
		public <T> T getValue() {
			Object slot = null;
			Object[] cons = enumItemSlot.getEnumConstants();
			for(Object constant : cons) {
				if(constant.toString().equalsIgnoreCase(enumMap)) {
					slot = constant;
				}
			}
			return (T) slot;
		}
		
	}
	
	/**
	 * -----
	 * Utility functions
	 * -----
	 */
	
	public String getNMSPackage() {
		return this.NMSString;
	}
	
	public static Object getFieldObject(String fieldName,Class<?> clazz,Object object) {
		
		Field f;
		Object j = null;
		try {
			
			f = clazz.getDeclaredField(fieldName);
			f.setAccessible(true);
			j = f.get(object); 
			
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return j;
	}

}
