package com.bawnorton.multitweaks.config;

import com.bawnorton.multitweaks.config.entry.PlayerPreview;
import com.bawnorton.multitweaks.skin.SkinManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bawnorton.multitweaks.Global.*;
import static com.bawnorton.multitweaks.skin.SkinManager.sendSkin;

public class MultiTweaksConfig {

    public static IntegerSliderEntry lenienceSlider;
    public static IntegerSliderEntry redSlider;
    public static IntegerSliderEntry greenSlider;
    public static IntegerSliderEntry blueSlider;
    public static IntegerSliderEntry hueSlider;

    public static ConfigBuilder buildScreen(String name, Screen parent) {
        builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new TranslatableText(name));
        ConfigCategory keybindCategory = builder.getOrCreateCategory(new TranslatableText("category.multitweaks.keybind"));
        ConfigCategory skinCategory = builder.getOrCreateCategory(new TranslatableText("category.multitweaks.skin"));
        ConfigCategory soundCategory = builder.getOrCreateCategory(new TranslatableText("category.multitweaks.sound"));
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
        SkinManager.getSkin();
        PlayerPreview preview = new PlayerPreview();
        skinCategory.addEntry(preview);
        lenienceSlider = entryBuilder.startIntSlider(new LiteralText(""), 0, 0, 255)
                .setDefaultValue(0)
                .setSaveConsumer(newValue -> lenience = newValue)
                .setTextGetter((integer) -> new LiteralText(String.format("Lenience: %d", integer)))
                .build();
        redSlider = entryBuilder.startIntSlider(new LiteralText(""), 0, 0, 255)
                .setDefaultValue(0)
                .setSaveConsumer(newValue -> selectionSlider[0] = newValue)
                .setTextGetter((integer) -> new LiteralText(String.format("Red: %d", integer)))
                .build();
        blueSlider = entryBuilder.startIntSlider(new LiteralText(""), 0, 0, 255)
                .setDefaultValue(0)
                .setSaveConsumer(newValue -> selectionSlider[1] = newValue)
                .setTextGetter((integer) -> new LiteralText(String.format("Green: %d", integer)))
                .build();
        greenSlider = entryBuilder.startIntSlider(new LiteralText(""), 0, 0, 255)
                .setDefaultValue(0)
                .setSaveConsumer(newValue -> selectionSlider[2] = newValue)
                .setTextGetter((integer) -> new LiteralText(String.format("Blue: %d", integer)))
                .build();
        hueSlider = entryBuilder.startIntSlider(new LiteralText(""), 0, 0, 3600)
                .setDefaultValue(0)
                .setSaveConsumer(newValue -> alteredHue = newValue)
                .setTextGetter((integer) -> new LiteralText("HUE"))
                .build();
        skinCategory.addEntry(lenienceSlider);
        skinCategory.addEntry(redSlider);
        skinCategory.addEntry(blueSlider);
        skinCategory.addEntry(greenSlider);
        skinCategory.addEntry(hueSlider);
        soundCategory.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Kingdom Chat"), kingdomDing)
                .setDefaultValue(kingdomDing)
                .setSaveConsumer(newValue -> kingdomDing = newValue)
                .build());
        soundCategory.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Visit Chat"), visitDing)
                .setDefaultValue(visitDing)
                .setSaveConsumer(newValue -> visitDing = newValue)
                .build());
        soundCategory.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Helper Chat"), helperDing)
                .setDefaultValue(helperDing)
                .setSaveConsumer(newValue -> helperDing = newValue)
                .build());
        soundCategory.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Direct Messages"), messageDing)
                .setDefaultValue(messageDing)
                .setSaveConsumer(newValue -> messageDing = newValue)
                .build());
        soundCategory.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Question Words"), questionDing)
                .setDefaultValue(questionDing)
                .setSaveConsumer(newValue -> questionDing = newValue)
                .build());
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
                json.add("keybinds", keybindJson);
                json.add("sounds", booleanJson);
                file.write(json.toString());
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FileUtils.copyFile(skinPNGFile, storedFile);
            } catch (NullPointerException | IOException e) {
                System.out.println("Cannot Save Skin, No Skin File");
            }
            sendSkin();
        });
        builder.setDefaultBackgroundTexture(new Identifier("multitweaks:textures/gui_background.png"));
//        builder.setDefaultBackgroundTexture(new Identifier("multitweaks:textures/rick.png"));
        builder.setTransparentBackground(true);
        return builder;
    }
}
