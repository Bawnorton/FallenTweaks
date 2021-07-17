package com.bawnorton.multitweaks;

import com.bawnorton.multitweaks.config.KeybindSettings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

import static com.bawnorton.multitweaks.MultiTweaksClient.textBinds;
import static com.bawnorton.multitweaks.config.BuildConfig.keyCounts;

public class MultiTweaks implements ModInitializer {
    public static final String NAME = "Multiplayer Tweaks";
    public static final String MOD_ID = "multitweaks";
    public static KeybindSettings[] keybindSettings = new KeybindSettings[24];
    public static String currentChat = "";

    @Override
    public void onInitialize() {
        System.out.println("Loading Multiplayer Tweaks Mod");
        File settingsFile = new File("config", "multitweaks.json");
        JsonObject jsonObject = null;
        try {
            JsonParser reader = new JsonParser();
            JsonElement element = reader.parse(new FileReader(settingsFile));
            if(!element.isJsonNull()) {
                jsonObject = (JsonObject) element;
            }

        } catch (FileNotFoundException e) {
            try {
                new FileWriter(settingsFile.getPath());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        if(jsonObject != null && !jsonObject.isJsonNull()) {
            Iterator<Map.Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
            int i = 0;
            while(iterator.hasNext()) {
                JsonArray jsonArray = iterator.next().getValue().getAsJsonArray();
                keybindSettings[i] = new KeybindSettings(
                        InputUtil.fromTranslationKey(jsonArray.get(0).getAsString()), jsonArray.get(1).getAsString()
                );
                if (textBinds[i] != null) {
                    keyCounts[i] = keyCounts[i] + 24;
                }
                textBinds[i] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                        Integer.toString(keyCounts[i]),
                        InputUtil.Type.KEYSYM,
                        keybindSettings[i].key.getCode(),
                        "category.multitweaks.gui"
                ));
                int finalI = i;
                ClientTickEvents.END_CLIENT_TICK.register(client -> {
                    while(textBinds[finalI].wasPressed()) {
                        String text = keybindSettings[finalI].phrase;
                        assert MinecraftClient.getInstance().player != null;
                        MinecraftClient.getInstance().player.sendChatMessage(text);
                    }
                });
                i++;
            }
        }
        else {
            for(int i = 0; i < 24; i++) {
                keybindSettings[i] = new KeybindSettings(InputUtil.UNKNOWN_KEY, "");
            }
        }
    }
}
