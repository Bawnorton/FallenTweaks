package com.bawnorton.multitweaks;

import com.bawnorton.multitweaks.config.KeybindSettings;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Global {
    public static final String NAME = "Multiplayer Tweaks";
    public static final String MOD_ID = "multitweaks";
    public static final int[] keyCounts = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
    public static String currentChat = "";
    public static KeybindSettings[] keybindSettings = new KeybindSettings[24];
    public static KeyBinding[] textBinds = new KeyBinding[24];
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static KeyBinding menuKeybind;
    public static KeyBinding scoreboardKeybind;
    public static KeyBinding gammaKeybind;
    public static Map<String, Integer> spammers = new HashMap<>();
    public static boolean renderChatType = false;
    public static boolean showScoreboard = true;
    public static boolean kingdomDing = true;
    public static boolean visitDing = true;
    public static boolean helperDing = false;
    public static boolean messageDing = false;
    public static boolean questionDing = false;
    public static boolean farmDing = true;
    public static boolean barracksDing = true;
    public static boolean blacksmithDing = true;
    public static boolean autoCharSpam = false;
    public static boolean sendSpamMessage = false;
    public static String ipAddress;
    public static String incomingSound;
    public static ConfigBuilder builder;
}
