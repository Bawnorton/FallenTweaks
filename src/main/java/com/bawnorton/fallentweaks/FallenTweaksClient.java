package com.bawnorton.fallentweaks;

import com.bawnorton.fallentweaks.config.FallenTweaksConfig;
import com.bawnorton.fallentweaks.config.KeybindSettings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.bawnorton.fallentweaks.Global.*;

public class FallenTweaksClient implements ClientModInitializer {

    public static void saveConfig() throws IOException {
        File settingsFile = new File("config", "multitweaks.json");
        JsonObject jsonObject;
        try {
            jsonObject = new JsonParser().parse(new FileReader(settingsFile)).getAsJsonObject();
        } catch (IllegalStateException e) {
            jsonObject = new JsonParser().parse("{}").getAsJsonObject();
        }
        Set<Map.Entry<String, JsonElement>> jsonEntries = jsonObject.entrySet();
        FileWriter file = new FileWriter(settingsFile);
        JsonObject keybindJson = new JsonObject();
        JsonObject serverJson = new JsonObject();
        Gson gson = new Gson();
        for (int i = 0; i < keybindSettings.length; i++) {
            if (keybindSettings[i] == null) {
                keybindSettings[i] = new KeybindSettings(InputUtil.UNKNOWN_KEY, "");
            }
            JsonElement jsonElement = gson.toJsonTree(new String[]{keybindSettings[i].key.getTranslationKey(), keybindSettings[i].phrase});
            keybindJson.add(Integer.toString(i), jsonElement);
        }
        serverJson.add("keybinds", keybindJson);
        if (ipAddress.contains("fallenkingdom") || inDev) {
            JsonObject booleanJson = new JsonObject();
            booleanJson.add("helperchat", gson.toJsonTree(helperDing));
            booleanJson.add("kingdomchat", gson.toJsonTree(kingdomDing));
            booleanJson.add("visitchat", gson.toJsonTree(visitDing));
            booleanJson.add("staffchat", gson.toJsonTree(staffDing));
            booleanJson.add("messagechat", gson.toJsonTree(messageDing));
            booleanJson.add("question", gson.toJsonTree(questionDing));
            booleanJson.add("charspam", gson.toJsonTree(autoCharSpam));
            booleanJson.add("farm", gson.toJsonTree(farmDing));
            booleanJson.add("barracks", gson.toJsonTree(barracksDing));
            booleanJson.add("blacksmith", gson.toJsonTree(blacksmithDing));
            booleanJson.add("betterbank", gson.toJsonTree(betterBank));
            booleanJson.add("bettertroops", gson.toJsonTree(betterTroops));
            booleanJson.add("betterfarm", gson.toJsonTree(betterFarm));
            booleanJson.add("betterraid", gson.toJsonTree(betterRaid));
            booleanJson.add("bettercombat", gson.toJsonTree(betterCombat));
            booleanJson.add("displaychat", gson.toJsonTree(displayChat));
            booleanJson.add("barracksscoreboard", gson.toJsonTree(barracksTime));
            booleanJson.add("savedrank", gson.toJsonTree(savedRank));
            booleanJson.add("leavevisitors", gson.toJsonTree(leaveVisitors));

            JsonObject spammerJson = new JsonObject();
            for (String s : spammers.keySet()) {
                spammerJson.add(s, gson.toJsonTree(spammers.get(s)).getAsJsonArray());
            }
            serverJson.add("utility", booleanJson);
            serverJson.add("spammers", spammerJson);
        }
        JsonObject json = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonEntries) {
            json.add(entry.getKey(), entry.getValue());
        }
        json.add(ipAddress, serverJson);
        file.write(json.toString());
        file.close();
    }

    public void onInitializeClient() {
        registerKeybinds();
    }

    private void registerKeybinds() {
        menuKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fallentweaks.gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.fallentweaks.gui"
        ));
        scoreboardKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fallentweaks.toggle.scoreboard",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.fallentweaks.gui"
        ));
        gammaKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fallentweaks.toggle.gamma",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.fallentweaks.gui"
        ));
        hatKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fallentweaks.toggle.hat",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.fallentweaks.gui"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKeybind.wasPressed()) {
                Screen screen = FallenTweaksConfig.buildScreen("title.fallentweaks.config", client.currentScreen).build();
                client.openScreen(screen);
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (scoreboardKeybind.wasPressed()) {
                assert client.player != null;
                client.player.sendMessage(new TranslatableText("Scoreboard was " + (showScoreboard ? "Disabled" : "Enabled")), true);
                showScoreboard = !showScoreboard;
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (gammaKeybind.wasPressed()) {
                assert client.player != null;
                client.player.sendMessage(new TranslatableText("Gamma was " + (client.options.gamma > 1 ? "Disabled" : "Enabled")), true);
                if (client.options.gamma > 1) client.options.gamma -= 10;
                else client.options.gamma += 10;
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (hatKeybind.wasPressed()) {
                cycleHat += 1;
                if (cycleHat == 4) cycleHat = 0;
            }
        });
    }
    public static boolean isStaff() {
        assert client.player != null;
        String playerName = client.player.getDisplayName().getString();
        String[] staffTitles = {"HELPER","JR MOD","MOD","SR MOD","MANAGER","DEV","OWNER"};
        for(String title: staffTitles) {
            if(playerName.startsWith(title)) {
                return true;
            }
        }
        return false;
    }
}
