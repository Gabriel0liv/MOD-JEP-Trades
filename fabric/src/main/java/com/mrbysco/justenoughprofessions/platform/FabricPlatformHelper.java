package com.mrbysco.justenoughprofessions.platform;

import com.mrbysco.justenoughprofessions.FabricProfessionPlugin;
import com.mrbysco.justenoughprofessions.jei.ProfessionWrapper;
import com.mrbysco.justenoughprofessions.jei.VillagerTradeWrapper;
import com.mrbysco.justenoughprofessions.platform.services.IPlatformHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;

public class FabricPlatformHelper implements IPlatformHelper {

	@Override
	public ResourceLocation getEntityKey(EntityType entityType) {
		return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
	}

	@Override
	public ResourceLocation getProfessionKey(VillagerProfession villagerProfession) {
		return BuiltInRegistries.VILLAGER_PROFESSION.getKey(villagerProfession);
	}

	@Override
	public RecipeType<ProfessionWrapper> getProfessionType() {
		return FabricProfessionPlugin.PROFESSION_TYPE;
	}

	@Override
	public RecipeType<VillagerTradeWrapper> getTradeType(int level) {
		return switch (level) {
			case 1 -> FabricProfessionPlugin.TRADE_NOVICE_TYPE;
			case 2 -> FabricProfessionPlugin.TRADE_APPRENTICE_TYPE;
			case 3 -> FabricProfessionPlugin.TRADE_JOURNEYMAN_TYPE;
			case 4 -> FabricProfessionPlugin.TRADE_EXPERT_TYPE;
			case 5 -> FabricProfessionPlugin.TRADE_MASTER_TYPE;
			default -> throw new IllegalArgumentException("Unsupported villager trade level: " + level);
		};
	}
}
