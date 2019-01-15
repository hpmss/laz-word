package me.hpms.lootworld;

import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory;
import org.bukkit.event.entity.EntityShootBowEvent;

import net.minecraft.server.v1_12_R1.Blocks;
import net.minecraft.server.v1_12_R1.DifficultyDamageScaler;
import net.minecraft.server.v1_12_R1.EntityArrow;
import net.minecraft.server.v1_12_R1.EntityCreeper;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.EntitySkeleton;
import net.minecraft.server.v1_12_R1.EntityTippedArrow;
import net.minecraft.server.v1_12_R1.EntityZombie;
import net.minecraft.server.v1_12_R1.GroupDataEntity;
import net.minecraft.server.v1_12_R1.IRangedEntity;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.Items;
import net.minecraft.server.v1_12_R1.MathHelper;
import net.minecraft.server.v1_12_R1.PathfinderGoalArrowAttack;
import net.minecraft.server.v1_12_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_12_R1.SoundEffects;
import net.minecraft.server.v1_12_R1.World;

public class CustomEntityMob extends EntityZombie implements IRangedEntity{
	
	//Test class for NMSEntity
	
	public CustomEntityMob(World world) {
		super(world);
	}
	
	@Override
	protected void r() {
		super.r();
		this.goalSelector.a(2,new PathfinderGoalArrowAttack(this,1.0,12,20));
		this.targetSelector.a(3,new PathfinderGoalNearestAttackableTarget<>(this,EntityCreeper.class,true));
		this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this,EntitySkeleton.class,true));
	}
	
	
	@Override
	public void a(final EntityLiving target, final float f) {
	    final EntityArrow entityarrow = this.prepareProjectile(f);
	    final double motX = target.locX - this.locX;
	    final double motY = target.getBoundingBox().b + target.length / 3.0f - entityarrow.locY;
	    final double motZ = target.locZ - this.locZ;
	    final double horizontalMot = MathHelper.sqrt(motX * motX + motZ * motZ);
	    entityarrow.shoot(motX, motY + horizontalMot * 0.2, motZ, 1.6f, 14 - world.getDifficulty().a() * 4);
	    final EntityShootBowEvent event = CraftEventFactory.callEntityShootBowEvent(this, this.getItemInMainHand(),
	                entityarrow, 0.8f);
	    if (event.isCancelled()) {
	        event.getProjectile().remove();
	        return;
	    }
	    if (event.getProjectile() == entityarrow.getBukkitEntity()) {
	        this.world.addEntity(entityarrow);
	    }
	    this.a(SoundEffects.fV, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
	}

	protected EntityArrow prepareProjectile(final float unknown) {
	    final EntityArrow arrow = new EntityTippedArrow(this.world, this);
	    arrow.a(this, unknown);
	    return arrow;
	}
	
	@Override
	public GroupDataEntity prepare(DifficultyDamageScaler dds,GroupDataEntity gde) {
		gde = super.prepare(dds, gde);
		this.setSlot(NMSEntity.NMSEntitySlot.MAIN_HAND.getValue(), new ItemStack(Items.BOW));
		this.setSlot(NMSEntity.NMSEntitySlot.HEAD.getValue(), new ItemStack(Blocks.PUMPKIN));
		return gde;
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(NMSEntity.NMSAttributes.MAX_HEALTH.getValue()).setValue(1000.0);
		Log.info(this.getAttributeInstance(NMSEntity.NMSAttributes.MAX_HEALTH.getValue()).getValue());
		this.getAttributeInstance(NMSEntity.NMSAttributes.ARMOR.getValue()).setValue(5.0);
	}
	
	
	

}
