package com.bawnorton.multitweaks.mixin;

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

import static com.bawnorton.multitweaks.Global.*;


@Mixin(ChatHudListener.class)
public class ChatHudListenerMixin {
    @Shadow
    @Final
    private MinecraftClient client;


    @Redirect(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V"))
    public void updateChat(ChatHud chatHud, Text message) {
        String messageText = message.getString();
        assert client.player != null;
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
                if(sendSpamMessage) {
                    client.player.sendChatMessage("Please don't character spam");
                    sendSpamMessage = false;
                }
                if (currentChat.contains("Helper")) {
                    currentChat = "Helper Chat";
                } else {
                    currentChat = "Global Chat";
                }
                break;
            case "Helper Chat: Disabled":
                if(sendSpamMessage) {
                    client.player.sendChatMessage("Please don't character spam");
                    sendSpamMessage = false;
                }
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
        if(messageText.startsWith("HELPER") && !(messageText.contains("participating") || messageText.contains("joined the network") || messageText.contains("left the network") || messageText.contains("joined the lobby"))) {
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
        } else if (messageText.startsWith("Billy") || messageText.startsWith("Joe")) {
            incomingSound = "farm";
        } else if (messageText.startsWith("Aaron")) {
            incomingSound = "barracks";
        } else if (messageText.startsWith("Jerry")) {
            incomingSound = "blacksmith";
        }
        boolean charSpam = containsSpam(messageText);
        outer: if(charSpam) {
            if(autoCharSpam) {
                if(messageText.contains(client.player.getEntityName()) || messageText.startsWith("KINGDOM") || messageText.startsWith("VISITATION") || messageText.startsWith("HELPER")) break outer;
                switch (currentChat) {
                    case "Kingdom Chat":
                        client.player.sendChatMessage("/k c");
                        sendSpamMessage = true;
                        break;
                    case "Visit Chat":
                        client.player.sendChatMessage("/vc");
                        sendSpamMessage = true;
                        break;
                    case "Helper Chat":
                        client.player.sendChatMessage("/hc");
                        sendSpamMessage = true;
                        break;
                    case "":
                        break;
                    default:
                        client.player.sendChatMessage("Please don't character spam");
                        break;
                }
            }
            String playerName = messageText.substring(messageText.substring(0, messageText.indexOf(":")).lastIndexOf(" ") + 1);
            if(spammers.containsKey(playerName)) {
                spammers.replace(playerName, spammers.get(playerName) + 1);
            } else {
                spammers.put(playerName, 1);
            }
        }
        client.inGameHud.getChatHud().addMessage(message);
    }
    private boolean containsSpam(String messageText) {
        char previous = ' ';
        int count = 1;
        for(char c: messageText.toCharArray()) {
            if(c == previous) count++;
            else {
                count = 1;
                previous = c;
            }
        }
        return count >= 7;
    }
}
