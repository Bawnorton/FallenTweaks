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
import org.lwjgl.glfw.GLFW;


public class MultiTweaksClient implements ClientModInitializer {
    private static KeyBinding keyBinding;
    public static KeyBinding[] binds = new KeyBinding[24];
    public static MWTextField[] paragraphs = new MWTextField[24];

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
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.multitweaks.gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.multitweaks.gui"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while(keyBinding.wasPressed()) {
                MinecraftClient.getInstance().openScreen(new MScreen(new Gui()));
            }
        });
    }
}
