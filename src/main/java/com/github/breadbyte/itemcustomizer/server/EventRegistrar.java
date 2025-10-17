package com.github.breadbyte.itemcustomizer.server;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EventRegistrar {

    public static ActionResult onAttackEntity(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        ItemCustomizer.LOGGER.info("AttackEntityCallback triggered");
        var cmodeldata = player.getMainHandStack().getComponents().get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (cmodeldata == null) {
            return ActionResult.PASS;
        }

        var sound = cmodeldata.getString(0);
        if (sound != null && !sound.isEmpty()) {

            world.playSoundFromEntity(null, player, net.minecraft.sound.SoundEvent.of(Identifier.of(sound)), SoundCategory.PLAYERS, 1.0f, 1.0f);
            return ActionResult.PASS;
        }

        return ActionResult.PASS;
    }

    public static void onAfterDamage(LivingEntity livingEntity, DamageSource damageSource, float v, float v1, boolean b) {
        ItemCustomizer.LOGGER.info("onAfterDamage triggered");
        if (!(livingEntity instanceof PlayerEntity player)) {
            return;
        }

        var cmodeldata = player.getMainHandStack().getComponents().get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (cmodeldata == null) {
            ItemCustomizer.LOGGER.info("No custom model data found");
            return;
        }

        var sound = cmodeldata.getString(0);
        if (sound != null && !sound.isEmpty()) {
            ItemCustomizer.LOGGER.info("Playing sound: {}", sound);
            livingEntity.getEntityWorld().playSoundFromEntity(null, player, net.minecraft.sound.SoundEvent.of(Identifier.of(sound)), SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
    }
}
