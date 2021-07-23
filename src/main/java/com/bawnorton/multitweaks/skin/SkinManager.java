package com.bawnorton.multitweaks.skin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.util.Session;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.bawnorton.multitweaks.Global.*;

public class SkinManager {
    private static final Session session = client.getSession();
    private static final String token = session.getAccessToken();
    public static String uuid = session.getUuid();
    public static String skinURL;
    public static Identifier skinIdentifier;

    public static void getSkin() {
        if(skinURL == null) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replaceAll("-",""));
                HttpResponse response = httpClient.execute(httpGet);
                JsonObject userJson = new JsonParser().parse(EntityUtils.toString(response.getEntity())).getAsJsonObject();
                JsonArray userProperties = userJson.getAsJsonArray("properties");
                String encodedTexture = userProperties.get(0).getAsJsonObject().get("value").getAsString();
                String decodedTexuture = new String(Base64.getDecoder().decode(encodedTexture));
                JsonObject skinJson = new JsonParser().parse(decodedTexuture).getAsJsonObject();
                JsonObject textureJson = skinJson.getAsJsonObject("textures");
                JsonObject skinObject = textureJson.getAsJsonObject("SKIN");
                skinURL = skinObject.get("url").getAsString();
            } catch (IOException | IllegalArgumentException e) {
                System.out.println("Cannot Get Skin, Invalid Session");
                return;
            }
        }
        if(skinIdentifier == null || storedFile == null) {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("model", "default");
            MinecraftProfileTexture profileTexture = new MinecraftProfileTexture(skinURL, metadata);
            skinIdentifier = client.getSkinProvider().loadSkin(profileTexture, MinecraftProfileTexture.Type.SKIN);
            String skinPath = skinIdentifier.getPath();
            skinPath = skinPath.substring(0, skinPath.indexOf("/")) + skinPath.substring(skinPath.indexOf("/"), skinPath.indexOf("/") + 3) + "/" + skinPath.substring(skinPath.indexOf("/") + 1);
            skinPNGFile = new File(assetDirectory.getAbsolutePath() + "/" + skinPath);
            storedFile = new File("config", "skin.png");
            try {
                System.out.println("Copied " + skinPNGFile.getAbsolutePath() + " -> " + storedFile);
                FileUtils.copyFile(skinPNGFile, storedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void sendSkin() {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut("https://api.mojang.com/user/profile/"+uuid+"/skin");
        try {
            httpClient.execute(httpPut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
