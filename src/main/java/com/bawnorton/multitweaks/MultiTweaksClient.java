package com.bawnorton.multitweaks;

import com.bawnorton.multitweaks.config.MultiTweaksConfig;
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
    }
}
