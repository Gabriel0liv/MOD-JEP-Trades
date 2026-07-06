package com.mrbysco.justenoughprofessions.jei;

import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;

public record VillagerTradeEntry(VillagerProfession profession, int level, ItemStack inputA, ItemStack inputB, ItemStack output) {
	public VillagerTradeEntry {
		inputA = normalize(inputA);
		inputB = normalize(inputB);
		output = normalize(output);
	}

	private static ItemStack normalize(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack copy = stack.copy();
		copy.setCount(1);
		return copy;
	}
}
