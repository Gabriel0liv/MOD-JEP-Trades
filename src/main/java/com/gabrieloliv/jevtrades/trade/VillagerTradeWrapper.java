package com.gabrieloliv.jevtrades.trade;

import com.gabrieloliv.jevtrades.jei.ProfessionTokenHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

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

    public List<ItemStack> getInputAOptions() {
        return entry.inputAOptions();
    }

    public ItemStack getInputB() {
        return entry.inputB();
    }

    public List<ItemStack> getInputBOptions() {
        return entry.inputBOptions();
    }

    public ItemStack getOutput() {
        return entry.output();
    }

    public List<ItemStack> getOutputOptions() {
        return entry.outputOptions();
    }
}
