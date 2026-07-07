package com.gabrieloliv.jevtrades.jei.profession;

import mezz.jei.api.ingredients.IIngredientType;

import java.util.Optional;

public final class ProfessionIngredientType implements IIngredientType<ProfessionIngredient> {
    public static final ProfessionIngredientType TYPE = new ProfessionIngredientType();

    private ProfessionIngredientType() {
    }

    @Override
    public Class<? extends ProfessionIngredient> getIngredientClass() {
        return ProfessionIngredient.class;
    }

    @Override
    public String getUid() {
        return "jevtrades:profession";
    }

    @Override
    public Optional<ProfessionIngredient> castIngredient(Object ingredient) {
        return ingredient instanceof ProfessionIngredient professionIngredient
                ? Optional.of(professionIngredient)
                : Optional.empty();
    }
}
