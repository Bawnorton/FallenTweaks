package com.bawnorton.multitweaks.mixin;

import com.bawnorton.multitweaks.MultiTweaksClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

import static com.bawnorton.multitweaks.Global.trainTime;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
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
    }
}
