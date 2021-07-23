package com.bawnorton.multitweaks.mixin;

import com.bawnorton.multitweaks.skin.SkinManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.bawnorton.multitweaks.Global.renderSetUUID;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "getTexture", at = @At("RETURN"), cancellable = true)
    public void getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity, CallbackInfoReturnable<Identifier> ci) {
        if(renderSetUUID && SkinManager.skinIdentifier != null) {
            renderSetUUID = false;
            ci.setReturnValue(SkinManager.skinIdentifier);
        }
    }
}
