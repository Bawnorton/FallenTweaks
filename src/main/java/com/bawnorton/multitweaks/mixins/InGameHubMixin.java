package com.bawnorton.multitweaks.mixins;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.bawnorton.multitweaks.MultiTweaksClient.showScoreboard;


@Mixin(InGameHud.class)
public abstract class InGameHubMixin {

    @Shadow protected abstract void renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderScoreboardSidebar(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"))
    private void ignoreCall(InGameHud hud, MatrixStack matrices, ScoreboardObjective objective) {
        if(showScoreboard) {
            renderScoreboardSidebar(matrices, objective);
        }
    }
}
