package com.gabrieloliv.jevtrades.jei;

import com.gabrieloliv.jevtrades.Constants;
import com.gabrieloliv.jevtrades.trade.VillagerTradeCollector;
import com.gabrieloliv.jevtrades.trade.VillagerTradeWrapper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@JeiPlugin
public class JustEnoughVillagerTradesJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(Constants.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new VillagerTradeCategory(guiHelper, VillagerTradeRecipeTypes.NOVICE, "jevtrades.trades.novice.title"),
                new VillagerTradeCategory(guiHelper, VillagerTradeRecipeTypes.APPRENTICE, "jevtrades.trades.apprentice.title"),
                new VillagerTradeCategory(guiHelper, VillagerTradeRecipeTypes.JOURNEYMAN, "jevtrades.trades.journeyman.title"),
                new VillagerTradeCategory(guiHelper, VillagerTradeRecipeTypes.EXPERT, "jevtrades.trades.expert.title"),
                new VillagerTradeCategory(guiHelper, VillagerTradeRecipeTypes.MASTER, "jevtrades.trades.master.title")
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ItemStack emerald = new ItemStack(Items.EMERALD);
        ItemStack villagerEgg = new ItemStack(Items.VILLAGER_SPAWN_EGG);
        registration.addRecipeCatalyst(emerald,
                VillagerTradeRecipeTypes.NOVICE,
                VillagerTradeRecipeTypes.APPRENTICE,
                VillagerTradeRecipeTypes.JOURNEYMAN,
                VillagerTradeRecipeTypes.EXPERT,
                VillagerTradeRecipeTypes.MASTER);
        registration.addRecipeCatalyst(villagerEgg,
                VillagerTradeRecipeTypes.NOVICE,
                VillagerTradeRecipeTypes.APPRENTICE,
                VillagerTradeRecipeTypes.JOURNEYMAN,
                VillagerTradeRecipeTypes.EXPERT,
                VillagerTradeRecipeTypes.MASTER);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registerLevel(registration, 1);
        registerLevel(registration, 2);
        registerLevel(registration, 3);
        registerLevel(registration, 4);
        registerLevel(registration, 5);
    }

    private static void registerLevel(IRecipeRegistration registration, int level) {
        registration.addRecipes(VillagerTradeRecipeTypes.byLevel(level), VillagerTradeCollector.getTradeWrappers(level));
    }
}
