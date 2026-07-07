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
        for (ResourceLocation professionId : VillagerTradeCollector.getProfessionsWithTrades()) {
            registration.addRecipeCategories(new VillagerTradeCategory(guiHelper,
                    VillagerTradeRecipeTypes.getOrCreate(professionId), professionId));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ItemStack emerald = new ItemStack(Items.EMERALD);
        ItemStack villagerEgg = new ItemStack(Items.VILLAGER_SPAWN_EGG);
        for (ResourceLocation professionId : VillagerTradeCollector.getProfessionsWithTrades()) {
            var recipeType = VillagerTradeRecipeTypes.getOrCreate(professionId);
            registration.addRecipeCatalyst(emerald, recipeType);
            registration.addRecipeCatalyst(villagerEgg, recipeType);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        for (ResourceLocation professionId : VillagerTradeCollector.getProfessionsWithTrades()) {
            registration.addRecipes(VillagerTradeRecipeTypes.getOrCreate(professionId),
                    VillagerTradeCollector.getTradeWrappers(professionId));
        }
    }
}
