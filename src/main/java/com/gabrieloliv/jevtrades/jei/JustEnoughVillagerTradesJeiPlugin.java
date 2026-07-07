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
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;

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
        for (ResourceLocation professionId : getProfessionIds()) {
            registration.addRecipeCategories(new VillagerTradeCategory(guiHelper,
                    VillagerTradeRecipeTypes.getOrCreate(professionId), professionId));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ItemStack emerald = new ItemStack(Items.EMERALD);
        ItemStack villagerEgg = new ItemStack(Items.VILLAGER_SPAWN_EGG);
        for (ResourceLocation professionId : getProfessionIds()) {
            var recipeType = VillagerTradeRecipeTypes.getOrCreate(professionId);
            registration.addRecipeCatalyst(emerald, recipeType);
            registration.addRecipeCatalyst(villagerEgg, recipeType);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        for (ResourceLocation professionId : getProfessionIds()) {
            registration.addRecipes(VillagerTradeRecipeTypes.getOrCreate(professionId),
                    VillagerTradeCollector.getTradeWrappers(professionId));
        }
    }

    private static List<ResourceLocation> getProfessionIds() {
        return ForgeRegistries.VILLAGER_PROFESSIONS.getValues().stream()
                .filter(profession -> profession != VillagerProfession.NONE)
                .map(ForgeRegistries.VILLAGER_PROFESSIONS::getKey)
                .filter(id -> id != null)
                .sorted(Comparator.comparing(ResourceLocation::toString))
                .toList();
    }
}
