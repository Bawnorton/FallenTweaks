package com.bawnorton.multitweaks.gui;

import com.google.gson.JsonObject;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.client.MinecraftClient;

import java.io.FileWriter;
import java.io.IOException;

import static com.bawnorton.multitweaks.MultiTweaksClient.paragraphs;

public class MScreen extends CottonClientScreen {
    public MScreen(GuiDescription description) {
        super(description);
    }

    @Override
    public void onClose() {
        try {
            openFile(MinecraftClient.getInstance().runDirectory.getCanonicalPath() + "/multitweaks.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onClose();
    }

    private void openFile(String path) throws IOException {
        FileWriter file = new FileWriter(path);
        for(int i = 0; i < paragraphs.length; i++) {
            if (paragraphs[i] == null || paragraphs[i].getText().isEmpty()) continue;
            JsonObject json = new JsonObject();
            json.addProperty(Integer.toString(i), paragraphs[i].getText());
            file.write(json.toString());
        }
    }
}
