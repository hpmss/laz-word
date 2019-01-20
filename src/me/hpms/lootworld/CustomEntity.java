package me.hpms.lootworld;

import java.lang.reflect.Constructor;

import org.bukkit.entity.EntityType;

import me.hpms.lootworld.util.NMSEntity;

public class CustomEntity {
	
	private LootWorld plugin;
	
	private NMSEntity nms;
	
	private EntityType type;
	
	private final float health;
	
	private final double followRange;
	
	private final double knockbackResistance;
	
	private final double movementSpeed;
	
	private final double flyingSpeed;
	
	private final double attackDamage;
	
	private final double attackSpeed;
	
	private final double armor;
	
	private final double armorDurability;
	
	private final float armorPiercingLevel;
	
	private final float luck;
	
	
	public CustomEntity(LootWorld lw,EntityType type,float health,double followRange,double knockbackResistance,
			double movementSpeed,double flyingSpeed
			,double attackDamage,double attackSpeed,double armor,
			double armorDurability,float armorPiercingLevel,float luck) {
		this.plugin = lw;
		this.nms = plugin.getNMSEntity();
		this.type = type;
		this.health = health;
		this.followRange = followRange;
		this.knockbackResistance = knockbackResistance;
		this.movementSpeed = movementSpeed;
		this.flyingSpeed = flyingSpeed;
		this.attackDamage = attackDamage;
		this.attackSpeed = attackSpeed;
		this.armor = armor;
		this.armorDurability = armorDurability;
		this.armorPiercingLevel = armorPiercingLevel;
		this.luck = luck;
		
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public <T> Class<T> createEntity() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class<T> clazz = null;
		Constructor<T> constructor = null;
		Object b = null;
		Object c = null;
		
		String name = type.getName();
		clazz = (Class<T>) nms.getEntityClassByName(name);
		
		return clazz;
	}
}
