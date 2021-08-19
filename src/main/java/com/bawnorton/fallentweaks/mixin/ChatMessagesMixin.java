package com.bawnorton.fallentweaks.mixin;

import com.bawnorton.fallentweaks.Global;
import com.google.common.collect.Lists;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;

@Mixin(ChatMessages.class)
public class ChatMessagesMixin {

    @Shadow
    @Final
    private static OrderedText field_25263;

    @Shadow
    private static String getRenderedChatMessage(String message) {
        return null;
    }

    /**
     * @author curmor
     * @reason im lazy and not super familiar with how to inject inside of runnables
     */
    @Overwrite
    public static List<OrderedText> breakRenderedChatMessageLines(StringVisitable stringVisitable, int width, TextRenderer textRenderer) {
        TextCollector textCollector = new TextCollector();
        stringVisitable.visit((style, string) -> {
            textCollector.add(StringVisitable.styled(getRenderedChatMessage(string), style));
            return Optional.empty();
        }, Style.EMPTY);
        List<OrderedText> list = Lists.newArrayList();
        textRenderer.getTextHandler().method_29971(textCollector.getCombined(), width, Style.EMPTY, (stringVisitablex, boolean_) -> {
            Global.visibleStringMessages.add(stringVisitablex.getString());
            OrderedText orderedText = Language.getInstance().reorder(stringVisitablex);
            list.add(boolean_ ? OrderedText.concat(field_25263, orderedText) : orderedText);
        });
        return list.isEmpty() ? Lists.newArrayList(OrderedText.EMPTY) : list;
    }
}
