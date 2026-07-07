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

public final class VillagerTradeCollector {
    private static final Map<Integer, List<VillagerTradeEntry>> TRADES_BY_LEVEL = createTradeMap();

    private VillagerTradeCollector() {
    }

    public static void collect(VillagerTradesEvent event) {
        VillagerProfession profession = event.getType();
        removeExistingEntries(profession);

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

                TRADES_BY_LEVEL.get(level).add(new VillagerTradeEntry(
                        profession,
                        level,
                        offer.getBaseCostA(),
                        offer.getCostB(),
                        offer.getResult()
                ));
            }
        }
    }

    public static List<VillagerTradeWrapper> getTradeWrappers(int level) {
        return TRADES_BY_LEVEL.getOrDefault(level, List.of()).stream()
                .sorted(Comparator
                        .comparing((VillagerTradeEntry entry) -> safeToString(ForgeRegistries.VILLAGER_PROFESSIONS.getKey(entry.profession())))
                        .thenComparing(entry -> safeToString(ForgeRegistries.ITEMS.getKey(entry.output().getItem())))
                        .thenComparing(entry -> safeToString(ForgeRegistries.ITEMS.getKey(entry.inputA().getItem())))
                        .thenComparing(entry -> safeToString(ForgeRegistries.ITEMS.getKey(entry.inputB().getItem()))))
                .map(VillagerTradeWrapper::new)
                .toList();
    }

    private static Map<Integer, List<VillagerTradeEntry>> createTradeMap() {
        Map<Integer, List<VillagerTradeEntry>> tradesByLevel = new HashMap<>();
        for (int level = 1; level <= 5; level++) {
            tradesByLevel.put(level, new ArrayList<>());
        }
        return tradesByLevel;
    }

    private static void removeExistingEntries(VillagerProfession profession) {
        for (List<VillagerTradeEntry> trades : TRADES_BY_LEVEL.values()) {
            trades.removeIf(entry -> entry.profession() == profession);
        }
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
