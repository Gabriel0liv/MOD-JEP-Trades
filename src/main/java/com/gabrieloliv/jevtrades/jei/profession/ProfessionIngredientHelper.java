package com.gabrieloliv.jevtrades.jei.profession;

import com.gabrieloliv.jevtrades.jei.ProfessionTokenHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ProfessionIngredientHelper implements IIngredientHelper<ProfessionIngredient> {
    private static final ResourceLocation NONE_PROFESSION = new ResourceLocation("minecraft", "none");
    private static final ResourceLocation NITWIT_PROFESSION = new ResourceLocation("minecraft", "nitwit");

    public static List<ResourceLocation> getRegisteredProfessionIds() {
        return ForgeRegistries.VILLAGER_PROFESSIONS.getKeys().stream()
                .filter(ProfessionIngredientHelper::isTradeProfession)
                .sorted(ResourceLocation::compareTo)
                .toList();
    }

    public static List<ProfessionIngredient> getRegisteredProfessionIngredients() {
        return getRegisteredProfessionIds().stream()
                .map(ProfessionIngredient::new)
                .toList();
    }

    public static boolean isTradeProfession(ResourceLocation professionId) {
        return professionId != null
                && !professionId.equals(NONE_PROFESSION)
                && !professionId.equals(NITWIT_PROFESSION);
    }

    @Override
    public mezz.jei.api.ingredients.IIngredientType<ProfessionIngredient> getIngredientType() {
        return ProfessionIngredientType.TYPE;
    }

    @Override
    public String getDisplayName(ProfessionIngredient ingredient) {
        return ingredient.displayName().getString();
    }

    @Override
    public String getUniqueId(ProfessionIngredient ingredient, UidContext context) {
        return ingredient.professionId().toString();
    }

    @Override
    public boolean hasSubtypes(ProfessionIngredient ingredient) {
        return false;
    }

    @Override
    public String getWildcardId(ProfessionIngredient ingredient) {
        return ingredient.professionId().toString();
    }

    @Override
    public String getDisplayModId(ProfessionIngredient ingredient) {
        return ingredient.professionId().getNamespace();
    }

    @Override
    public long getAmount(ProfessionIngredient ingredient) {
        return 1;
    }

    @Override
    public ProfessionIngredient copyWithAmount(ProfessionIngredient ingredient, long amount) {
        return ingredient;
    }

    @Override
    public Iterable<Integer> getColors(ProfessionIngredient ingredient) {
        return List.of();
    }

    @Override
    public ResourceLocation getResourceLocation(ProfessionIngredient ingredient) {
        return ingredient.professionId();
    }

    @Override
    public ItemStack getCheatItemStack(ProfessionIngredient ingredient) {
        return ProfessionTokenHelper.createProfessionToken(ingredient.professionId());
    }

    @Override
    public ProfessionIngredient copyIngredient(ProfessionIngredient ingredient) {
        return ingredient;
    }

    @Override
    public ProfessionIngredient normalizeIngredient(ProfessionIngredient ingredient) {
        return ingredient;
    }

    @Override
    public boolean isValidIngredient(ProfessionIngredient ingredient) {
        return ingredient != null && ingredient.professionId() != null;
    }

    @Override
    public boolean isIngredientOnServer(ProfessionIngredient ingredient) {
        return true;
    }

    @Override
    public Stream<ResourceLocation> getTagStream(ProfessionIngredient ingredient) {
        return Stream.empty();
    }

    @Override
    public boolean isHiddenFromRecipeViewersByTags(ProfessionIngredient ingredient) {
        return false;
    }

    @Override
    public String getErrorInfo(ProfessionIngredient ingredient) {
        return ingredient == null ? "null profession ingredient" : ingredient.professionId().toString();
    }

    @Override
    public Optional<TagKey<?>> getTagKeyEquivalent(Collection<ProfessionIngredient> ingredients) {
        return Optional.empty();
    }

    @Override
    public Optional<ResourceLocation> getTagEquivalent(Collection<ProfessionIngredient> ingredients) {
        return Optional.empty();
    }
}
