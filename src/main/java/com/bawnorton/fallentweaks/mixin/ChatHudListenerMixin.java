package com.bawnorton.fallentweaks.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

import static com.bawnorton.fallentweaks.Global.*;


@Mixin(ChatHudListener.class)
public class ChatHudListenerMixin {
    @Shadow
    @Final
    private MinecraftClient client;


    @Redirect(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V"))
    public void updateChat(ChatHud chatHud, Text message) {
        client.inGameHud.getChatHud().addMessage(message);
        if (!ipAddress.contains("fallenkingdom")) return;
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
            case "Staff Chat: Disabled":
            case "Kingdom Chat: Disabled":
            case "Visitation Chat: Disabled":
                if (sendSpamMessage) {
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
                if (sendSpamMessage) {
                    client.player.sendChatMessage("Please don't character spam");
                    sendSpamMessage = false;
                }
                if (currentChat.contains("Kingdom")) {
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
            case "Staff Chat: Enabled":
                currentChat = "Staff Chat";
                break;

        }
        if (messageText.startsWith("HELPER") && !(messageText.contains("participating") || messageText.contains("joined the network") || messageText.contains("left the network") || messageText.contains("joined the lobby"))) {
            incomingSound = "helperchat";
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL, 1.0F, 0.5F);
        } else if (messageText.startsWith("KINGDOM")) {
            incomingSound = "kingdomchat";
        } else if (messageText.startsWith("VISITATION")) {
            incomingSound = "visitchat";
        } else if (messageText.startsWith("STAFF")) {
            incomingSound = "staffchat";
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL, 1.0F, 0.5F);
        } else if (messageText.startsWith("From ") || messageText.startsWith("To ")) {
            incomingSound = "messagechat";
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL, 1.0F, 0.5F);
        } else if (messageText.contains("where") || messageText.contains("how") || messageText.contains("when") || messageText.contains("why") || messageText.contains("what") || messageText.contains("?")) {
            if(!messageText.contains(client.player.getDisplayName().getString())) {
                incomingSound = "question";
                client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL, 1.0F, 0.5F);
            }
        } else if (messageText.startsWith("Billy") || messageText.startsWith("Joe")) {
            incomingSound = "farm";
        } else if (messageText.startsWith("Aaron")) {
            incomingSound = "barracks";
        } else if (messageText.startsWith("Jerry")) {
            incomingSound = "blacksmith";
        }

        if (messageText.matches("(.*)Started training of your (.*)! You have (.*) spaces left!")) {
            String troopName = messageText.substring(messageText.indexOf("your") + 5, messageText.indexOf("!"));
            trainTime += savedRank.equals("KNIGHT") ? troopTimes.get(troopName) * 0.95 :
                         savedRank.equals("NOBLE") ? troopTimes.get(troopName) * 0.90 :
                         savedRank.equals("MAJESTY") ? troopTimes.get(troopName) * 0.85 :
                         troopTimes.get(troopName) ;
        }

        if (messageText.startsWith("VISITATION") && messageText.contains("is now visiting your kingdom")) {
            if(leaveVisitors) {
                String playerName = messageText.substring(messageText.indexOf("VISITATION") + 11, messageText.indexOf(" is now"));
                visitors.put(playerName, true);
            }
        }

        boolean charSpam = containsSpam(messageText);
        outer:
        if (charSpam) {
            if (messageText.contains(client.player.getEntityName()) || messageText.startsWith("KINGDOM") || messageText.startsWith("VISITATION") || messageText.startsWith("HELPER"))
                break outer;
            if (autoCharSpam) {
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
            String spamMessage = messageText.substring(messageText.indexOf(":") + 1);
            String playerIdentity = messageText.substring(0, messageText.indexOf(":"));
            String playerName = playerIdentity.substring(playerIdentity.lastIndexOf(" "));
            String date = java.time.LocalDate.now().toString();
            String time = java.time.LocalTime.now().toString();
            String item = date + " " + time + ": " + spamMessage;
            if (spammers.containsKey(playerName)) {
                List<String> messages = spammers.get(playerName);
                messages.add(item);
                spammers.replace(playerName, messages);
            } else {
                List<String> messages = new ArrayList<>();
                messages.add(item);
                spammers.put(playerName, messages);
            }
        }
    }

    private boolean containsSpam(String messageText) {
        if (!messageText.contains(":")) return false;
        char previous = ' ';
        int count = 1;
        for (char c : messageText.toCharArray()) {
            if (c == previous) count++;
            else {
                count = 1;
                previous = c;
            }
        }
        return count >= 6;
    }
}
