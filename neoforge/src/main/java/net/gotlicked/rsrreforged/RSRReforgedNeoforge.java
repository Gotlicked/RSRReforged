package net.gotlicked.rsrreforged;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class RSRReforgedNeoforge {

    public RSRReforgedNeoforge(IEventBus eventBus) {

        Constants.LOG.info("Hello NeoForge world!");
        RSRReforgedCommon.init();

    }
}