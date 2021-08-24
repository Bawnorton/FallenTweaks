package com.bawnorton.fallentweaks.mixin;

import com.bawnorton.fallentweaks.Global;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.stream.StreamSupport;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {


    @Redirect(method = "onInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;updateSlotStacks(Ljava/util/List;)V"))
    private void getContent(ScreenHandler screenHandler, List<ItemStack> itemStacks) {
        for(ItemStack itemStack: itemStacks) {
            String itemName = itemStack.getName().getString();
            if(itemName.contains(" Lvl") && !itemName.contains("Barrack")) {
                String troopName = itemName.substring(0, itemName.indexOf(" Lvl"));
                if(itemStack.hasTag()) {
                    CompoundTag compoundTag = itemStack.getTag();
                    assert compoundTag != null;
                    if(compoundTag.contains("display")) {
                        CompoundTag displayTag = (CompoundTag) compoundTag.get("display");
                        assert displayTag != null;
                        ListTag loreTag = (ListTag) displayTag.get("Lore");
                        assert loreTag != null;
                        for(Tag tag: loreTag) {
                            JsonParser reader = new JsonParser();
                            JsonObject loreLineJson = reader.parse(tag.asString()).getAsJsonObject();
                            if(loreLineJson.has("extra")) {
                                JsonArray loreLineText = loreLineJson.get("extra").getAsJsonArray();
                                for(JsonElement loreLinePart: loreLineText) {
                                    JsonObject loreLinePartJson = loreLinePart.getAsJsonObject();
                                    if(loreLinePartJson.has("text")) {
                                        String loreLinePartString = loreLinePartJson.get("text").getAsString().replaceAll(" ", "");
                                        if(loreLinePartString.contains("sec(s)")) {
                                            double trainTime = Double.parseDouble(loreLinePartString.substring(0, loreLinePartString.indexOf("sec(s)")));
                                            if(trainTime < Global.troopTimes.get(troopName)) {
                                                Global.troopTimes.replace(troopName, trainTime);
                                            }
                                        } else if (loreLinePartString.contains("min(s)")) {
                                            double trainTime = Double.parseDouble(loreLinePartString.substring(0, loreLinePartString.indexOf("min(s)"))) * 60;
                                            if(trainTime < Global.troopTimes.get(troopName)) {
                                                Global.troopTimes.replace(troopName, trainTime);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        screenHandler.updateSlotStacks(itemStacks);
    }
}
