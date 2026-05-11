package com.mochensky.deadlyconnection;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class DeadlyConnection implements ModInitializer {

    private static boolean isProcessingDeath = false;

    @Override
    public void onInitialize() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, _) -> {
            if (entity instanceof ServerPlayer deadPlayer && !isProcessingDeath) {
                isProcessingDeath = true;

                try {
                    ServerLevel world = deadPlayer.level();
                    for (ServerPlayer player :
                            world.getServer().getPlayerList().getPlayers()) {
                        if (player != deadPlayer && player.isAlive()) {
                            player.kill(world);
                        }
                    }
                } finally {
                    isProcessingDeath = false;
                }
            }
        });
    }
}