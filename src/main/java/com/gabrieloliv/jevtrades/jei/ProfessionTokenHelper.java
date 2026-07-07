package com.gabrieloliv.jevtrades.jei;

import com.gabrieloliv.jevtrades.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Locale;

public final class ProfessionTokenHelper {
    public static final String PROFESSION_TAG = Constants.MOD_ID + "_profession";

    private ProfessionTokenHelper() {
    }

    public static ItemStack createProfessionToken(ResourceLocation professionId) {
        ItemStack stack = new ItemStack(Items.VILLAGER_SPAWN_EGG);

        String readable = toReadableName(professionId);
        stack.setHoverName(Component.translatable("jevtrades.profession_token", readable).withStyle(ChatFormatting.GOLD));

        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(PROFESSION_TAG, professionId.toString());

        CompoundTag entityTag = new CompoundTag();
        entityTag.putString("id", "minecraft:villager");

        CompoundTag villagerData = new CompoundTag();
        villagerData.putString("profession", professionId.toString());
        villagerData.putString("type", "minecraft:plains");
        villagerData.putInt("level", 1);

        entityTag.put("VillagerData", villagerData);
        tag.put("EntityTag", entityTag);

        return stack;
    }

    public static ResourceLocation getProfessionId(ItemStack stack) {
        if (stack.hasTag() && stack.getTag() != null && stack.getTag().contains(PROFESSION_TAG)) {
            return ResourceLocation.tryParse(stack.getTag().getString(PROFESSION_TAG));
        }
        return null;
    }

    public static String toReadableName(ResourceLocation professionId) {
        String path = professionId.getPath().replace('_', ' ');
        String[] parts = path.split(" ");
        StringBuilder builder = new StringBuilder();

        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(part.substring(0, 1).toUpperCase(Locale.ROOT));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }

        return builder.toString();
    }
}
