package com.bawnorton.multitweaks.mixin;

import com.bawnorton.multitweaks.MultiTweaksClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bawnorton.multitweaks.Global.*;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable private ClientConnection connection;

    private int counter = 0;

    @Inject(method = "stop", at = @At("HEAD"))
    public void saveConfig(CallbackInfo ci) {
        try {
            MultiTweaksClient.saveConfig();
        } catch (IOException ignored) {
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void reduceTime(CallbackInfo callbackInfo) {
        if (trainTime > 0) {
            trainTime -= 0.05;
        } else {
            trainTime = 0;
        }
        if(client.world != null && client.player != null) {
            if (visitors.size() > 0) {
                counter++;
                if(counter == 20) {
                    for(String visitor: visitors.keySet()) visitors.replace(visitor, false);
                    for(AbstractClientPlayerEntity playerEntity: client.world.getPlayers()) {
                        for(String visitor: visitors.keySet()) {
                            if(playerEntity.getDisplayName().getString().contains(visitor)) {
                                visitors.replace(visitor, true);
                            }
                        }
                    }
                    List<String> toRemove = new ArrayList<>();
                    for(String visitor: visitors.keySet()) {
                        if(!visitors.get(visitor)) {
                            client.player.sendMessage(new LiteralText("§5§lVISITATION §d" + visitor + " §7has left your kingdom."), false);
                            toRemove.add(visitor);
                        }
                    }
                    for(String remove: toRemove) {
                        visitors.remove(remove);
                    }
                    counter = 0;
                }
            }
        }
    }
}
