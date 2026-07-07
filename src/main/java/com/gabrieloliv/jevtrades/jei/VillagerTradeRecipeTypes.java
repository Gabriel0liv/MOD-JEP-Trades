package com.gabrieloliv.jevtrades.jei;

import com.gabrieloliv.jevtrades.Constants;
import com.gabrieloliv.jevtrades.trade.VillagerTradeWrapper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class VillagerTradeRecipeTypes {
    private static final Map<ResourceLocation, RecipeType<VillagerTradeWrapper>> BY_PROFESSION = new LinkedHashMap<>();

    private VillagerTradeRecipeTypes() {
    }

    public static RecipeType<VillagerTradeWrapper> getOrCreate(ResourceLocation professionId) {
        return BY_PROFESSION.computeIfAbsent(professionId, id ->
                RecipeType.create(Constants.MOD_ID,
                        "trades/profession/" + id.getNamespace() + "/" + id.getPath(),
                        VillagerTradeWrapper.class));
    }

    public static Collection<RecipeType<VillagerTradeWrapper>> getAll() {
        return BY_PROFESSION.values();
    }
}
