package com.mochensky.deadlyconnection;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class DeadlyConnection implements ModInitializer {
    private static boolean isProcessingDeath = false;

    @Override
    public void onInitialize() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity && !isProcessingDeath) {
                isProcessingDeath = true;
                try {
                    ServerPlayerEntity deadPlayer = (ServerPlayerEntity) entity;
                    ServerWorld world = deadPlayer.getEntityWorld();

                    for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
                        if (player != deadPlayer && !player.isDead()) {
                            player.kill(player.getEntityWorld());
                        }
                    }
                } finally {
                    isProcessingDeath = false;
                }
            }
        });
    }
}