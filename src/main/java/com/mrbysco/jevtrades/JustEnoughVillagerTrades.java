package com.mrbysco.jevtrades;

import com.mrbysco.jevtrades.trade.VillagerTradeCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class JustEnoughVillagerTrades {
    public JustEnoughVillagerTrades() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> "client-only", (remoteVersion, isServer) -> true));

        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, VillagerTradeCollector::collect);
    }
}
