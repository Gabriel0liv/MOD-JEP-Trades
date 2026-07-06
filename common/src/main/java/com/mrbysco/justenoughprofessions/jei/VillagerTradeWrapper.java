package com.mrbysco.justenoughprofessions.jei;

import com.mrbysco.justenoughprofessions.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record VillagerTradeWrapper(VillagerTradeEntry entry) {
	public ResourceLocation getProfessionName() {
		return Services.PLATFORM.getProfessionKey(entry.profession());
	}

	public ItemStack getInputA() {
		return entry.inputA();
	}

	public ItemStack getInputB() {
		return entry.inputB();
	}

	public ItemStack getOutput() {
		return entry.output();
	}
}
