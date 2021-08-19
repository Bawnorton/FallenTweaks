package com.bawnorton.fallentweaks.mixin;

import com.bawnorton.fallentweaks.config.KeybindSettings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.bawnorton.fallentweaks.Global.*;

@Mixin(ConnectScreen.class)
public abstract class ConnectScreenMixin {
    private static void loadConfig(String ipAddress) {
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
            JsonObject serverJson;
            JsonObject keybindJson;
            try {
                serverJson = jsonObject.get(ipAddress).getAsJsonObject();
            } catch (NullPointerException e) {
                serverJson = new JsonObject();
                serverJson.add(ipAddress, null);
            }
            try {
                keybindJson = serverJson.get("keybinds").getAsJsonObject();
            } catch (NullPointerException e) {
                keybindJson = new JsonObject();
                for (int i = 0; i < 24; i++) {
                    keybindSettings[i] = new KeybindSettings(InputUtil.UNKNOWN_KEY, "");
                }
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
                        "category.fallentweaks.gui"
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
                JsonObject booleanJson = serverJson.get("utility").getAsJsonObject();
                helperDing = booleanJson.get("helperchat").getAsBoolean();
                kingdomDing = booleanJson.get("kingdomchat").getAsBoolean();
                visitDing = booleanJson.get("visitchat").getAsBoolean();
                staffDing = booleanJson.get("staffchat").getAsBoolean();
                messageDing = booleanJson.get("messagechat").getAsBoolean();
                questionDing = booleanJson.get("question").getAsBoolean();
                autoCharSpam = booleanJson.get("charspam").getAsBoolean();
                farmDing = booleanJson.get("farm").getAsBoolean();
                barracksDing = booleanJson.get("barracks").getAsBoolean();
                blacksmithDing = booleanJson.get("blacksmith").getAsBoolean();
                betterBank = booleanJson.get("betterbank").getAsBoolean();
                betterTroops = booleanJson.get("bettertroops").getAsBoolean();
                betterFarm = booleanJson.get("betterfarm").getAsBoolean();
                betterRaid = booleanJson.get("betterraid").getAsBoolean();
                betterCombat = booleanJson.get("bettercombat").getAsBoolean();
                displayChat = booleanJson.get("displaychat").getAsBoolean();
                barracksTime = booleanJson.get("barracksscoreboard").getAsBoolean();
                savedRank = booleanJson.get("savedrank").getAsString();
                leaveVisitors = booleanJson.get("leavevisitors").getAsBoolean();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            try {
                JsonObject spammerJson = serverJson.get("spammers").getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : spammerJson.entrySet()) {
                    JsonArray jsonMessages = entry.getValue().getAsJsonArray();
                    List<String> messages = new ArrayList<>();
                    for (JsonElement element : jsonMessages) {
                        messages.add(element.getAsString());
                    }
                    spammers.put(entry.getKey(), messages);
                }
            } catch (NullPointerException ignored) {
            }
        } else {
            for (int i = 0; i < 24; i++) {
                keybindSettings[i] = new KeybindSettings(InputUtil.UNKNOWN_KEY, "");
            }
        }
    }

    @Inject(method = "connect", at = @At("HEAD"))
    public void saveAddress(final String address, final int port, CallbackInfo ci) {
        try {
            loadConfig(address);
        } catch (NullPointerException ignore) {

        }
        ipAddress = address;
    }
}
