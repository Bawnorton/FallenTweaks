package com.bawnorton.multitweaks;

import com.bawnorton.multitweaks.config.KeybindSettings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

import static com.bawnorton.multitweaks.Global.*;

public class MultiTweaks implements ModInitializer {

    @Override
    public void onInitialize() {
        System.out.println("Loading Multiplayer Tweaks Mod");
        File settingsFile = new File("config", "multitweaks.json");
        JsonObject jsonObject = null;
        try {
            JsonParser reader = new JsonParser();
            JsonElement element = reader.parse(new FileReader(settingsFile));
            if (!element.isJsonNull()) {
                jsonObject = (JsonObject) element;
            }

        } catch (FileNotFoundException e) {
            try {
                new FileWriter(settingsFile.getPath());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        if (jsonObject != null && !jsonObject.isJsonNull()) {
            JsonObject keybindJson;
            try {
                keybindJson = jsonObject.get("keybinds").getAsJsonObject();
            } catch (NullPointerException e) {
                keybindJson = jsonObject;
            }
            Iterator<Map.Entry<String, JsonElement>> iterator = keybindJson.entrySet().iterator();
            int i = 0;
            while (iterator.hasNext()) {
                JsonArray jsonArray = iterator.next().getValue().getAsJsonArray();
                keybindSettings[i] = new KeybindSettings(
                        InputUtil.fromTranslationKey(jsonArray.get(0).getAsString()), jsonArray.get(1).getAsString()
                );
                if (textBinds[i] != null) {
                    keyCounts[i] = keyCounts[i] + 24;
                }
                textBinds[i] = new KeyBinding(
                        "Bind " + keyCounts[i] + ":",
                        InputUtil.Type.KEYSYM,
                        keybindSettings[i].key.getCode(),
                        "category.multitweaks.gui"
                );
                int finalI = i;
                ClientTickEvents.END_CLIENT_TICK.register(client -> {
                    while (textBinds[finalI].wasPressed()) {
                        String text = keybindSettings[finalI].phrase;
                        assert client.player != null;
                        client.player.sendChatMessage(text);
                    }
                });
                i++;
            }
            try {
                JsonObject booleanJson = jsonObject.get("utility").getAsJsonObject();
                helperDing = booleanJson.get("helperchat").getAsBoolean();
                kingdomDing = booleanJson.get("kingdomchat").getAsBoolean();
                visitDing = booleanJson.get("visitchat").getAsBoolean();
                messageDing = booleanJson.get("messagechat").getAsBoolean();
                questionDing = booleanJson.get("question").getAsBoolean();
                autoCharSpam = booleanJson.get("charspam").getAsBoolean();
                farmDing = booleanJson.get("farm").getAsBoolean();
                barracksDing = booleanJson.get("barracks").getAsBoolean();
                blacksmithDing = booleanJson.get("blacksmith").getAsBoolean();
            } catch (NullPointerException e) {
                return;
            }
            try {
                JsonObject spammerJson = jsonObject.get("spammers").getAsJsonObject();
                for(Map.Entry<String, JsonElement> element: spammerJson.entrySet()) {
                    spammers.put(element.getKey(), element.getValue().getAsInt());
                }
            } catch (NullPointerException ignored) {}
        } else {
            for (int i = 0; i < 24; i++) {
                keybindSettings[i] = new KeybindSettings(InputUtil.UNKNOWN_KEY, "");
            }
        }
    }
}
