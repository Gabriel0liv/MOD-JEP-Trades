package com.gabrieloliv.jevtrades.jei;

import com.gabrieloliv.jevtrades.Constants;
import com.gabrieloliv.jevtrades.trade.VillagerTradeWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.WeakHashMap;

public class VillagerTradeCategory implements IRecipeCategory<VillagerTradeWrapper> {
    private static final ResourceLocation PROFESSION_ICON =
            new ResourceLocation(Constants.MOD_ID, "textures/gui/profession_icon.png");
    private static final int BACKGROUND_WIDTH = 118;
    private static final int BACKGROUND_HEIGHT = 42;
    private static final int INPUT_A_X = 4;
    private static final int INPUT_B_X = 24;
    private static final int OUTPUT_X = 80;
    private static final int SLOT_Y = 16;
    private static final int TEXT_MAX_WIDTH = 110;

    private final Map<VillagerTradeWrapper, ResourceLocation> focusedProfessions = new WeakHashMap<>();
    private final RecipeType<VillagerTradeWrapper> recipeType;
    private final Component title;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic slotDrawable;

    public VillagerTradeCategory(IGuiHelper guiHelper, RecipeType<VillagerTradeWrapper> recipeType, String titleKey) {
        this.recipeType = recipeType;
        this.title = Component.translatable(titleKey);
        this.background = guiHelper.createBlankDrawable(BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = new IDrawable() {
            @Override
            public int getWidth() {
                return 16;
            }

            @Override
            public int getHeight() {
                return 16;
            }

            @Override
            public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
                guiGraphics.blit(PROFESSION_ICON, xOffset, yOffset, 0, 0, 16, 16, 256, 256);
            }
        };
        this.slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public RecipeType<VillagerTradeWrapper> getRecipeType() {
        return recipeType;
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
        ResourceLocation focusedProfession = getFocusedProfession(focuses);
        if (focusedProfession != null) {
            focusedProfessions.put(recipe, focusedProfession);
        } else {
            focusedProfessions.remove(recipe);
        }

        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_A_X + 1, SLOT_Y + 1)
                .addItemStacks(recipe.getInputAOptions());

        if (!recipe.getInputBOptions().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_B_X + 1, SLOT_Y + 1)
                    .addItemStacks(recipe.getInputBOptions());
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X + 1, SLOT_Y + 1)
                .addItemStacks(recipe.getOutputOptions());

        ItemStack professionToken = recipe.getProfessionToken();
        if (!professionToken.isEmpty()) {
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                    .addItemStack(professionToken);
            builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
                    .addItemStack(professionToken);
        }
    }

    @Override
    public void draw(VillagerTradeWrapper recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        slotDrawable.draw(guiGraphics, INPUT_A_X, SLOT_Y);
        if (!recipe.getInputBOptions().isEmpty()) {
            slotDrawable.draw(guiGraphics, INPUT_B_X, SLOT_Y);
        }
        slotDrawable.draw(guiGraphics, OUTPUT_X, SLOT_Y);

        if (!focusedProfessions.containsKey(recipe)) {
            Font font = Minecraft.getInstance().font;
            ResourceLocation professionId = recipe.getProfessionId();
            String professionText = professionId == null ? "unknown" : ProfessionTokenHelper.toReadableName(professionId);
            float scale = Math.min(0.75F, (float) TEXT_MAX_WIDTH / Math.max(1, font.width(professionText)));

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(4.0F, 4.0F, 0.0F);
            poseStack.scale(scale, scale, 1.0F);
            guiGraphics.drawString(font, professionText, 0, 0, 0x404040, false);
            poseStack.popPose();
        }

        guiGraphics.drawString(Minecraft.getInstance().font, ">", 58, 22, 0x606060, false);
    }

    private static ResourceLocation getFocusedProfession(IFocusGroup focuses) {
        return focuses.getItemStackFocuses()
                .map(IFocus::getTypedValue)
                .map(typed -> typed.getItemStack().orElse(ItemStack.EMPTY))
                .map(ProfessionTokenHelper::getProfessionId)
                .filter(id -> id != null)
                .findFirst()
                .orElse(null);
    }
}
