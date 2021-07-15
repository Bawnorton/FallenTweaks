package com.bawnorton.multitweaks.gui;

import com.bawnorton.multitweaks.gui.widgets.MWTabPanel;
import com.bawnorton.multitweaks.gui.widgets.MWTextField;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static com.bawnorton.multitweaks.MultiTweaksClient.binds;
import static com.bawnorton.multitweaks.MultiTweaksClient.paragraphs;


public class Gui extends LightweightGuiDescription {
    public Gui() {
        JsonObject jsonObject = null;
        try {
            jsonObject = openFile(MinecraftClient.getInstance().runDirectory.getCanonicalPath() + "/multitweaks.json");
        } catch (IOException e) {
            try {
                new FileWriter(MinecraftClient.getInstance().runDirectory.getCanonicalPath() + "/multitweaks.json");
                jsonObject = openFile(MinecraftClient.getInstance().runDirectory.getCanonicalPath() + "/multitweaks.json");
            } catch (IOException f) {
                f.printStackTrace();
            }
        }
        WGridPanel root = new WGridPanel(1);
        root.setSize(265, 200);

        MWTabPanel tabs = new MWTabPanel();
        for(int i = 0; i < 4; i++) {
            WGridPanel panel = new WGridPanel(1);
            for(int j = 0; j < 6; j++) {
                WLabel bind = new WLabel(binds[i * 6 + j].getBoundKeyLocalizedText());
                bind.setVerticalAlignment(VerticalAlignment.CENTER);
                bind.setHorizontalAlignment(HorizontalAlignment.LEFT);
                panel.add(bind, 0, 25*j, 60, 20);
                MWTextField paragraph;
                if(paragraphs[i * 6 + j] == null || paragraphs[i * 6 + j].getText().isEmpty()) {
                    paragraph = new MWTextField(new TranslatableText("gui.multitweaks.text." + ((i * 6) + j)));
                    paragraph.setMaxLength(256);
                    if(jsonObject != null) {
                        if(jsonObject.has(Integer.toString(i * 6 + j))) {
                            String text = jsonObject.get(Integer.toString(i * 6 + j)).toString();
                            text = text.substring(1, text.length() - 1);
                            paragraph.setText(text);
                        }
                    }
                }
                else {
                    paragraph = paragraphs[i * 6 + j];
                }
                paragraphs[i * 6 + j] = paragraph;
                panel.add(paragraph, 65, 25*j, 185, 20);
            }
            int finalI = i;
            tabs.add(panel, tab -> tab.title(new TranslatableText("gui.multitweaks.tab" + finalI)));
        }
        root.add(tabs, 0, 0);

        setRootPanel(root);
        root.validate(this);
    }

    public JsonObject openFile(String path) throws FileNotFoundException {
        FileReader jsonFile = new FileReader(path);
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonFile);
        if(!element.isJsonNull()) {
            return (JsonObject) element;
        }
        return new JsonObject();
    }
}