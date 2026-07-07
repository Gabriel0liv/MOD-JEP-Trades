package com.gabrieloliv.jevtrades.trade;

import com.gabrieloliv.jevtrades.jei.ProfessionTokenHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public record VillagerTradeWrapper(VillagerTradeEntry entry) {
    public ResourceLocation getProfessionId() {
        return ForgeRegistries.VILLAGER_PROFESSIONS.getKey(entry.profession());
    }

    public ItemStack getProfessionToken() {
        ResourceLocation id = getProfessionId();
        return id == null ? ItemStack.EMPTY : ProfessionTokenHelper.createProfessionToken(id);
    }

    public int getLevel() {
        return entry.level();
    }

    public ItemStack getInputA() {
        return entry.inputA();
    }

    public ItemStack getInputB() {
        return entry.inputB();
    }

    public ItemStack getOutput() {
        return entry.output();
    }
}
