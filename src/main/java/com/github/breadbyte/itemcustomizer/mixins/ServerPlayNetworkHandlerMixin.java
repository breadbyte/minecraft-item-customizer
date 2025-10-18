package com.github.breadbyte.itemcustomizer.mixins;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onUpdateSelectedSlot(Lnet/minecraft/network/packet/c2s/play/UpdateSelectedSlotC2SPacket;)V", at = @At("HEAD"))
    public void mixin$onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket pkt, CallbackInfo ci) {
        ItemCustomizer.LOGGER.info("onUpdateSelectedSlot triggered");
        var pl = this.player;

        // Get targeted slot
        var mainhand = pl.getInventory().getStack(pkt.getSelectedSlot());

        var cmd = mainhand.getComponents().get(net.minecraft.component.DataComponentTypes.CUSTOM_MODEL_DATA);
        if (cmd != null) {
            // 1 for swapping sound
            var sound = cmd.getString(1);
            if (sound != null && !sound.isEmpty()) {
                pl.getEntityWorld().playSoundFromEntity(null, pl, net.minecraft.sound.SoundEvent.of(net.minecraft.util.Identifier.of(sound)), net.minecraft.sound.SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }

    }
}
