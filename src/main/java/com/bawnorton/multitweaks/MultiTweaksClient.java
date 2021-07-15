package com.bawnorton.multitweaks;

import com.bawnorton.multitweaks.gui.Gui;
import com.bawnorton.multitweaks.gui.MScreen;
import com.bawnorton.multitweaks.gui.widgets.MWTextField;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;


public class MultiTweaksClient implements ClientModInitializer {
    private static KeyBinding menuKeybind;
    private static KeyBinding scoreboardKeybind;
    public static KeyBinding[] binds = new KeyBinding[24];
    public static MWTextField[] paragraphs = new MWTextField[24];
    public static boolean showScoreboard = true;

    public void onInitializeClient() {
        for(int i = 0; i < 24; i++) {
            binds[i] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.multitweaks.binds." + i,
                    InputUtil.Type.KEYSYM,
                    -1,
                    "category.multitweaks.gui"
            ));
            int finalI = i;
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                while(binds[finalI].wasPressed()) {
                    if(paragraphs[finalI] == null) return;
                    String text = paragraphs[finalI].getText();
                    if( text.isEmpty()) return;
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;
                    assert player != null;
                    player.sendChatMessage(text);
                }
            });
        }
        menuKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.multitweaks.gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.multitweaks.gui"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while(menuKeybind.wasPressed()) {
                MinecraftClient.getInstance().openScreen(new MScreen(new Gui()));
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
