package com.gabrieloliv.jevtrades.trade;

import com.gabrieloliv.jevtrades.Constants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class VillagerTradeCollector {
    private static final Map<ResourceLocation, List<VillagerTradeEntry>> TRADES_BY_PROFESSION = new HashMap<>();

    private VillagerTradeCollector() {
    }

    public static void collect(VillagerTradesEvent event) {
        VillagerProfession profession = event.getType();
        ResourceLocation professionId = ForgeRegistries.VILLAGER_PROFESSIONS.getKey(profession);
        if (professionId == null) {
            return;
        }

        List<VillagerTradeEntry> collectedTrades = new ArrayList<>();

        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
        for (int level = 1; level <= 5; level++) {
            List<VillagerTrades.ItemListing> listings = trades.get(level);
            if (listings == null || listings.isEmpty()) {
                continue;
            }

            for (VillagerTrades.ItemListing listing : listings) {
                MerchantOffer offer = createOffer(profession, level, listing);
                if (offer == null || offer.getResult().isEmpty()) {
                    continue;
                }

                collectedTrades.add(new VillagerTradeEntry(
                        profession,
                        level,
                        offer.getBaseCostA(),
                        offer.getCostB(),
                        offer.getResult()
                ));
            }
        }

        collectedTrades.sort(Comparator
                .comparingInt(VillagerTradeEntry::level)
                .thenComparing(entry -> safeToString(ForgeRegistries.ITEMS.getKey(entry.output().getItem())))
                .thenComparing(entry -> safeToString(ForgeRegistries.ITEMS.getKey(entry.inputA().getItem())))
                .thenComparing(entry -> safeToString(ForgeRegistries.ITEMS.getKey(entry.inputB().getItem()))));

        TRADES_BY_PROFESSION.remove(professionId);
        if (!collectedTrades.isEmpty()) {
            TRADES_BY_PROFESSION.put(professionId, collectedTrades);
        }
    }

    public static List<ResourceLocation> getProfessionsWithTrades() {
        return TRADES_BY_PROFESSION.keySet().stream()
                .sorted(Comparator.comparing(ResourceLocation::toString))
                .collect(Collectors.toList());
    }

    public static List<VillagerTradeWrapper> getTradeWrappers(ResourceLocation professionId) {
        return TRADES_BY_PROFESSION.getOrDefault(professionId, List.of()).stream()
                .map(VillagerTradeWrapper::new)
                .toList();
    }

    public static List<VillagerTradeWrapper> getTradeWrappers(int level) {
        return TRADES_BY_PROFESSION.values().stream()
                .flatMap(List::stream)
                .filter(entry -> entry.level() == level)
                .map(VillagerTradeWrapper::new)
                .toList();
    }

    public static List<VillagerTradeWrapper> getTradeWrappers(ResourceLocation professionId, int level) {
        return TRADES_BY_PROFESSION.getOrDefault(professionId, List.of()).stream()
                .filter(entry -> entry.level() == level)
                .map(VillagerTradeWrapper::new)
                .toList();
    }

    public static boolean hasTrades(ResourceLocation professionId, int level) {
        return TRADES_BY_PROFESSION.getOrDefault(professionId, List.of()).stream()
                .anyMatch(entry -> entry.level() == level);
    }

    private static MerchantOffer createOffer(VillagerProfession profession, int level, VillagerTrades.ItemListing listing) {
        try {
            Entity trader = null;
            return listing.getOffer(trader, RandomSource.create(0L));
        } catch (Throwable throwable) {
            Constants.LOGGER.warn("Unable to resolve villager trade for profession {} level {}",
                    ForgeRegistries.VILLAGER_PROFESSIONS.getKey(profession), level, throwable);
            return null;
        }
    }

    private static String safeToString(ResourceLocation location) {
        return location != null ? location.toString() : "";
    }
}
