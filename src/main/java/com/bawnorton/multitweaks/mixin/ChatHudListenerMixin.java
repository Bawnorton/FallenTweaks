package com.bawnorton.multitweaks.mixin;

import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;

import static com.bawnorton.multitweaks.Global.currentChat;
import static com.bawnorton.multitweaks.Global.incomingSound;


@Mixin(ChatHudListener.class)
public class ChatHudListenerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Redirect(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V"))
    public void updateChat(ChatHud chatHud, Text message) {
        String messageText = message.getString();
        switch (messageText) {
            case "Kingdom Chat: Enabled":
                if (currentChat.contains("Helper")) {
                    currentChat = "Kingdom Chat and Helper Chat";
                } else {
                    currentChat = "Kingdom Chat";
                }
                break;
            case "Kingdom Chat: Disabled":
            case "Visitation Chat: Disabled":
                if (currentChat.contains("Helper")) {
                    currentChat = "Helper Chat";
                } else {
                    currentChat = "Global Chat";
                }
                break;
            case "Helper Chat: Disabled":
                if (currentChat.contains("Kingodm")) {
                    currentChat = "Kingdom Chat";
                } else if (currentChat.contains("Visit")) {
                    currentChat = "Visit Chat";
                } else {
                    currentChat = "Global Chat";
                }
                break;
            case "Visitation Chat: Enabled":
                if (currentChat.contains("Helper")) {
                    currentChat = "Visit Chat and Helper Chat";
                } else {
                    currentChat = "Visit Chat";
                }
                break;
            case "Helper Chat: Enabled":
                if (currentChat.contains("Kingdom")) {
                    currentChat = "Kingdom Chat and Helper Chat";
                } else if (currentChat.contains("Visit")) {
                    currentChat = "Visit Chat and Helper Chat";
                } else {
                    currentChat = "Helper Chat";
                }
                break;
        }
        assert client.player != null;
        if(messageText.startsWith("HELPER") && !(messageText.contains("participating") || messageText.contains("joined the network") || messageText.contains("left the network"))) {
            incomingSound = "helperchat";
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL, 1.0F, 0.5F);
        } else if (messageText.startsWith("KINGDOM")) {
            incomingSound = "kingdomchat";
        } else if (messageText.startsWith("VISITATION")) {
            incomingSound = "visitchat";
        } else if (messageText.startsWith("From") || messageText.startsWith("To")) {
            incomingSound = "messagechat";
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL, 1.0F, 0.5F);
        } else if (messageText.contains("where") || messageText.contains("how") || messageText.contains("when") || messageText.contains("why") || messageText.contains("what") || messageText.contains("?")) {
            incomingSound = "question";
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL, 1.0F, 0.5F);
        }
        client.inGameHud.getChatHud().addMessage(message);
    }
}
