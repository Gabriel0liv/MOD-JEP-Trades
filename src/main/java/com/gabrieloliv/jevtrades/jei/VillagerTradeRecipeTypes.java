package com.gabrieloliv.jevtrades.jei;

import com.gabrieloliv.jevtrades.Constants;
import com.gabrieloliv.jevtrades.trade.VillagerTradeWrapper;
import mezz.jei.api.recipe.RecipeType;

import java.util.List;

public final class VillagerTradeRecipeTypes {
    public static final RecipeType<VillagerTradeWrapper> NOVICE =
            RecipeType.create(Constants.MOD_ID, "trades/novice", VillagerTradeWrapper.class);
    public static final RecipeType<VillagerTradeWrapper> APPRENTICE =
            RecipeType.create(Constants.MOD_ID, "trades/apprentice", VillagerTradeWrapper.class);
    public static final RecipeType<VillagerTradeWrapper> JOURNEYMAN =
            RecipeType.create(Constants.MOD_ID, "trades/journeyman", VillagerTradeWrapper.class);
    public static final RecipeType<VillagerTradeWrapper> EXPERT =
            RecipeType.create(Constants.MOD_ID, "trades/expert", VillagerTradeWrapper.class);
    public static final RecipeType<VillagerTradeWrapper> MASTER =
            RecipeType.create(Constants.MOD_ID, "trades/master", VillagerTradeWrapper.class);

    private VillagerTradeRecipeTypes() {
    }

    public static RecipeType<VillagerTradeWrapper> byLevel(int level) {
        return switch (level) {
            case 1 -> NOVICE;
            case 2 -> APPRENTICE;
            case 3 -> JOURNEYMAN;
            case 4 -> EXPERT;
            case 5 -> MASTER;
            default -> throw new IllegalArgumentException("Unsupported villager level: " + level);
        };
    }

    public static List<RecipeType<VillagerTradeWrapper>> all() {
        return List.of(NOVICE, APPRENTICE, JOURNEYMAN, EXPERT, MASTER);
    }
}
