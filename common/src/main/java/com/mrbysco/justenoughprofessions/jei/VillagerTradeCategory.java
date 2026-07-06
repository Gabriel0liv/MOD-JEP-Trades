package com.mrbysco.justenoughprofessions.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.justenoughprofessions.platform.Services;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VillagerTradeCategory implements IRecipeCategory<VillagerTradeWrapper> {
	private static final int INPUT_A_X = 8;
	private static final int INPUT_B_X = 31;
	private static final int OUTPUT_X = 72;
	private static final int SLOT_Y = 25;

	private final int level;
	private final Component title;
	private final IDrawableStatic background;
	private final IDrawable icon;
	private final IDrawableStatic slotDrawable;

	public VillagerTradeCategory(IGuiHelper guiHelper, int level, String titleKey) {
		this.level = level;
		this.title = Component.translatable(titleKey);
		this.background = guiHelper.createBlankDrawable(118, 48);
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(Items.EMERALD));
		this.slotDrawable = guiHelper.getSlotDrawable();
	}

	@Override
	public RecipeType<VillagerTradeWrapper> getRecipeType() {
		return Services.PLATFORM.getTradeType(level);
	}

	@Override
	public Component getTitle() {
		return title;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, VillagerTradeWrapper recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, INPUT_A_X + 1, SLOT_Y + 1).addItemStack(recipe.getInputA());
		if (!recipe.getInputB().isEmpty()) {
			builder.addSlot(RecipeIngredientRole.INPUT, INPUT_B_X + 1, SLOT_Y + 1).addItemStack(recipe.getInputB());
		}
		builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X + 1, SLOT_Y + 1).addItemStack(recipe.getOutput());
	}

	@Override
	public void draw(VillagerTradeWrapper tradeWrapper, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		this.slotDrawable.draw(guiGraphics, INPUT_A_X, SLOT_Y);
		if (!tradeWrapper.getInputB().isEmpty()) {
			this.slotDrawable.draw(guiGraphics, INPUT_B_X, SLOT_Y);
		}
		this.slotDrawable.draw(guiGraphics, OUTPUT_X, SLOT_Y);

		Font font = Minecraft.getInstance().font;
		String text = Screen.hasShiftDown() ? tradeWrapper.getProfessionName().toString() : tradeWrapper.getProfessionName().getPath();
		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();
		if (font.width(text) > 116) {
			poseStack.scale(0.75F, 0.75F, 0.75F);
		}
		guiGraphics.drawString(font, text, 0, 3, 8, false);
		poseStack.popPose();

		guiGraphics.drawString(font, ">", 57, 31, 0x555555, false);
	}
}
