package net.gotlicked.rsrreforged.requester;

import com.refinedmods.refinedstorage.api.network.impl.node.iface.InterfaceTransferResult;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeDestination;
import com.refinedmods.refinedstorage.common.support.FilterWithFuzzyMode;
import com.refinedmods.refinedstorage.common.support.containermenu.NetworkNodeExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicator;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicators;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.support.network.SimpleConnectionStrategy;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerImpl;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import net.gotlicked.rsrreforged.content.BlockEntities;
import net.gotlicked.rsrreforged.content.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class RequesterBlockEntity extends AbstractBaseNetworkNodeContainerBlockEntity<RequesterNetworkNode>
        implements NetworkNodeExtendedMenuProvider<RequesterData> {

    public static final UpgradeDestination REQUESTER_DESTINATION = new UpgradeDestination() {
        @Override
        public @NonNull Component getName() {
            return Component.translatable("block.rsrreforged.requester");
        }

        @Override
        public @NonNull ItemStack getStackRepresentation() {
            return new ItemStack(Items.INSTANCE.getRequester());
        }
    };
    private static final int EXPORT_SLOTS = 9;
    private final FilterWithFuzzyMode filter;
    private final UpgradeContainer upgradeContainer;

    public RequesterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.INSTANCE.getRequester(), pos, state,
                new RequesterNetworkNode(16));
        this.filter = FilterWithFuzzyMode.create(
                createFilterContainer(),
                this::setChanged);
        this.upgradeContainer = new UpgradeContainer(REQUESTER_DESTINATION);
        this.mainNetworkNode.setFilter(this.filter);
        this.mainNetworkNode.setUpgradeContainer(this.upgradeContainer);
    }

    public static ResourceContainer createFilterContainer() {
        return new ResourceContainerImpl(
                EXPORT_SLOTS, _ -> Integer.MAX_VALUE,
                RefinedStorageApi.INSTANCE.getItemResourceFactory(),
                RefinedStorageApi.INSTANCE.getAlternativeResourceFactories());
    }

    public static ResourceContainer createFilterContainer(final RequesterData interfaceData) {
        final ResourceContainer filterContainer = createFilterContainer();
        final ResourceContainerData resourceContainerData = interfaceData.filterContainerData();
        for (
                int i = 0; i < resourceContainerData.resources().size(); ++i) {
            final int ii = i;
            resourceContainerData.resources().get(i).ifPresent(
                    resource -> filterContainer.set(
                            ii, resource));
        }
        return filterContainer;
    }

    @Override
    public void setLevel(@NonNull Level level) {
        super.setLevel(level);
        this.mainNetworkNode.setLevel(level);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(
            final int syncId, final @NonNull Inventory inventory,
            final @NonNull Player player) {
        return new RequesterContainerMenu(
                syncId, player, this, filter.getFilterContainer(), upgradeContainer,
                getExportingIndicators());
    }


    @Override
    public @NonNull RequesterData getMenuData() {
        return new RequesterData(
                ResourceContainerData.of(filter.getFilterContainer()),
                getExportingIndicators().getAll());
    }

    @Override
    public @NonNull StreamEncoder<RegistryFriendlyByteBuf, RequesterData> getMenuCodec() {
        return RequesterData.STREAM_CODEC;
    }


    private ExportingIndicators getExportingIndicators() {
        return new ExportingIndicators(
                filter.getFilterContainer(), i -> toExportingIndicator(
                mainNetworkNode.getLastResult(i)),
                true);
    }

    private ExportingIndicator toExportingIndicator(
            @Nullable final InterfaceTransferResult result) {
        return switch (result) {
            case STORAGE_DOES_NOT_ACCEPT_RESOURCE -> ExportingIndicator.DESTINATION_DOES_NOT_ACCEPT_RESOURCE;
            case RESOURCE_MISSING -> ExportingIndicator.RESOURCE_MISSING;
            case AUTOCRAFTING_STARTED -> ExportingIndicator.AUTOCRAFTING_WAS_STARTED;
            case AUTOCRAFTING_MISSING_RESOURCES -> ExportingIndicator.AUTOCRAFTING_MISSING_RESOURCES;
            case null, default -> ExportingIndicator.NONE;
        };
    }

    @Override
    public @NonNull Component getName() {
        return Component.translatable("block.rsrreforged.requester");
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    protected @NonNull InWorldNetworkNodeContainer createMainContainer(@NonNull RequesterNetworkNode networkNode) {
        return RefinedStorageApi.INSTANCE.createNetworkNodeContainer(
                        this, networkNode).connectionStrategy(
                        new SimpleConnectionStrategy(
                                getBlockPos()))
                .build();
    }

    @Override
    public void writeConfiguration(final @NonNull ValueOutput output) {
        super.writeConfiguration(output);
        filter.store(output);
    }

    @Override
    public void readConfiguration(final @NonNull ValueInput input) {
        super.readConfiguration(input);
        filter.read(input);
    }
}