package net.gotlicked.rsrreforged.content;

import net.gotlicked.rsrreforged.craftingemitter.CraftingEmitterBlockEntity;
import net.gotlicked.rsrreforged.requester.RequesterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class BlockEntities {
    private BlockEntities() {

    }

    public static final BlockEntities INSTANCE = new BlockEntities();

    @Nullable
    private Supplier<BlockEntityType<RequesterBlockEntity>> requester;

    @Nullable
    private Supplier<BlockEntityType<CraftingEmitterBlockEntity>> craftingEmitter;

    public void setRequester(final @Nullable Supplier<BlockEntityType<RequesterBlockEntity>> supplier) {
        this.requester = supplier;
    }

    public void setCraftingEmitter(final @Nullable Supplier<BlockEntityType<CraftingEmitterBlockEntity>> supplier) {
        this.craftingEmitter = supplier;
    }

    public BlockEntityType<RequesterBlockEntity> getRequester() {
        return requireNonNull(requester).get();
    }

    public BlockEntityType<CraftingEmitterBlockEntity> getCraftingEmitter() {
        return requireNonNull(craftingEmitter).get();
    }

}