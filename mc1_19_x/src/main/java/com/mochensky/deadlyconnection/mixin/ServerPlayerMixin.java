package com.mochensky.deadlyconnection.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin {
    @Unique
    private static boolean isProcessingDeath = false;

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        if (isProcessingDeath) return;

        isProcessingDeath = true;
        try {
            ServerPlayerEntity deadPlayer = (ServerPlayerEntity) (Object) this;
            ServerWorld world = deadPlayer.getWorld();
            for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
                if (player != deadPlayer && !player.isDead()) {
                    player.kill();
                }
            }
        } finally {
            isProcessingDeath = false;
        }
    }
}