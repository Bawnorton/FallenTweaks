package com.bawnorton.multitweaks;

import com.bawnorton.multitweaks.config.BuildConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

public class MultiTweaksClient implements ClientModInitializer {
    public static KeyBinding menuKeybind;
    public static KeyBinding scoreboardKeybind;
    public static KeyBinding[] textBinds = new KeyBinding[24];
    public static boolean showScoreboard = true;

    public void onInitializeClient() {
        menuKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.multitweaks.gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.multitweaks.gui"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while(menuKeybind.wasPressed()) {
                Screen screen = BuildConfig.buildScreen("title.multitweaks.config", MinecraftClient.getInstance().currentScreen).build();
                MinecraftClient.getInstance().openScreen(screen);
            }
        });
        scoreboardKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.multitweaks.toggle.scoreboard",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.multitweaks.gui"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while(scoreboardKeybind.wasPressed()) {
                assert MinecraftClient.getInstance().player != null;
                MinecraftClient.getInstance().player.sendMessage(new TranslatableText("Scoreboard was " + (showScoreboard ? "Disabled" : "Enabled")), true);
                showScoreboard = !showScoreboard;
            }
        });
    }
}
