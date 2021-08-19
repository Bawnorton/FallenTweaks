package com.bawnorton.fallentweaks.skin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.util.Session;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.bawnorton.fallentweaks.Global.client;

public class SkinManager {
    private static final Session session = client.getSession();

    public static void saveSession() {
        File settingsFile = new File("config", "session.json");
        try {
            FileWriter file = new FileWriter(settingsFile);
            JsonObject json = new JsonObject();
            Gson gson = new Gson();
            json.add("token", gson.toJsonTree(session.getAccessToken()));
            file.write(json.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
