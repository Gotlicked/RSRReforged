package net.gotlicked.rsrreforged.content;

import net.gotlicked.rsrreforged.craftingemitter.CraftingEmitterContainerMenu;
import net.gotlicked.rsrreforged.requester.RequesterContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class Menus {
    public static final Menus INSTANCE = new Menus();
    @Nullable
    private Supplier<MenuType<CraftingEmitterContainerMenu>> craftingEmitter;
    @Nullable
    private Supplier<MenuType<RequesterContainerMenu>> requester;

    private Menus() {
    }

    public MenuType<CraftingEmitterContainerMenu> getCraftingEmitter() {
        return requireNonNull(craftingEmitter).get();
    }

    public void setCraftingEmitter(final @Nullable Supplier<MenuType<CraftingEmitterContainerMenu>> supplier) {
        this.craftingEmitter = supplier;
    }

    public MenuType<RequesterContainerMenu> getRequester() {
        return requireNonNull(requester).get();
    }

    public void setRequester(final @Nullable Supplier<MenuType<RequesterContainerMenu>> supplier) {
        this.requester = supplier;
    }

}
