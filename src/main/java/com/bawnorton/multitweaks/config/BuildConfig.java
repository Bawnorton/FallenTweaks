package com.bawnorton.multitweaks.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bawnorton.multitweaks.MultiTweaks.keybindSettings;
import static com.bawnorton.multitweaks.MultiTweaksClient.*;

public class BuildConfig {
    public static ConfigBuilder builder;
    public static final int[] keyCounts = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};

    public static ConfigBuilder buildScreen(String name, Screen parent) {
        builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new TranslatableText(name));
        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.multitweaks.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        List<List<AbstractConfigListEntry>> entries = new ArrayList<>();
        for(int i = 0; i < 24; i++) {
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
            general.addEntry(entryBuilder.startSubCategory(new LiteralText("Bind " + (i + 1) + ": " +
                    ((KeyCodeEntry)entries.get(i).get(0)).getValue().getKeyCode().getLocalizedText().getString() + " -> " + keybindSettings[finalI].phrase),
                    categories).build());
        }
        builder.setSavingRunnable(() -> {
            outer: for(int i = 0; i < 24; i++) {
                if(!entries.get(i).get(0).isEdited()) continue;
                int finalI = i;
                InputUtil.Key key = ((KeyCodeEntry)entries.get(i).get(0)).getValue().getKeyCode();
                if(key.getTranslationKey().equals(menuKeybind.getBoundKeyTranslationKey()) ||
                        key.getTranslationKey().equals(scoreboardKeybind.getBoundKeyTranslationKey())) {
                    assert MinecraftClient.getInstance().player != null;
                    MinecraftClient.getInstance().player.sendMessage(new LiteralText(
                            "MultiTweaks: Did not register Bind " + (i + 1) + " (" + key.getLocalizedText().getString() + ") <- Conflicting Keybind, Please Re-Bind"), false);
                    continue;
                }
                for(KeyBinding bind: MinecraftClient.getInstance().options.keysAll) {
                    assert MinecraftClient.getInstance().player != null;
                    if(bind.matchesKey(key.getCode(), key.getCode())) {

                        MinecraftClient.getInstance().player.sendMessage(new LiteralText(
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
                    while(textBinds[finalI].wasPressed()) {
                        String text = keybindSettings[finalI].phrase;
                        assert MinecraftClient.getInstance().player != null;
                        MinecraftClient.getInstance().player.sendChatMessage(text);
                    }
                });
            }
            File settingsFile = new File("config", "multitweaks.json");
            try {
                FileWriter file = new FileWriter(settingsFile);
                JsonObject json = new JsonObject();
                Gson gson = new Gson();
                for(int i = 0; i < keybindSettings.length; i++) {
                    json.add(Integer.toString(i), gson.toJsonTree(new String[]{keybindSettings[i].key.getTranslationKey(), keybindSettings[i].phrase}));
                }
                file.write(json.toString());
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return builder;
    }
}
