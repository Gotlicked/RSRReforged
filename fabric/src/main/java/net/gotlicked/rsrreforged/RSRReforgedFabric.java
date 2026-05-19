package net.gotlicked.rsrreforged;

import net.fabricmc.api.ModInitializer;

public class RSRReforgedFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {

        Constants.LOG.info("Hello Fabric world!");
        RSRReforgedCommon.init();
    }
}
