package com.gabrieloliv.jevtrades.jei.profession;

import com.gabrieloliv.jevtrades.Constants;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.rendering.BatchRenderElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ProfessionIngredientRenderer implements IIngredientRenderer<ProfessionIngredient> {
    private static final ResourceLocation PROFESSION_ICON =
            new ResourceLocation(Constants.MOD_ID, "textures/gui/profession_icon.png");

    @Override
    public void render(GuiGraphics guiGraphics, ProfessionIngredient ingredient) {
        render(guiGraphics, ingredient, 0, 0);
    }

    @Override
    public void render(GuiGraphics guiGraphics, ProfessionIngredient ingredient, int xOffset, int yOffset) {
        guiGraphics.blit(PROFESSION_ICON, xOffset, yOffset, 0, 0, 16, 16, 256, 256);
    }

    @Override
    public void renderBatch(GuiGraphics guiGraphics, List<BatchRenderElement<ProfessionIngredient>> ingredients) {
        for (BatchRenderElement<ProfessionIngredient> element : ingredients) {
            render(guiGraphics, element.ingredient(), element.x(), element.y());
        }
    }

    @Override
    public List<Component> getTooltip(ProfessionIngredient ingredient, TooltipFlag tooltipFlag) {
        return List.of(
                ingredient.displayName(),
                Component.literal(ingredient.professionId().toString()).withStyle(ChatFormatting.BLUE)
        );
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltipBuilder, ProfessionIngredient ingredient, TooltipFlag tooltipFlag) {
        for (Component line : getTooltip(ingredient, tooltipFlag)) {
            tooltipBuilder.add(line);
        }
    }

    @Override
    public Font getFontRenderer(Minecraft minecraft, ProfessionIngredient ingredient) {
        return minecraft.font;
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }
}
