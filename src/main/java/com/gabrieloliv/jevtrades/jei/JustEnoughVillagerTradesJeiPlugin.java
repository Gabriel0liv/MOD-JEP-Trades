package com.gabrieloliv.jevtrades.jei;

import com.gabrieloliv.jevtrades.Constants;
import com.gabrieloliv.jevtrades.trade.VillagerTradeCollector;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@JeiPlugin
public class JustEnoughVillagerTradesJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(Constants.MOD_ID, "jei_plugin");
    private static final ResourceLocation NONE_PROFESSION = new ResourceLocation("minecraft", "none");
    private static final ResourceLocation NITWIT_PROFESSION = new ResourceLocation("minecraft", "nitwit");

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
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        registration.addExtraItemStacks(getRegisteredProfessionTokens());
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        for (ResourceLocation professionId : getRegisteredProfessionIds()) {
            ItemStack token = ProfessionTokenHelper.createProfessionToken(professionId);
            String readable = ProfessionTokenHelper.toReadableName(professionId);

            registration.addAliases(VanillaTypes.ITEM_STACK, token, List.of(
                    "trades",
                    "villager trades",
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

        for (var type : VillagerTradeRecipeTypes.all()) {
            registration.addRecipeCatalyst(emerald, type);
            registration.addRecipeCatalyst(villagerEgg, type);
        }

        for (ResourceLocation professionId : VillagerTradeCollector.getProfessionsWithTrades()) {
            ItemStack token = ProfessionTokenHelper.createProfessionToken(professionId);

            for (int level = 1; level <= 5; level++) {
                if (VillagerTradeCollector.hasTrades(professionId, level)) {
                    registration.addRecipeCatalyst(token, VillagerTradeRecipeTypes.byLevel(level));
                }
            }
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        for (int level = 1; level <= 5; level++) {
            registration.addRecipes(VillagerTradeRecipeTypes.byLevel(level), VillagerTradeCollector.getTradeWrappers(level));
        }
    }

    private static List<ResourceLocation> getRegisteredProfessionIds() {
        return ForgeRegistries.VILLAGER_PROFESSIONS.getKeys().stream()
                .filter(JustEnoughVillagerTradesJeiPlugin::isTradeProfession)
                .sorted(ResourceLocation::compareTo)
                .toList();
    }

    private static List<ItemStack> getRegisteredProfessionTokens() {
        return getRegisteredProfessionIds().stream()
                .map(ProfessionTokenHelper::createProfessionToken)
                .toList();
    }

    private static boolean isTradeProfession(ResourceLocation professionId) {
        return professionId != null
                && !professionId.equals(NONE_PROFESSION)
                && !professionId.equals(NITWIT_PROFESSION);
    }
}
