package com.github.breadbyte.itemcustomizer.mixins;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "addCritParticles(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    public void mixin$addCritParticles(Entity target, CallbackInfo ci) {
        ItemCustomizer.LOGGER.info("PlayerEntityMixin mixin$addCritParticles triggered");
        if (!(target instanceof LivingEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity)(Object)this;
        var mainHand = player.getMainHandStack();
        var cmd = mainHand.getComponents().get(net.minecraft.component.DataComponentTypes.CUSTOM_MODEL_DATA);
        if (cmd != null) {
            // 2 for critical hit sound
            var sound = cmd.getString(2);
            if (sound != null && !sound.isEmpty()) {
                player.getEntityWorld().playSoundFromEntity(null, player, net.minecraft.sound.SoundEvent.of(net.minecraft.util.Identifier.of(sound)), net.minecraft.sound.SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }
    }

    @Inject(method = "spawnSweepAttackParticles()V", at = @At("HEAD"))
    public void mixin$doSweep(CallbackInfo ci) {
        ItemCustomizer.LOGGER.info("PlayerEntityMixin mixin$doSweep triggered");
        PlayerEntity player = (PlayerEntity)(Object)this;
        var mainHand = player.getMainHandStack();
        var cmd = mainHand.getComponents().get(net.minecraft.component.DataComponentTypes.CUSTOM_MODEL_DATA);
        if (cmd != null) {
            // 3 for sweep attack sound
            var sound = cmd.getString(3);
            if (sound != null && !sound.isEmpty()) {
                player.getEntityWorld().playSoundFromEntity(null, player, net.minecraft.sound.SoundEvent.of(net.minecraft.util.Identifier.of(sound)), net.minecraft.sound.SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }
    }
}
