package com.bawnorton.multitweaks.mixins;

import com.bawnorton.multitweaks.MultiTweaks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.lwjgl.system.CallbackI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(ChatHudListener.class)
public class ChatHudListenerMixin {
    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V"))
    public void updateChat(ChatHud chatHud, Text message) {
        String messageText = message.getString();
        switch (messageText) {
            case "Kingdom Chat: Enabled":
                MultiTweaks.currentChat = "Kingdom Chat";
                break;
            case "Kingdom Chat: Disabled":
            case "Visitation Chat: Disabled":
                if(MultiTweaks.currentChat.contains("Helper")) {
                    MultiTweaks.currentChat = "Helper Chat";
                }
                else {
                    MultiTweaks.currentChat = "Global Chat";
                }
                break;
            case "Helper Chat: Disabled":
                if(MultiTweaks.currentChat.contains("Kingodm")) {
                    MultiTweaks.currentChat = "Kingdom Chat";
                }
                else if (MultiTweaks.currentChat.contains("Visit")) {
                    MultiTweaks.currentChat = "Visit Chat";
                }
                else {
                    MultiTweaks.currentChat = "Global Chat";
                }
                break;
            case "Visitation Chat: Enabled":
                MultiTweaks.currentChat = "Visit Chat";
                break;
            case "Helper Chat: Enabled":
                if (MultiTweaks.currentChat.equals("Kingdom Chat")) {
                    MultiTweaks.currentChat = "Kingdom Chat and Helper Chat";
                } else if (MultiTweaks.currentChat.equals("Visit Chat")) {
                    MultiTweaks.currentChat = "Visit Chat and Helper Chat";
                }
                else {
                    MultiTweaks.currentChat = "Helper Chat";
                }
                break;
        }
        client.inGameHud.getChatHud().addMessage(message);
    }
}
