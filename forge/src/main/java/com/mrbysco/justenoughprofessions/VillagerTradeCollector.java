package com.mrbysco.justenoughprofessions;

import com.mrbysco.justenoughprofessions.jei.VillagerTradeEntry;
import com.mrbysco.justenoughprofessions.jei.VillagerTradeWrapper;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VillagerTradeCollector {
	private static final Map<Integer, List<VillagerTradeEntry>> TRADES_BY_LEVEL = new HashMap<>();

	static {
		for (int level = 1; level <= 5; level++) {
			TRADES_BY_LEVEL.put(level, new ArrayList<>());
		}
	}

	public static void collect(VillagerTradesEvent event) {
		VillagerProfession profession = event.getType();
		Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		Entity trader = createDummyVillager(profession);

		for (int level = 1; level <= 5; level++) {
			List<VillagerTrades.ItemListing> listings = trades.get(level);
			if (listings == null || listings.isEmpty()) {
				continue;
			}

			for (VillagerTrades.ItemListing listing : listings) {
				MerchantOffer offer = createOffer(profession, level, listing, trader);
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
		List<VillagerTradeEntry> trades = TRADES_BY_LEVEL.getOrDefault(level, Collections.emptyList());
		List<VillagerTradeWrapper> wrappers = new ArrayList<>(trades.size());
		for (VillagerTradeEntry entry : trades) {
			wrappers.add(new VillagerTradeWrapper(entry));
		}
		return wrappers;
	}

	private static MerchantOffer createOffer(VillagerProfession profession, int level, VillagerTrades.ItemListing listing, Entity trader) {
		try {
			return listing.getOffer(trader, RandomSource.create(0L));
		} catch (Exception exception) {
			Constants.LOG.warn("Unable to resolve villager trade for profession {} level {}",
					ForgeRegistries.VILLAGER_PROFESSIONS.getKey(profession), level, exception);
			return null;
		}
	}

	private static Entity createDummyVillager(VillagerProfession profession) {
		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return null;
		}
		Villager villager = EntityType.VILLAGER.create(level);
		if (villager != null) {
			villager.setVillagerData(villager.getVillagerData().setProfession(profession));
		}
		return villager;
	}
}
