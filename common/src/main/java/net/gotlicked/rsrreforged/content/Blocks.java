package net.gotlicked.rsrreforged.content;

import net.gotlicked.rsrreforged.craftingemitter.CraftingEmitterBlock;
import net.gotlicked.rsrreforged.requester.RequesterBlock;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class Blocks {
    private Blocks() {

    }

    @Nullable
    private Supplier<RequesterBlock> requester;

    @Nullable
    private Supplier<CraftingEmitterBlock> craftingEmitter;

    public void setRequester(final @Nullable Supplier<RequesterBlock> requesterBlockSupplier) {
        this.requester = requesterBlockSupplier;
    }

    public void setCraftingEmitter(final @Nullable Supplier<CraftingEmitterBlock> craftingEmitterBlockSupplier) {
        this.craftingEmitter = craftingEmitterBlockSupplier;
    }

    public RequesterBlock getRequester() {
        return requireNonNull(requester).get();
    }

    public CraftingEmitterBlock getCraftingEmitter() {
        return requireNonNull(craftingEmitter).get();
    }
}
