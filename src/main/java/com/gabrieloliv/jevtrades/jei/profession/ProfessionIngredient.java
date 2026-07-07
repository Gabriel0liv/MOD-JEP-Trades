package com.gabrieloliv.jevtrades.jei.profession;

import com.gabrieloliv.jevtrades.jei.ProfessionTokenHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record ProfessionIngredient(ResourceLocation professionId) {
    public String readableName() {
        return ProfessionTokenHelper.toReadableName(professionId);
    }

    public Component displayName() {
        return Component.translatable("jevtrades.profession_token", readableName());
    }
}
