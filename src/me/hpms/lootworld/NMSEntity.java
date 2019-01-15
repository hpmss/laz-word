package me.hpms.lootworld;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.jline.internal.Log;

@SuppressWarnings("rawtypes")
public final class NMSEntity {
	
	private final String NMSString;
	
	private Class minecraftKey;
	
	private Class entityTypes;
	
	public NMSEntity() {
		
		String packageName = "net.minecraft.server.v";
		String[] bukkitVersion = Bukkit.getBukkitVersion().split("-");
		String version = bukkitVersion[0].substring(0,4);
		packageName += version.replace(".", "_") + "_R" + bukkitVersion[1].substring(bukkitVersion[1].length() - 1);
		
		this.NMSString = packageName;
		
		try {
			minecraftKey = Class.forName(NMSString  + ".MinecraftKey");
			entityTypes = Class.forName(NMSString + ".EntityTypes");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			registerEntity(54,"zombie",CustomEntityMob.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void registerEntity(final int id,final String name,final Class<?> clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException, NoSuchFieldException, ClassNotFoundException {
		
		Object key = null;
		Object a = null;
		Method register = null;
		Constructor keyConstructor = minecraftKey.getConstructor(String.class);
		//MinecraftKey key = new MinecraftKey(key_name)
		key = keyConstructor.newInstance(name);
		
		//Get method 'a' to register entity
		a = getField("b",entityTypes,a);
		Object b_registryMaterials = entityTypes.getField("b").get(entityTypes);
		//Register to current set of keys
		register = a.getClass().getMethod("a",int.class,Object.class ,Object.class);
		register.invoke(b_registryMaterials ,id, name,clazz);
		
		//Set of minecraft-key ( entities key -> example: minecraft:squid )
		//Adding key if not exist
		Set minecraftKeySet = (Set) entityTypes.getField("d").get(entityTypes);
		if(!minecraftKeySet.contains(key)) {
			minecraftKeySet.add(key);
		}
		Log.info(minecraftKeySet);
		
	}
	
	
	public String getNMSPackage() {
		return this.NMSString;
	}
	
	public Object getField(String fieldName,Class<?> clazz,Object object) {
		
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
