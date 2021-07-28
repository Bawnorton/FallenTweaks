package com.bawnorton.multitweaks.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.bawnorton.multitweaks.Global.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Shadow public abstract void equipStack(EquipmentSlot slot, ItemStack stack);

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow public abstract Text getDisplayName();

    @Inject(method = "tick", at = @At("HEAD"))
    public void renderCrown(CallbackInfo ci) {
        if(ipAddress.contains("fallenkingdom")) {
            if(this.getDisplayName().getString().contains("curmor") && !client.player.getDisplayName().getString().contains("curmor")) {
                if(this.getEquippedStack(EquipmentSlot.HEAD) == ItemStack.EMPTY) {
                    ItemStack pickaxeStack = new ItemStack(Items.GOLDEN_PICKAXE);
                    CompoundTag tag = new CompoundTag();
                    tag.putInt("CustomModelData", 3);
                    pickaxeStack.setTag(tag);
                    this.equipStack(EquipmentSlot.HEAD, pickaxeStack);
                }
                if(this.getEquippedStack(EquipmentSlot.CHEST) == ItemStack.EMPTY) {
                    ItemStack itemStack = new ItemStack(Items.NETHERITE_CHESTPLATE);
                    this.equipStack(EquipmentSlot.CHEST, itemStack);
                }
                if(this.getEquippedStack(EquipmentSlot.LEGS) == ItemStack.EMPTY) {
                    ItemStack itemStack = new ItemStack(Items.NETHERITE_LEGGINGS);
                    this.equipStack(EquipmentSlot.LEGS, itemStack);
                }
                if(this.getEquippedStack(EquipmentSlot.FEET) == ItemStack.EMPTY) {
                    ItemStack itemStack = new ItemStack(Items.NETHERITE_BOOTS);
                    this.equipStack(EquipmentSlot.FEET, itemStack);
                }
                if(this.getEquippedStack(EquipmentSlot.MAINHAND) == ItemStack.EMPTY) {
                    ItemStack itemStack = new ItemStack(Items.NETHERITE_SWORD);
                    this.equipStack(EquipmentSlot.MAINHAND, itemStack);
                }
            } else if (this.getDisplayName().getString().contains(client.player.getDisplayName().getString())) {
                if(cycleHat != 0) {
                    if(this.getEquippedStack(EquipmentSlot.HEAD) == ItemStack.EMPTY || persistentHat != cycleHat) {
                        ItemStack pickaxeStack = new ItemStack(Items.GOLDEN_PICKAXE);
                        CompoundTag tag = new CompoundTag();
                        tag.putInt("CustomModelData", cycleHat);
                        persistentHat = cycleHat;
                        pickaxeStack.setTag(tag);
                        this.equipStack(EquipmentSlot.HEAD, pickaxeStack);
                    }
                } else {
                    if(this.getEquippedStack(EquipmentSlot.HEAD) != ItemStack.EMPTY) {
                        this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
}
