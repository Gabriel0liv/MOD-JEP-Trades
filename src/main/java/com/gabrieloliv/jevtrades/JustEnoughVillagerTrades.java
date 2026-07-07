package com.gabrieloliv.jevtrades;

import com.gabrieloliv.jevtrades.client.ClientOnly;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.DistExecutor;

@Mod(Constants.MOD_ID)
public class JustEnoughVillagerTrades {
    public JustEnoughVillagerTrades() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> "client-only", (remoteVersion, isServer) -> true));

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientOnly::init);
    }
}
