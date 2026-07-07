package com.gabrieloliv.jevtrades.trade;

import com.gabrieloliv.jevtrades.Constants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class VillagerTradeCollector {
    private static final int SAMPLE_ATTEMPTS = 512;
    private static final int MAX_VARIANTS_PER_TRADE = 128;
    private static final Map<ResourceLocation, List<VillagerTradeEntry>> TRADES_BY_PROFESSION = new HashMap<>();
    private static boolean clientBootstrapped = false;

    private VillagerTradeCollector() {
    }

    public static void ensureClientTradesLoaded() {
        if (clientBootstrapped) {
            return;
        }

        clientBootstrapped = true;
        TRADES_BY_PROFESSION.clear();

        for (ResourceLocation professionId : ForgeRegistries.VILLAGER_PROFESSIONS.getKeys()) {
            VillagerProfession profession = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(professionId);
            if (!isTradeProfession(professionId) || profession == null) {
                continue;
            }

            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = createMutableTradeMap(profession);
            VillagerTradesEvent event = new VillagerTradesEvent(trades, profession);
            MinecraftForge.EVENT_BUS.post(event);
            collect(event);
        }
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
                List<MerchantOffer> offers = createOffers(profession, level, listing);
                if (offers.isEmpty()) {
                    continue;
                }

                collectedTrades.add(createEntryFromOffers(profession, level, offers));
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
        ensureClientTradesLoaded();
        return TRADES_BY_PROFESSION.keySet().stream()
                .sorted(Comparator.comparing(ResourceLocation::toString))
                .collect(Collectors.toList());
    }

    public static List<VillagerTradeWrapper> getTradeWrappers(ResourceLocation professionId) {
        ensureClientTradesLoaded();
        return TRADES_BY_PROFESSION.getOrDefault(professionId, List.of()).stream()
                .map(VillagerTradeWrapper::new)
                .toList();
    }

    public static List<VillagerTradeWrapper> getTradeWrappers(int level) {
        ensureClientTradesLoaded();
        return TRADES_BY_PROFESSION.values().stream()
                .flatMap(List::stream)
                .filter(entry -> entry.level() == level)
                .map(VillagerTradeWrapper::new)
                .toList();
    }

    public static List<VillagerTradeWrapper> getTradeWrappers(ResourceLocation professionId, int level) {
        ensureClientTradesLoaded();
        return TRADES_BY_PROFESSION.getOrDefault(professionId, List.of()).stream()
                .filter(entry -> entry.level() == level)
                .map(VillagerTradeWrapper::new)
                .toList();
    }

    public static boolean hasTrades(ResourceLocation professionId, int level) {
        ensureClientTradesLoaded();
        return TRADES_BY_PROFESSION.getOrDefault(professionId, List.of()).stream()
                .anyMatch(entry -> entry.level() == level);
    }

    private static Int2ObjectMap<List<VillagerTrades.ItemListing>> createMutableTradeMap(VillagerProfession profession) {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> mutable = new Int2ObjectOpenHashMap<>();
        for (int level = 1; level <= 5; level++) {
            mutable.put(level, new ArrayList<>());
        }

        Object rawTrades = VillagerTrades.TRADES.get(profession);
        if (rawTrades instanceof Int2ObjectMap<?> vanillaTrades) {
            for (int level = 1; level <= 5; level++) {
                Object value = vanillaTrades.get(level);
                List<VillagerTrades.ItemListing> target = mutable.get(level);

                if (value instanceof VillagerTrades.ItemListing[] array) {
                    target.addAll(Arrays.asList(array));
                } else if (value instanceof List<?> list) {
                    for (Object item : list) {
                        if (item instanceof VillagerTrades.ItemListing listing) {
                            target.add(listing);
                        }
                    }
                }
            }
        }

        return mutable;
    }

    private static List<MerchantOffer> createOffers(VillagerProfession profession, int level, VillagerTrades.ItemListing listing) {
        Map<String, MerchantOffer> unique = new LinkedHashMap<>();

        for (int seed = 0; seed < SAMPLE_ATTEMPTS; seed++) {
            try {
                Entity trader = null;
                MerchantOffer offer = listing.getOffer(trader, RandomSource.create(seed));
                if (offer == null || offer.getResult().isEmpty()) {
                    continue;
                }

                unique.putIfAbsent(offerKey(offer), offer);
                if (unique.size() >= MAX_VARIANTS_PER_TRADE) {
                    break;
                }
            } catch (Throwable throwable) {
                if (seed == 0) {
                    Constants.LOGGER.warn("Unable to resolve villager trade for profession {} level {}",
                            ForgeRegistries.VILLAGER_PROFESSIONS.getKey(profession), level, throwable);
                }
                break;
            }
        }

        return List.copyOf(unique.values());
    }

    private static VillagerTradeEntry createEntryFromOffers(VillagerProfession profession, int level, List<MerchantOffer> offers) {
        List<ItemStack> inputAOptions = new ArrayList<>(offers.size());
        List<ItemStack> inputBOptions = new ArrayList<>(offers.size());
        List<ItemStack> outputOptions = new ArrayList<>(offers.size());

        for (MerchantOffer offer : offers) {
            inputAOptions.add(offer.getBaseCostA());
            if (!offer.getCostB().isEmpty()) {
                inputBOptions.add(offer.getCostB());
            }
            outputOptions.add(offer.getResult());
        }

        return new VillagerTradeEntry(profession, level, inputAOptions, inputBOptions, outputOptions);
    }

    private static String offerKey(MerchantOffer offer) {
        return stackKey(offer.getBaseCostA()) + "->" +
                stackKey(offer.getCostB()) + "->" +
                stackKey(offer.getResult());
    }

    private static String stackKey(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return "empty";
        }

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        String tag = stack.hasTag() && stack.getTag() != null ? stack.getTag().toString() : "";
        return (itemId != null ? itemId.toString() : "unknown") + "|" + tag;
    }

    private static String safeToString(ResourceLocation location) {
        return location != null ? location.toString() : "";
    }

    private static boolean isTradeProfession(ResourceLocation professionId) {
        return professionId != null
                && !professionId.equals(new ResourceLocation("minecraft", "none"))
                && !professionId.equals(new ResourceLocation("minecraft", "nitwit"));
    }
}
