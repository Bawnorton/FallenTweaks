package com.bawnorton.fallentweaks.config;

import net.minecraft.client.util.InputUtil;

public class KeybindSettings {
    public InputUtil.Key key;
    public String phrase;

    public KeybindSettings(InputUtil.Key key, String phrase) {
        this.key = key;
        this.phrase = phrase;
    }
}
