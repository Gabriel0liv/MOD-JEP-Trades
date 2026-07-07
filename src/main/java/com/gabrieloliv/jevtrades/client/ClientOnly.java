package com.gabrieloliv.jevtrades.client;

import com.gabrieloliv.jevtrades.trade.VillagerTradeCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

public final class ClientOnly {
    private ClientOnly() {
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, VillagerTradeCollector::collect);
    }
}
