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
    public abstract double getChatScale();

    @Shadow
    public abstract int getWidth();

    @Shadow
    public abstract void addMessage(Text message);


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
        if (betterBank) {
            String noResource = "(.*)You do not have enough (.*) to sell\\.";
            String maxGold = "(.*)Your bank’s vault has reached its max capacity! Please upgrade your bank to increase its capacity\\.";
            String sellingResource = "(.*)Sold (.*) for (.*) Gold ꀕ\\.";
            if (messageString.matches(noResource)) {
                removeLastMessage(message, noResource);
            } else if (messageString.matches(maxGold)) {
                String replacement = "§7Reached max capacity for §6Gold §7ꀕ";
                LiteralText replacementText = new LiteralText(replacement + ".");
                removeLastMessage(replacementText, replacement + "\\.");
                this.addMessage(replacementText);
                callbackInfo.cancel();
            } else if (messageString.matches(sellingResource)) {
                removeLastMessage(message, sellingResource);
            }
        }
        if (betterFarm) {
            String maxStorage = "(.*)Your bank’s vault has reached its max capacity for (.*) Please upgrade your bank or spend some of your resources before you try again\\.";
            if (messageString.matches(maxStorage)) {
                String replacement = "§7Reached max capacity for §9" + messageString.substring(messageString.indexOf("for") + 4, messageString.indexOf(" Please"));
                LiteralText replacementText = new LiteralText(replacement);
                removeLastMessage(replacementText, replacement);
                this.addMessage(replacementText);
                callbackInfo.cancel();
            }
        }
        if (betterTroops) {
            String noResource = "(.*)You don't have enough (.*). You need at least (.*) to do this transaction\\.";
            String noSpace = "(.*)You don't have enough room in your barracks to train this troop.";
            String trainTroop = "(.*)Started training of your (.*)! You have (.*) spaces left!";
            if (messageString.matches(noResource)) {
                removeLastMessage(message, noResource);
            } else if (messageString.matches(noSpace)) {
                removeLastMessage(message, noSpace);
            } else if (messageString.matches(trainTroop)) {
                removeLastMessage(message, trainTroop);

            }
        }
        if (betterCombat) {
            String inCombat = "(.*)You're in combat! You can logout safely after (.*)\\.";
            String outCombat = "(.*)You're no longer in combat! You can logout safely\\.";
            String teleporting = "(.*)Teleporting to your kingdom home in (.*)";
            String addQuiver = "(.*)arrows were added to your quiver. (.*)";
            String fullQuiver = "(.*)Your Quiver is full, you can't pickup any new arrows\\.";
            if (messageString.matches(inCombat)) {
                removeLastMessage(message, inCombat);
            } else if (messageString.matches(outCombat)) {
                removeLastMessage(message, outCombat);
            } else if (messageString.matches(teleporting)) {
                removeLastMessage(message, teleporting);
            } else if (messageString.matches(addQuiver)) {
                removeLastMessage(message, addQuiver);
            } else if (messageString.matches(fullQuiver)) {
                removeLastMessage(message, fullQuiver);
            }
        }
        if (betterRaid) {
            String noStamina = "You don't have enough stamina. You need at least (.*) for this troop\\.";
            String offPath = "You can only hit mobs when you're on the path\\.";
            String notKingdom = "You're not part of this kingdom\\.";
            String noTroop = "You don't have any troops of the type (.*) left\\.";
            if (messageString.matches(noStamina)) {
                removeLastMessage(message, noStamina);
            } else if (messageString.matches(offPath)) {
                removeLastMessage(message, offPath);
            } else if (messageString.matches(notKingdom)) {
                removeLastMessage(message, notKingdom);
            } else if (messageString.matches(noTroop)) {
                removeLastMessage(message, noTroop);
            }
        }
    }

    private void removeLastMessage(Text message, String regex) {
        int index = -1;
        for (ChatHudLine<Text> chatHudLine : messages) {
            if (chatHudLine.getText().getString().matches(regex)) {
                index = messages.indexOf(chatHudLine);
            }
        }
        if (index != -1) {
            int i = MathHelper.floor((double) this.getWidth() / this.getChatScale());
            int num = ChatMessages.breakRenderedChatMessageLines(message, i, this.client.textRenderer).size();
            messages.remove(index);
            for (int j = 0; j < num; j++) {
                visibleMessages.remove(index);
            }
        }
    }
}
