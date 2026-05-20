package net.gotlicked.rsrreforged.content;

import net.gotlicked.rsrreforged.craftingemitter.CraftingEmitterBlock;
import net.gotlicked.rsrreforged.requester.RequesterBlock;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class Blocks {
    public static final Blocks INSTANCE = new Blocks();
    @Nullable
    private Supplier<RequesterBlock> requester;
    @Nullable
    private Supplier<CraftingEmitterBlock> craftingEmitter;

    private Blocks() {

    }

    public RequesterBlock getRequester() {
        return requireNonNull(requester).get();
    }

    public void setRequester(final @Nullable Supplier<RequesterBlock> requesterBlockSupplier) {
        this.requester = requesterBlockSupplier;
    }

    public CraftingEmitterBlock getCraftingEmitter() {
        return requireNonNull(craftingEmitter).get();
    }

    public void setCraftingEmitter(final @Nullable Supplier<CraftingEmitterBlock> craftingEmitterBlockSupplier) {
        this.craftingEmitter = craftingEmitterBlockSupplier;
    }
}
