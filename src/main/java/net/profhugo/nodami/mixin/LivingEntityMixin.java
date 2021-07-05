package net.profhugo.nodami.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import net.profhugo.nodami.interfaces.EntityHurtCallback;
import net.profhugo.nodami.interfaces.EntityKnockbackCallback;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Shadow @Nullable public abstract LivingEntity getAttacking();

	@Shadow @Nullable public abstract DamageSource getRecentDamageSource();

	@Shadow @Nullable public abstract LivingEntity getAttacker();

	@Inject(at = @At("TAIL"), method = "applyDamage", cancellable = true)
	private void onEntityHurt(final DamageSource source, final float amount, CallbackInfo info) {
		ActionResult result = EntityHurtCallback.EVENT.invoker().hurtEntity((LivingEntity) (Object) this, source,
				amount);
		if (result == ActionResult.FAIL) {
			info.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "takeKnockback", cancellable = true)
	private void onTakingKnockback(double strength, double x, double z, CallbackInfo ci) {
		ActionResult result = EntityKnockbackCallback.EVENT.invoker().takeKnockback((LivingEntity) (Object) this,
				this.getAttacker(), (float) strength, x, z);
		if (result == ActionResult.FAIL) {
			ci.cancel();
		}

	}
}
