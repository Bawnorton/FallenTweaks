package com.bawnorton.multitweaks.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.bawnorton.multitweaks.Global.*;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    @Final
    private List<ChatHudLine<Text>> messages;
    @Shadow
    @Final
    private List<ChatHudLine<OrderedText>> visibleMessages;

    @Shadow
    public abstract void addMessage(Text message);

    @Shadow public abstract double getChatScale();

    @Shadow public abstract int getWidth();

    private static int indexOffset = -1;

    /**
     * @author curmor
     * @reason @Redirect was causing runtime errors
     * and @Overwrite achieves the same thing that I
     * was trying to do without any errors
     */
    @Overwrite
    private boolean isChatFocused() {
        renderChatType = false;
        renderBarracksTime = false;
        if (this.client.currentScreen instanceof ChatScreen) {
            if (client.player == null) return true;
            if (displayChat) renderChatType = true;
            if (barracksTime) renderBarracksTime = true;
            return true;
        }
        return false;
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"), cancellable = true)
    private void removeMessage(Text message, int messageId, int timestamp, boolean refresh, CallbackInfo callbackInfo) {
        String messageString = message.getString();
        LiteralText replacementText = null;
        if (betterBank) {
            String noResource = "(.*)You do not have enough (.*) to sell\\.";
            String maxGold = "(.*)Your bank’s vault has reached its max capacity! Please upgrade your bank to increase its capacity\\.";
            String sellingResource = "(.*)Sold (.*) for (.*) Gold ꀕ\\.";
            if (messageString.matches(noResource)) {
                removeLastMessage(noResource);
            } else if (messageString.matches(maxGold)) {
                String replacement = "§7Reached max capacity for §6Gold §7ꀕ";
                replacementText = new LiteralText(replacement + ".");
                removeLastMessage(replacement + "\\.");
                this.addMessage(replacementText);
                callbackInfo.cancel();
            } else if (messageString.matches(sellingResource)) {
                removeLastMessage(sellingResource);
            }
        }
        if (betterFarm) {
            String maxStorage = "(.*)Your bank’s vault has reached its max capacity for (.*) Please upgrade your bank or spend some of your resources before you try again\\.";
            String noHarvest = "(.*)There is nothing to be harvested\\.";
            if (messageString.matches(maxStorage)) {
                String replacement = "§7Reached max capacity for §9" + messageString.substring(messageString.indexOf("for") + 4, messageString.indexOf(" Please"));
                replacementText = new LiteralText(replacement);
                removeLastMessage(replacement);
                this.addMessage(replacementText);
                callbackInfo.cancel();
            } else if (messageString.matches(noHarvest)) {
                removeLastMessage(noHarvest);
            }
        }
        if (betterTroops) {
            String noResource = "(.*)You don't have enough (.*). You need at least (.*) to do this transaction\\.";
            String noSpace = "(.*)You don't have enough room in your barracks to train this troop.";
            String trainTroop = "(.*)Started training of your (.*)! You have (.*) spaces left!";
            if (messageString.matches(noResource)) {
                String replacement = "§cYou need §4" + messageString.substring(messageString.indexOf("least") + 6, messageString.indexOf(" to")) + " §b" + messageString.substring(messageString.indexOf("enough") + 7, messageString.indexOf(". You")) + " §cto train this troop";
                replacementText = new LiteralText(replacement + ".");
                removeLastMessage(replacement + "\\.");
                this.addMessage(replacementText);
                callbackInfo.cancel();
            } else if (messageString.matches(noSpace)) {
                String replacement = "§cYour Barracks is full";
                replacementText = new LiteralText(replacement + ".");
                removeLastMessage(replacement + "\\.");
                this.addMessage(replacementText);
                callbackInfo.cancel();
            } else if (messageString.matches(trainTroop)) {
                String replacement = "§aTraining: §2" + messageString.substring(messageString.indexOf("your") + 5, messageString.indexOf("! Y")) + "§a | Space left: §2" + messageString.substring(messageString.indexOf("have") + 5, messageString.indexOf(" spaces"));
                replacementText = new LiteralText(replacement + ".");
                this.addMessage(replacementText);
                callbackInfo.cancel();
            }
        }
        if (betterCombat) {
            String inCombat = "(.*)You're in combat! You can logout safely after (.*)\\.";
            String outCombat = "(.*)You're no longer in combat! You can logout safely\\.";
            String teleporting = "(.*)Teleporting to your kingdom home in (.*)";
            String addQuiver = "(.*)arrows were added to your quiver\\. (.*)";
            String removeQuiver = "(.*)arrow was removed from your quiver\\. (.*)";
            String fullQuiver = "(.*)Your Quiver is full(.*)";
            if (messageString.matches(inCombat)) {
                String replacement = "§c§lCOMBAT §8| §7You're in combat for §c" + messageString.substring(messageString.indexOf("after") + 6, messageString.indexOf("sec")) + "seconds";
                replacementText = new LiteralText(replacement + ".");
                removeLastMessage(replacement.replaceAll("\\|", "\\\\|") + "\\.");
                this.addMessage(replacementText);
                callbackInfo.cancel();
            } else if (messageString.matches(outCombat)) {
                removeLastMessage(outCombat);
            } else if (messageString.matches(teleporting)) {
                removeLastMessage(teleporting);
            } else if (messageString.matches(addQuiver)) {
                removeLastMessage(addQuiver);
            } else if (messageString.matches(removeQuiver)) {
                removeLastMessage(removeQuiver);
            } else if (messageString.matches(fullQuiver)) {
                removeLastMessage(fullQuiver);
            }
        }
        if (betterRaid) {
            String noStamina = "You don't have enough stamina. You need at least (.*) for this troop\\.";
            String offPath = "You can only hit mobs when you're on the path\\.";
            String notKingdom = "You're not part of this kingdom\\.";
            String noTroop = "You don't have any troops of the type (.*) left\\.";
            if (messageString.matches(noStamina)) {
                String replacement = "§cYou need §b" + messageString.substring(messageString.indexOf("least") + 6, messageString.indexOf(" for")) + " §cstamina for this troop";
                replacementText = new LiteralText(replacement + ".");
                removeLastMessage(replacement + "\\.");
                this.addMessage(replacementText);
                callbackInfo.cancel();
            } else if (messageString.matches(offPath)) {
                removeLastMessage(offPath);
            } else if (messageString.matches(notKingdom)) {
                removeLastMessage(notKingdom);
            } else if (messageString.matches(noTroop)) {
                removeLastMessage(noTroop);
            }
        }
        int i = MathHelper.floor((double)this.getWidth() / this.getChatScale());
        int size = ChatMessages.breakRenderedChatMessageLines(replacementText == null ? message : replacementText, i, this.client.textRenderer).size() - 1;
        indexOffset += size;
    }

    private void removeLastMessage(String regex) {
        int index = -1;
        for (ChatHudLine<Text> chatHudLine : messages) {
            if (chatHudLine.getText().getString().matches(regex)) {
                index = messages.indexOf(chatHudLine);
            }
        }
        if (index != -1) {
            messages.remove(index);
            visibleMessages.remove(index + indexOffset);
            indexOffset = 0;
        }
    }
}
