package com.bawnorton.multitweaks.config;

import com.bawnorton.multitweaks.MultiTweaksClient;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bawnorton.multitweaks.Global.*;

@SuppressWarnings("rawtypes")
public class MultiTweaksConfig {

    public static ConfigBuilder buildScreen(String name, Screen parent) {
        builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new TranslatableText(name));
        ConfigCategory keybindCategory = builder.getOrCreateCategory(new TranslatableText("category.multitweaks.keybind"));
        ConfigCategory soundCategory = null;
        ConfigCategory utilityCategory = null;
        ConfigCategory spamCategory = null;
        if (ipAddress.contains("fallenkingdom") || inDev) {
            soundCategory = builder.getOrCreateCategory(new TranslatableText("category.multitweaks.sounds"));
            utilityCategory = builder.getOrCreateCategory(new TranslatableText("category.multitweaks.utility"));
            spamCategory = builder.getOrCreateCategory(new TranslatableText("category.multitweaks.spam"));
        }
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        List<List<AbstractConfigListEntry>> entries = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            int finalI = i;
            if (keybindSettings[finalI] == null) {
                keybindSettings[finalI] = new KeybindSettings(InputUtil.UNKNOWN_KEY, "");
            }
            List<AbstractConfigListEntry> categories = new ArrayList<>();
            categories.add(entryBuilder.startKeyCodeField(new TranslatableText("option.multitweaks.keybind"), keybindSettings[finalI].key)
                    .setDefaultValue(InputUtil.UNKNOWN_KEY)
                    .setSaveConsumer(newValue -> keybindSettings[finalI].key = newValue)
                    .build());
            categories.add(entryBuilder.startTextField(new TranslatableText("option.multitweaks.text"), keybindSettings[finalI].phrase)
                    .setDefaultValue("")
                    .setSaveConsumer(newValue -> keybindSettings[finalI].phrase = newValue)
                    .build());
            entries.add(categories);
            keybindCategory.addEntry(entryBuilder.startSubCategory(new LiteralText("Bind " + (i + 1) + ": " +
                            ((KeyCodeEntry) entries.get(i).get(0)).getValue().getKeyCode().getLocalizedText().getString() + " -> " + keybindSettings[finalI].phrase),
                    categories).build());
        }
        if (utilityCategory != null) {
            soundCategory.addEntry(entryBuilder.startTextDescription(new LiteralText("Chat Sounds"))
                    .build());
            soundCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Kingdom Chat"), kingdomDing)
                    .setDefaultValue(kingdomDing)
                    .setSaveConsumer(newValue -> kingdomDing = newValue)
                    .build());
            soundCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Visit Chat"), visitDing)
                    .setDefaultValue(visitDing)
                    .setSaveConsumer(newValue -> visitDing = newValue)
                    .build());
            soundCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Helper Chat"), helperDing)
                    .setDefaultValue(helperDing)
                    .setSaveConsumer(newValue -> helperDing = newValue)
                    .build());
            soundCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Staff Chat"), staffDing)
                    .setDefaultValue(staffDing)
                    .setSaveConsumer(newValue -> staffDing = newValue)
                    .build());
            soundCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Direct Messages"), messageDing)
                    .setDefaultValue(messageDing)
                    .setSaveConsumer(newValue -> messageDing = newValue)
                    .build());
            soundCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Question Words"), questionDing)
                    .setDefaultValue(questionDing)
                    .setSaveConsumer(newValue -> questionDing = newValue)
                    .build());
            soundCategory.addEntry(entryBuilder.startTextDescription(new LiteralText("Kingdom Sounds"))
                    .build());
            soundCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Farm"), farmDing)
                    .setDefaultValue(farmDing)
                    .setSaveConsumer(newValue -> farmDing = newValue)
                    .build());
            soundCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Barracks"), barracksDing)
                    .setDefaultValue(barracksDing)
                    .setSaveConsumer(newValue -> barracksDing = newValue)
                    .build());
            soundCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Blacksmith"), blacksmithDing)
                    .setDefaultValue(blacksmithDing)
                    .setSaveConsumer(newValue -> blacksmithDing = newValue)
                    .build());
            utilityCategory.addEntry(entryBuilder.startTextDescription(new LiteralText("Better Chat"))
                    .build());
            utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Better Bank Capacity Messages"), betterBank)
                    .setDefaultValue(betterBank)
                    .setSaveConsumer(newValue -> betterBank = newValue)
                    .build());
            utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Better Combat Messages"), betterCombat)
                    .setDefaultValue(betterCombat)
                    .setSaveConsumer(newValue -> betterCombat = newValue)
                    .build());
            utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Better Troop Training Messages"), betterTroops)
                    .setDefaultValue(betterTroops)
                    .setSaveConsumer(newValue -> betterTroops = newValue)
                    .build());
            utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Better Farm Selling Messages"), betterFarm)
                    .setDefaultValue(betterFarm)
                    .setSaveConsumer(newValue -> betterFarm = newValue)
                    .build());
            utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Better Raid Messages"), betterRaid)
                    .setDefaultValue(betterRaid)
                    .setSaveConsumer(newValue -> betterRaid = newValue)
                    .build());
            utilityCategory.addEntry(entryBuilder.startTextDescription(new LiteralText("Additions"))
                    .build());
            utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Current Chat"), displayChat)
                    .setDefaultValue(displayChat)
                    .setSaveConsumer(newValue -> displayChat = newValue)
                    .build());
            utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Barracks Training Time"), barracksTime)
                    .setDefaultValue(barracksTime)
                    .setSaveConsumer(newValue -> barracksTime = newValue)
                    .build());
            utilityCategory.addEntry(entryBuilder.startTextDescription(new LiteralText("Automatic"))
                    .build());
            utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Auto Warn Char Spam"), autoCharSpam)
                    .setDefaultValue(autoCharSpam)
                    .setSaveConsumer(newValue -> autoCharSpam = newValue)
                    .build());
            for (String s : spammers.keySet()) {
                List<AbstractConfigListEntry> categories = new ArrayList<>();
                List<String> messages = spammers.get(s);
                for (String message : messages) {
                    categories.add(entryBuilder.startTextDescription(new LiteralText(message)).build());
                }
                spamCategory.addEntry(entryBuilder.startSubCategory(new LiteralText(s + ": " + messages.size()), categories).build());
            }
        }
        builder.setSavingRunnable(() -> {
            outer:
            for (int i = 0; i < 24; i++) {
                if (!entries.get(i).get(0).isEdited()) continue;
                int finalI = i;
                InputUtil.Key key = ((KeyCodeEntry) entries.get(i).get(0)).getValue().getKeyCode();
                for (KeyBinding bind : client.options.keysAll) {
                    assert client.player != null;
                    if (bind.matchesKey(key.getCode(), key.getCode())) {

                        client.player.sendMessage(new LiteralText(
                                "MultiTweaks: Did not register Bind " + (i + 1) + " (" + key.getLocalizedText().getString() + ") <- Conflicting Keybind, Please Re-Bind"), false);
                        continue outer;
                    }
                }
                if (textBinds[i] != null) {
                    keyCounts[i] = keyCounts[i] + 24;
                }
                textBinds[i] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                        Integer.toString(keyCounts[i]),
                        InputUtil.Type.KEYSYM,
                        key.getCode(),
                        "category.multitweaks.gui"
                ));
                ClientTickEvents.END_CLIENT_TICK.register(client -> {
                    while (textBinds[finalI].wasPressed()) {
                        String text = keybindSettings[finalI].phrase;
                        assert client.player != null;
                        client.player.sendChatMessage(text);
                    }
                });
            }
            assert client.world != null;
            try {
                MultiTweaksClient.saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        builder.setTransparentBackground(true);
        return builder;
    }
}
