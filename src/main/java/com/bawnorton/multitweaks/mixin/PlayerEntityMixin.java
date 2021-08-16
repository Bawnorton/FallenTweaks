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
    @Shadow
    public abstract void equipStack(EquipmentSlot slot, ItemStack stack);

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    public abstract Text getDisplayName();

    @Inject(method = "tick", at = @At("HEAD"))
    public void renderCrown(CallbackInfo ci) {
        if (ipAddress.contains("fallenkingdom")) {
            assert client.player != null;
            if (this.getDisplayName().getString().contains(client.player.getDisplayName().getString())) {
                if (cycleHat != 0) {
                    if (this.getEquippedStack(EquipmentSlot.HEAD) == ItemStack.EMPTY || persistentHat != cycleHat) {
                        ItemStack pickaxeStack = new ItemStack(Items.GOLDEN_PICKAXE);
                        CompoundTag tag = new CompoundTag();
                        tag.putInt("CustomModelData", cycleHat);
                        persistentHat = cycleHat;
                        pickaxeStack.setTag(tag);
                        this.equipStack(EquipmentSlot.HEAD, pickaxeStack);
                    }
                } else {
                    if (this.getEquippedStack(EquipmentSlot.HEAD).equals(new ItemStack(Items.GOLDEN_PICKAXE))) {
                        this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
}
