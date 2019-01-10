package me.hpms.lootworld;

public enum ChestRarity {
	COMMON(50f),
	UNCOMMON(30f),
	RARE(10f),
	MYTHICAL(5f),
	LEGENDARY(3f),
	IMMORTAL(1f),
	ARCANA(0.75f),
	ANCIENT(0.25f);
	
	private float probability;
	
	ChestRarity(float probability) {
		this.probability = probability;
	}
	
	public float getChestProbability() {
		return this.probability;
	}
	
}
