package com.bawnorton.multitweaks.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bawnorton.multitweaks.Global.*;

public class MultiTweaksConfig {

    public static ConfigBuilder buildScreen(String name, Screen parent) {
        builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new TranslatableText(name));
        ConfigCategory keybindCategory = builder.getOrCreateCategory(new TranslatableText("category.multitweaks.keybind"));
        ConfigCategory utilityCategory = builder.getOrCreateCategory(new TranslatableText("category.multitweaks.utility"));
        ConfigCategory spamCategory = builder.getOrCreateCategory(new TranslatableText("category.multitweaks.spam"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        List<List<AbstractConfigListEntry>> entries = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            int finalI = i;
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
        utilityCategory.addEntry(entryBuilder.startTextDescription(new LiteralText("Chat Sounds"))
                .build());
        utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Kingdom Chat"), kingdomDing)
                .setDefaultValue(kingdomDing)
                .setSaveConsumer(newValue -> kingdomDing = newValue)
                .build());
        utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Visit Chat"), visitDing)
                .setDefaultValue(visitDing)
                .setSaveConsumer(newValue -> visitDing = newValue)
                .build());
        utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Helper Chat"), helperDing)
                .setDefaultValue(helperDing)
                .setSaveConsumer(newValue -> helperDing = newValue)
                .build());
        utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Direct Messages"), messageDing)
                .setDefaultValue(messageDing)
                .setSaveConsumer(newValue -> messageDing = newValue)
                .build());
        utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Question Words"), questionDing)
                .setDefaultValue(questionDing)
                .setSaveConsumer(newValue -> questionDing = newValue)
                .build());
        utilityCategory.addEntry(entryBuilder.startTextDescription(new LiteralText("Kingdom Sounds"))
                .build());
        utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Farm"), farmDing)
                .setDefaultValue(farmDing)
                .setSaveConsumer(newValue -> farmDing = newValue)
                .build());
        utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Barracks"), barracksDing)
                .setDefaultValue(barracksDing)
                .setSaveConsumer(newValue -> barracksDing = newValue)
                .build());
        utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Blacksmith"), blacksmithDing)
                .setDefaultValue(blacksmithDing)
                .setSaveConsumer(newValue -> blacksmithDing = newValue)
                .build());
        utilityCategory.addEntry(entryBuilder.startTextDescription(new LiteralText("Automatic"))
                .build());
        utilityCategory.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Auto Warn Char Spam"), autoCharSpam)
                .setDefaultValue(autoCharSpam)
                .setSaveConsumer(newValue -> autoCharSpam = newValue)
                .build());
        for(String s: spammers.keySet()) {
            spamCategory.addEntry(entryBuilder.startTextDescription(new LiteralText(s + ": " + spammers.get(s))).build());
        }
        builder.setSavingRunnable(() -> {
            outer:
            for (int i = 0; i < 24; i++) {
                if (!entries.get(i).get(0).isEdited()) continue;
                int finalI = i;
                InputUtil.Key key = ((KeyCodeEntry) entries.get(i).get(0)).getValue().getKeyCode();
                if (key.getTranslationKey().equals(menuKeybind.getBoundKeyTranslationKey()) ||
                        key.getTranslationKey().equals(scoreboardKeybind.getBoundKeyTranslationKey())) {
                    assert client.player != null;
                    client.player.sendMessage(new LiteralText(
                            "MultiTweaks: Did not register Bind " + (i + 1) + " (" + key.getLocalizedText().getString() + ") <- Conflicting Keybind, Please Re-Bind"), false);
                    continue;
                }
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
            File settingsFile = new File("config", "multitweaks.json");
            try {
                FileWriter file = new FileWriter(settingsFile);
                JsonObject keybindJson = new JsonObject();
                JsonObject json = new JsonObject();
                Gson gson = new Gson();
                for (int i = 0; i < keybindSettings.length; i++) {
                    keybindJson.add(Integer.toString(i), gson.toJsonTree(new String[]{keybindSettings[i].key.getTranslationKey(), keybindSettings[i].phrase}));
                }
                JsonObject booleanJson = new JsonObject();
                booleanJson.add("helperchat", gson.toJsonTree(helperDing));
                booleanJson.add("kingdomchat", gson.toJsonTree(kingdomDing));
                booleanJson.add("visitchat", gson.toJsonTree(visitDing));
                booleanJson.add("messagechat", gson.toJsonTree(messageDing));
                booleanJson.add("question", gson.toJsonTree(questionDing));
                booleanJson.add("farm", gson.toJsonTree(farmDing));
                booleanJson.add("barracks", gson.toJsonTree(barracksDing));
                booleanJson.add("blacksmith", gson.toJsonTree(blacksmithDing));
                booleanJson.add("charspam", gson.toJsonTree(autoCharSpam));
                JsonObject spammerJson = new JsonObject();
                for(String s: spammers.keySet()) {
                    spammerJson.add(s, gson.toJsonTree(spammers.get(s)));
                }
                json.add("keybinds", keybindJson);
                json.add("utility", booleanJson);
                json.add("spammers", spammerJson);
                file.write(json.toString());
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        builder.setTransparentBackground(true);
        return builder;
    }
}
