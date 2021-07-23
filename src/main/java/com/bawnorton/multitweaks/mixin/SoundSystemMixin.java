package com.bawnorton.multitweaks.mixin;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.bawnorton.multitweaks.Global.*;
import static com.bawnorton.multitweaks.Global.incomingSound;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    public void stopSound(SoundInstance soundInstance, CallbackInfo ci) {
        try {
            if(!incomingSound.equals("")) {
                switch (incomingSound) {
                    case "helperchat":
                        if (!helperDing) {
                            incomingSound = "";
                            ci.cancel();
                        }
                        break;
                    case "kingdomchat":
                        if (!kingdomDing) {
                            incomingSound = "";
                            ci.cancel();
                        }
                        break;
                    case "visitchat":
                        if (!visitDing) {
                            incomingSound = "";
                            ci.cancel();
                        }
                        break;
                    case "messagechat":
                        if (!messageDing) {
                            incomingSound = "";
                            ci.cancel();
                        }
                        break;
                    case "question":
                        if (!questionDing) {
                            incomingSound = "";
                            ci.cancel();
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (NullPointerException ignored) {}
    }
}
