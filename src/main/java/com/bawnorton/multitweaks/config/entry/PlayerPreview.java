package com.bawnorton.multitweaks.config.entry;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Quaternion;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.bawnorton.multitweaks.Global.*;
import static com.bawnorton.multitweaks.config.MultiTweaksConfig.*;
import static com.bawnorton.multitweaks.skin.SkinManager.skinIdentifier;
import static com.bawnorton.multitweaks.skin.SkinManager.skinURL;

public class PlayerPreview extends AbstractConfigListEntry<Object> {
    public PlayerPreview() {
        super(new LiteralText(""), false);
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public Optional<Object> getDefaultValue() {
        return Optional.empty();
    }

    @Override
    public void save() {

    }

    @Override
    public List<? extends Element> children() {
        return new ArrayList<>();
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        alteredHue = hueSlider.getValue();
        lenience = lenienceSlider.getValue();
        selectionSlider[0] = redSlider.getValue();
        selectionSlider[1] = blueSlider.getValue();
        selectionSlider[2] = greenSlider.getValue();
        try {
            skinPng = ImageIO.read(storedFile);
            if(skinPng == null) {
                return;
            }
        } catch (IOException | IllegalArgumentException e) {
            return;
        }
        saveImage(colorImage(skinPng));
        LivingEntity playerEntity = client.player;
        renderSetUUID = true;
        if(playerEntity == null)return;
        float timestep = System.currentTimeMillis()%8000;
        timestep /= 8000F;
        timestep *= 2*Math.PI;
        timestep = (float) Math.sin(timestep);
        int height = client.getWindow().getHeight();
        int width = client.getWindow().getHeight();
        drawEntity(width / 8, (int)((double)(height) / 2.5), (int)((double)(height) / 6.25), timestep * 80f, playerEntity);
    }

    private static void drawEntity(int x, int y, int size, float mouseX, LivingEntity entity) {
        float f = (float)Math.atan(mouseX / 40.0F);
        float g = (float)Math.atan((float) 10 / 40.0F);
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)x, (float)y, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale((float)size, (float)size, (float)size);
        Quaternion quaternion = Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vector3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack.multiply(quaternion);
        float h = entity.bodyYaw;
        float i = entity.yaw;
        float j = entity.pitch;
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 180.0F + f * 20.0F;
        entity.yaw = 180.0F + f * 40.0F;
        entity.pitch = -g * 20.0F;
        entity.headYaw = entity.yaw;
        entity.prevHeadYaw = entity.yaw;
        EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, immediate, 15728880);
        });
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        entity.bodyYaw = h;
        entity.yaw = i;
        entity.pitch = j;
        entity.prevHeadYaw = k;
        entity.headYaw = l;
        RenderSystem.popMatrix();
    }
    private static BufferedImage colorImage(BufferedImage image) {
        if(alteredHue == 0 || lenience == 0) return image;
        Color selectedRGB = new Color(Color.HSBtoRGB(alteredHue / 3600, 1, 1));
        int width = image.getWidth();
        int height = image.getHeight();
        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                Color originalColor = new Color(image.getRGB(xx, yy), true);
                int red = originalColor.getRed();
                int blue = originalColor.getBlue();
                int green = originalColor.getGreen();
                int red2 = red + selectedRGB.getRed();
                int blue2 = blue + selectedRGB.getBlue();
                int green2 = green + selectedRGB.getGreen();
                if(red2 > 255) red2 -= 255;
                if(blue2 > 255) blue2 -= 255;
                if(green2 > 255) green2 -= 255;
                if(originalColor.getAlpha() == 255 &&
                    (red >= selectionSlider[0] - lenience && red <= selectionSlider[0] + lenience) &&
                    (blue >= selectionSlider[1] - lenience && blue <= selectionSlider[1] + lenience) &&
                    (green >= selectionSlider[2] - lenience && green <= selectionSlider[2] + lenience)) {
                    Color newColor = new Color(red2, blue2, green2);
                    image.setRGB(xx, yy, newColor.getRGB());
                }
            }
        }
        return image;
    }
    private void saveImage(BufferedImage image) {
        try {
            ImageIO.write(image, "png", skinPNGFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PlayerSkinTexture newSkin = new PlayerSkinTexture(skinPNGFile, skinURL, DefaultSkinHelper.getTexture(), false, () -> {});
        client.getTextureManager().registerTexture(skinIdentifier, newSkin);
    }
}
