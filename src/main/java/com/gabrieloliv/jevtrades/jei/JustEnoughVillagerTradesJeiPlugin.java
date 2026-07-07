package com.gabrieloliv.jevtrades.jei;

import com.gabrieloliv.jevtrades.Constants;
import com.gabrieloliv.jevtrades.jei.profession.ProfessionIngredient;
import com.gabrieloliv.jevtrades.jei.profession.ProfessionIngredientHelper;
import com.gabrieloliv.jevtrades.jei.profession.ProfessionIngredientRenderer;
import com.gabrieloliv.jevtrades.jei.profession.ProfessionIngredientType;
import com.gabrieloliv.jevtrades.trade.VillagerTradeCollector;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

@JeiPlugin
public class JustEnoughVillagerTradesJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(Constants.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(Items.VILLAGER_SPAWN_EGG, (stack, context) -> {
            ResourceLocation professionId = ProfessionTokenHelper.getProfessionId(stack);
            return professionId == null ? IIngredientSubtypeInterpreter.NONE : professionId.toString();
        });
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(
                ProfessionIngredientType.TYPE,
                ProfessionIngredientHelper.getRegisteredProfessionIngredients(),
                new ProfessionIngredientHelper(),
                new ProfessionIngredientRenderer()
        );
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        for (ProfessionIngredient ingredient : ProfessionIngredientHelper.getRegisteredProfessionIngredients()) {
            ResourceLocation professionId = ingredient.professionId();
            String readable = ingredient.readableName();

            registration.addAliases(ProfessionIngredientType.TYPE, ingredient, List.of(
                    "trades",
                    "villager trades",
                    "just enough trades",
                    readable,
                    professionId.getPath(),
                    professionId.toString()
            ));
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new VillagerTradeCategory(guiHelper, VillagerTradeRecipeTypes.NOVICE, "jevtrades.trades.novice.title", 1),
                new VillagerTradeCategory(guiHelper, VillagerTradeRecipeTypes.APPRENTICE, "jevtrades.trades.apprentice.title", 2),
                new VillagerTradeCategory(guiHelper, VillagerTradeRecipeTypes.JOURNEYMAN, "jevtrades.trades.journeyman.title", 3),
                new VillagerTradeCategory(guiHelper, VillagerTradeRecipeTypes.EXPERT, "jevtrades.trades.expert.title", 4),
                new VillagerTradeCategory(guiHelper, VillagerTradeRecipeTypes.MASTER, "jevtrades.trades.master.title", 5)
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ItemStack emerald = new ItemStack(Items.EMERALD);
        ItemStack villagerEgg = new ItemStack(Items.VILLAGER_SPAWN_EGG);

        for (var type : VillagerTradeRecipeTypes.all()) {
            registration.addRecipeCatalyst(emerald, type);
            registration.addRecipeCatalyst(villagerEgg, type);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        for (int level = 1; level <= 5; level++) {
            registration.addRecipes(VillagerTradeRecipeTypes.byLevel(level), VillagerTradeCollector.getTradeWrappers(level));
        }
    }
}
