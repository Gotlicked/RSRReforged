package net.gotlicked.rsrreforged.content;

import com.refinedmods.refinedstorage.common.support.BaseBlockItem;
import net.minecraft.world.item.BlockItem;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class Items {
    private Items() {

    }

    public static final Items INSTANCE = new Items();

    @Nullable
    private Supplier<BaseBlockItem> requester;

    @Nullable
    private Supplier<BaseBlockItem> craftingEmitter;

    public BlockItem getRequester() {
        return requireNonNull(requester).get();
    }

    public BlockItem getCraftingEmitter() {
        return requireNonNull(craftingEmitter).get();
    }

    public void setCraftingEmitter(final @Nullable Supplier<BaseBlockItem> supplier) {
        this.craftingEmitter = supplier;
    }

    public void setRequester(final @Nullable Supplier<BaseBlockItem> supplier) {
        this.requester = supplier;
    }

}
