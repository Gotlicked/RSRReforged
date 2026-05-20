package net.gotlicked.rsrreforged;

import net.fabricmc.api.ClientModInitializer;
import net.gotlicked.rsrreforged.content.Menus;
import net.gotlicked.rsrreforged.craftingemitter.CraftingEmitterScreen;
import net.gotlicked.rsrreforged.requester.RequesterScreen;
import net.minecraft.client.gui.screens.MenuScreens;

public class RSRReforgedFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(Menus.INSTANCE.getRequester(), RequesterScreen::new);
        MenuScreens.register(Menus.INSTANCE.getCraftingEmitter(), CraftingEmitterScreen::new);
    }
}
