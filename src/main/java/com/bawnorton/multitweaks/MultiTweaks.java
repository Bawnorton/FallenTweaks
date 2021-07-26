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
import net.minecraft.server.MinecraftServer;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

import static com.bawnorton.multitweaks.Global.*;

public class MultiTweaks implements ModInitializer {

    @Override
    public void onInitialize() {
        System.out.println("Loading Multiplayer Tweaks Mod");
    }
}
