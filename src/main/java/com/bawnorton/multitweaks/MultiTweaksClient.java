package com.bawnorton.multitweaks;

import com.bawnorton.multitweaks.config.MultiTweaksConfig;
import com.bawnorton.multitweaks.skin.SkinManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

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
    }
}
