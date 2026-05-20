package net.gotlicked.rsrreforged;

import net.gotlicked.rsrreforged.content.Menus;
import net.gotlicked.rsrreforged.craftingemitter.CraftingEmitterScreen;
import net.gotlicked.rsrreforged.requester.RequesterScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = RSRReforgedCommon.MOD_ID, dist = Dist.CLIENT)
public class RSRReforgedNeoforgeClient {

    public RSRReforgedNeoforgeClient(IEventBus modBus) {
        modBus.addListener(RegisterMenuScreensEvent.class, this::onRegisterMenuScreens);
    }

    private void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(Menus.INSTANCE.getRequester(), RequesterScreen::new);
        event.register(Menus.INSTANCE.getCraftingEmitter(), CraftingEmitterScreen::new);
    }
}
