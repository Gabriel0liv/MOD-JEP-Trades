package com.gabrieloliv.jevtrades.trade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record VillagerTradeEntry(
        VillagerProfession profession,
        int level,
        List<ItemStack> inputAOptions,
        List<ItemStack> inputBOptions,
        List<ItemStack> outputOptions
) {
    public VillagerTradeEntry {
        inputAOptions = List.copyOf(normalizeOptions(inputAOptions, true));
        inputBOptions = List.copyOf(normalizeOptions(inputBOptions, false));
        outputOptions = List.copyOf(normalizeOptions(outputOptions, true));
    }

    public ItemStack inputA() {
        return firstOrEmpty(inputAOptions);
    }

    public ItemStack inputB() {
        return firstOrEmpty(inputBOptions);
    }

    public ItemStack output() {
        return firstOrEmpty(outputOptions);
    }

    private static ItemStack firstOrEmpty(List<ItemStack> stacks) {
        return stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(0);
    }

    private static List<ItemStack> normalizeOptions(List<ItemStack> options, boolean required) {
        Map<String, ItemStack> unique = new LinkedHashMap<>();
        if (options != null) {
            for (ItemStack stack : options) {
                ItemStack normalized = normalize(stack);
                if (!normalized.isEmpty()) {
                    unique.putIfAbsent(stackKey(normalized), normalized);
                }
            }
        }

        if (required && unique.isEmpty()) {
            throw new IllegalArgumentException("Trade options cannot be empty");
        }

        return new ArrayList<>(unique.values());
    }

    private static ItemStack normalize(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack copy = stack.copy();
        copy.setCount(1);
        return copy;
    }

    private static String stackKey(ItemStack stack) {
        if (stack.isEmpty()) {
            return "empty";
        }

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        String tag = stack.hasTag() && stack.getTag() != null ? stack.getTag().toString() : "";
        return (itemId != null ? itemId.toString() : "unknown") + "|" + tag;
    }
}
