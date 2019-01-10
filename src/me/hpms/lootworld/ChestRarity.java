package me.hpms.lootworld;

public enum ChestRarity {
	COMMON(47.5f,"Common"),
	UNCOMMON(20f,"Uncommon"),
	RARE(12f,"Rare"),
	MYTHICAL(7f,"Mythical"),
	LEGENDARY(5f,"Legendary"),
	IMMORTAL(4f,"Immortal"),
	ARCANA(2.75f,"Arcana"),
	ANCIENT(1.75f,"Ancient");
	
	private float probability;
	private String name;
	
	ChestRarity(float probability,String name) {
		this.probability = probability;
		this.name = name;
	}
	
	public String getChestName() {
		return this.name;
	}
	
	public float getChestProbability() {
		return this.probability;
	}
	
}
