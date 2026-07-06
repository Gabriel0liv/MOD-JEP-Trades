package com.mrbysco.justenoughprofessions.platform.services;

import com.mrbysco.justenoughprofessions.jei.ProfessionWrapper;
import com.mrbysco.justenoughprofessions.jei.VillagerTradeWrapper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;

public interface IPlatformHelper {

	/**
	 * Get the registry name of the entitytype
	 *
	 * @param entityType The entity type
	 * @return The registry name
	 */
	ResourceLocation getEntityKey(EntityType entityType);

	/**
	 * Get the registry name of the profession
	 *
	 * @param villagerProfession The villager profession
	 * @return The registry name
	 */
	ResourceLocation getProfessionKey(VillagerProfession villagerProfession);

	/**
	 * Get the RecipeType for JEI
	 *
	 * @return The profession RecipeType
	 */
	RecipeType<ProfessionWrapper> getProfessionType();

	/**
	 * Get the RecipeType for villager trades by profession level.
	 *
	 * @param level The villager profession level
	 * @return The trade RecipeType
	 */
	RecipeType<VillagerTradeWrapper> getTradeType(int level);
}
