package me.hpms.lootworld;

public class CustomEntityInterface {
	
	private LootWorld plugin;
	
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
	
	
	public CustomEntityInterface(LootWorld lw,float health,double followRange,double knockbackResistance,
			double movementSpeed,double flyingSpeed
			,double attackDamage,double attackSpeed,double armor,
			double armorDurability,float armorPiercingLevel,float luck) {
		this.plugin = lw;
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
}
