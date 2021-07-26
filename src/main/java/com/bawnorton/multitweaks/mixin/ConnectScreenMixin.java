package com.bawnorton.multitweaks.mixin;

import net.minecraft.client.gui.screen.ConnectScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.bawnorton.multitweaks.Global.ipAddress;

@Mixin(ConnectScreen.class)
public abstract class ConnectScreenMixin {
    @Inject(method = "connect", at = @At("HEAD"))
    public void saveAddress(final String address, final int port, CallbackInfo ci) {
        ipAddress = address;
    }
}
