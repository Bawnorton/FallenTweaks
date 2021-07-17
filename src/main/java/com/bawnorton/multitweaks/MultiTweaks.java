package com.bawnorton.multitweaks;

import com.bawnorton.multitweaks.config.KeybindSettings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.util.InputUtil;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

public class MultiTweaks implements ModInitializer {
    public static final String NAME = "Multiplayer Tweaks";
    public static final String MOD_ID = "multitweaks";
    public static KeybindSettings[] keybindSettings = new KeybindSettings[24];

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
