package com.bawnorton.multitweaks;

import com.bawnorton.multitweaks.config.MultiTweaksConfig;
import com.bawnorton.multitweaks.skin.SkinManager;
import com.google.common.collect.Maps;
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
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.bawnorton.multitweaks.Global.*;

public class MultiTweaksClient implements ClientModInitializer {

    public void onInitializeClient() {
        registerKeybinds();
        SkinManager.saveSession();
    }
    private void registerKeybinds() {
        menuKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.multitweaks.gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.multitweaks.gui"
        ));
        scoreboardKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.multitweaks.toggle.scoreboard",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.multitweaks.gui"
        ));
        gammaKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.multitweaks.toggle.gamma",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.multitweaks.gui"
        ));
        hatKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.multitweaks.toggle.hat",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.multitweaks.gui"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKeybind.wasPressed()) {
                Screen screen = MultiTweaksConfig.buildScreen("title.multitweaks.config", client.currentScreen).build();
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
                if(client.options.gamma > 1) client.options.gamma -= 10;
                else client.options.gamma += 10;
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (hatKeybind.wasPressed()) {
                cycleHat += 1;
                if(cycleHat == 4) cycleHat = 0;
            }
        });
    }
    public static void saveConfig() throws IOException {
        File settingsFile = new File("config", "multitweaks.json");
        JsonObject jsonObject = new JsonParser().parse(new FileReader(settingsFile)).getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> jsonEntries = jsonObject.entrySet();
        FileWriter file = new FileWriter(settingsFile);
        JsonObject keybindJson = new JsonObject();
        JsonObject serverJson = new JsonObject();
        Gson gson = new Gson();
        for (int i = 0; i < keybindSettings.length; i++) {
            keybindJson.add(Integer.toString(i), gson.toJsonTree(new String[]{keybindSettings[i].key.getTranslationKey(), keybindSettings[i].phrase}));
        }
        serverJson.add("keybinds", keybindJson);
        if(ipAddress.contains("fallenkingdom")) {
            JsonObject booleanJson = new JsonObject();
            booleanJson.add("helperchat", gson.toJsonTree(helperDing));
            booleanJson.add("kingdomchat", gson.toJsonTree(kingdomDing));
            booleanJson.add("visitchat", gson.toJsonTree(visitDing));
            booleanJson.add("messagechat", gson.toJsonTree(messageDing));
            booleanJson.add("question", gson.toJsonTree(questionDing));
            booleanJson.add("charspam", gson.toJsonTree(autoCharSpam));
            booleanJson.add("farm", gson.toJsonTree(farmDing));
            booleanJson.add("barracks", gson.toJsonTree(barracksDing));
            booleanJson.add("blacksmith", gson.toJsonTree(blacksmithDing));
            JsonObject spammerJson = new JsonObject();
            for(String s: spammers.keySet()) {
                spammerJson.add(s, gson.toJsonTree(spammers.get(s)).getAsJsonArray());
            }
            serverJson.add("utility", booleanJson);
            serverJson.add("spammers", spammerJson);
        }
        JsonObject json = new JsonObject();
        for(Map.Entry<String, JsonElement> entry: jsonEntries) {
            json.add(entry.getKey(), entry.getValue());
        }
        json.add(ipAddress, serverJson);
        file.write(json.toString());
        file.close();
    }
}
