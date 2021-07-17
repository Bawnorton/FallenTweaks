package com.bawnorton.multitweaks.mixins;

import com.bawnorton.multitweaks.MultiTweaks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Shadow @Final private MinecraftClient client;

    /**
     * @author curmor
     * @reason @Redirect was causing runtime errors
     * and @Overwrite achieves the same thing that I
     * was trying to do without any errors
     */
    @Overwrite
    private boolean isChatFocused() {
        if(this.client.currentScreen instanceof ChatScreen) {
            if(client.player == null) return true;
            client.player.sendMessage(new LiteralText("You are in " + MultiTweaks.currentChat), true);
            return true;
        }
        return false;
    }
}