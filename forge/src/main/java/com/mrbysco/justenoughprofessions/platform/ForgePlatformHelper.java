package com.mrbysco.justenoughprofessions.platform;

import com.mrbysco.justenoughprofessions.ForgeProfessionPlugin;
import com.mrbysco.justenoughprofessions.jei.ProfessionWrapper;
import com.mrbysco.justenoughprofessions.jei.VillagerTradeWrapper;
import com.mrbysco.justenoughprofessions.platform.services.IPlatformHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgePlatformHelper implements IPlatformHelper {

	@Override
	public ResourceLocation getEntityKey(EntityType entityType) {
		return ForgeRegistries.ENTITY_TYPES.getKey(entityType);
	}

	@Override
	public ResourceLocation getProfessionKey(VillagerProfession villagerProfession) {
		return ForgeRegistries.VILLAGER_PROFESSIONS.getKey(villagerProfession);
	}

	@Override
	public RecipeType<ProfessionWrapper> getProfessionType() {
		return ForgeProfessionPlugin.PROFESSION_TYPE;
	}

	@Override
	public RecipeType<VillagerTradeWrapper> getTradeType(int level) {
		return switch (level) {
			case 1 -> ForgeProfessionPlugin.TRADE_NOVICE_TYPE;
			case 2 -> ForgeProfessionPlugin.TRADE_APPRENTICE_TYPE;
			case 3 -> ForgeProfessionPlugin.TRADE_JOURNEYMAN_TYPE;
			case 4 -> ForgeProfessionPlugin.TRADE_EXPERT_TYPE;
			case 5 -> ForgeProfessionPlugin.TRADE_MASTER_TYPE;
			default -> throw new IllegalArgumentException("Unsupported villager trade level: " + level);
		};
	}
}
