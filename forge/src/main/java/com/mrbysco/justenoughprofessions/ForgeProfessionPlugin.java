package com.mrbysco.justenoughprofessions;

import com.mrbysco.justenoughprofessions.compat.CompatibilityHelper;
import com.mrbysco.justenoughprofessions.jei.ProfessionCategory;
import com.mrbysco.justenoughprofessions.jei.ProfessionEntry;
import com.mrbysco.justenoughprofessions.jei.ProfessionWrapper;
import com.mrbysco.justenoughprofessions.jei.VillagerTradeCategory;
import com.mrbysco.justenoughprofessions.jei.VillagerTradeWrapper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedList;
import java.util.List;

@JeiPlugin
public class ForgeProfessionPlugin implements IModPlugin {
	private static final ResourceLocation UID = new ResourceLocation(Constants.MOD_ID, "jei_plugin");

	public static final RecipeType<ProfessionWrapper> PROFESSION_TYPE = RecipeType.create(Constants.MOD_ID, "professions", ProfessionWrapper.class);
	public static final RecipeType<VillagerTradeWrapper> TRADE_NOVICE_TYPE = RecipeType.create(Constants.MOD_ID, "trades/novice", VillagerTradeWrapper.class);
	public static final RecipeType<VillagerTradeWrapper> TRADE_APPRENTICE_TYPE = RecipeType.create(Constants.MOD_ID, "trades/apprentice", VillagerTradeWrapper.class);
	public static final RecipeType<VillagerTradeWrapper> TRADE_JOURNEYMAN_TYPE = RecipeType.create(Constants.MOD_ID, "trades/journeyman", VillagerTradeWrapper.class);
	public static final RecipeType<VillagerTradeWrapper> TRADE_EXPERT_TYPE = RecipeType.create(Constants.MOD_ID, "trades/expert", VillagerTradeWrapper.class);
	public static final RecipeType<VillagerTradeWrapper> TRADE_MASTER_TYPE = RecipeType.create(Constants.MOD_ID, "trades/master", VillagerTradeWrapper.class);

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(
				new ProfessionCategory(registration.getJeiHelpers().getGuiHelper()),
				new VillagerTradeCategory(registration.getJeiHelpers().getGuiHelper(), 1, "justenoughprofessions.trades.novice.title"),
				new VillagerTradeCategory(registration.getJeiHelpers().getGuiHelper(), 2, "justenoughprofessions.trades.apprentice.title"),
				new VillagerTradeCategory(registration.getJeiHelpers().getGuiHelper(), 3, "justenoughprofessions.trades.journeyman.title"),
				new VillagerTradeCategory(registration.getJeiHelpers().getGuiHelper(), 4, "justenoughprofessions.trades.expert.title"),
				new VillagerTradeCategory(registration.getJeiHelpers().getGuiHelper(), 5, "justenoughprofessions.trades.master.title")
		);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(Items.EMERALD), PROFESSION_TYPE);
		registration.addRecipeCatalyst(new ItemStack(Items.VILLAGER_SPAWN_EGG), PROFESSION_TYPE);
		registration.addRecipeCatalyst(new ItemStack(Items.EMERALD), TRADE_NOVICE_TYPE, TRADE_APPRENTICE_TYPE, TRADE_JOURNEYMAN_TYPE, TRADE_EXPERT_TYPE, TRADE_MASTER_TYPE);
		registration.addRecipeCatalyst(new ItemStack(Items.VILLAGER_SPAWN_EGG), TRADE_NOVICE_TYPE, TRADE_APPRENTICE_TYPE, TRADE_JOURNEYMAN_TYPE, TRADE_EXPERT_TYPE, TRADE_MASTER_TYPE);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		List<ProfessionWrapper> entries = new LinkedList<>();
		for (VillagerProfession profession : ForgeRegistries.VILLAGER_PROFESSIONS) {
			if (profession == VillagerProfession.NONE) {
				continue;
			}
			List<ItemStack> stacks = new LinkedList<>();
			List<ResourceLocation> knownItems = new LinkedList<>();
			for (PoiType poiType : ForgeRegistries.POI_TYPES.getValues()) {
				if (profession.acquirableJobSite().test(ForgeRegistries.POI_TYPES.getHolder(poiType).orElse(null))) {
					for (BlockState state : poiType.matchingStates()) {
						Block block = ForgeRegistries.BLOCKS.getValue(ForgeRegistries.BLOCKS.getKey(state.getBlock()));
						if (block != null) {
							ItemStack stack = CompatibilityHelper.compatibilityCheck(new ItemStack(block), ForgeRegistries.VILLAGER_PROFESSIONS.getKey(profession));
							ResourceLocation location = ForgeRegistries.ITEMS.getKey(stack.getItem());
							if (!stack.isEmpty() && !knownItems.contains(location)) {
								stacks.add(stack);
								knownItems.add(location);
							}
						}
					}
				}
			}
			if (!stacks.isEmpty()) {
				entries.add(new ProfessionWrapper(new ProfessionEntry(profession, stacks)));
			}
		}
		registration.addRecipes(PROFESSION_TYPE, entries);
		registration.addRecipes(TRADE_NOVICE_TYPE, VillagerTradeCollector.getTradeWrappers(1));
		registration.addRecipes(TRADE_APPRENTICE_TYPE, VillagerTradeCollector.getTradeWrappers(2));
		registration.addRecipes(TRADE_JOURNEYMAN_TYPE, VillagerTradeCollector.getTradeWrappers(3));
		registration.addRecipes(TRADE_EXPERT_TYPE, VillagerTradeCollector.getTradeWrappers(4));
		registration.addRecipes(TRADE_MASTER_TYPE, VillagerTradeCollector.getTradeWrappers(5));
	}
}
