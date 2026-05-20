package net.gotlicked.rsrreforged;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.support.BaseBlockItem;
import com.refinedmods.refinedstorage.fabric.api.RefinedStorageFabricApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.gotlicked.rsrreforged.content.BlockEntities;
import net.gotlicked.rsrreforged.content.Blocks;
import net.gotlicked.rsrreforged.content.Items;
import net.gotlicked.rsrreforged.content.Menus;
import net.gotlicked.rsrreforged.craftingemitter.CraftingEmitterBlock;
import net.gotlicked.rsrreforged.craftingemitter.CraftingEmitterBlockEntity;
import net.gotlicked.rsrreforged.craftingemitter.CraftingEmitterContainerMenu;
import net.gotlicked.rsrreforged.craftingemitter.CraftingEmitterData;
import net.gotlicked.rsrreforged.requester.RequesterBlock;
import net.gotlicked.rsrreforged.requester.RequesterBlockEntity;
import net.gotlicked.rsrreforged.requester.RequesterContainerMenu;
import net.gotlicked.rsrreforged.requester.RequesterData;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.NonNull;

public class RSRReforgedFabric implements ModInitializer {

    private static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(RSRReforgedCommon.MOD_ID, name);
    }

    private static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, id(name));
    }

    private static <B extends FriendlyByteBuf, V> StreamCodec<B, V> streamCodec(
            java.util.function.Supplier<StreamCodec<B, V>> codecSupplier) {
        return new StreamCodec<>() {
            private StreamCodec<B, V> delegate;

            private StreamCodec<B, V> get() {
                if (delegate == null) {
                    delegate = codecSupplier.get();
                }
                return delegate;
            }

            @Override
            public @NonNull V decode(@NonNull B buf) {
                return get().decode(buf);
            }

            @Override
            public void encode(@NonNull B buf, @NonNull V value) {
                get().encode(buf, value);
            }
        };
    }

    @Override
    public void onInitialize() {
        RSRReforgedCommon.LOG.info("Hello Fabric world!");

        registerBlocks();
        registerItems();
        registerBlockEntities();
        registerMenus();
        registerCreativeTab();
        registerCapabilitiesEvent(BlockEntities.INSTANCE.getRequester());
        registerCapabilitiesEvent(BlockEntities.INSTANCE.getCraftingEmitter());
        upgradeDestination();
        RSRReforgedCommon.init();
    }

    public void registerCapabilitiesEvent(
            final BlockEntityType<? extends AbstractNetworkNodeContainerBlockEntity<?>> type) {
        RefinedStorageFabricApi.INSTANCE.getNetworkNodeContainerProviderLookup().registerForBlockEntity(
                (be, dir) -> be.getContainerProvider(),
                type
        );
    }

    private void upgradeDestination() {
        RefinedStorageApi.INSTANCE.getUpgradeRegistry().forDestination(
                RequesterBlockEntity.REQUESTER_DESTINATION).add(
                com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getStackUpgrade(), 4);

    }

    private void registerBlocks() {
        final RequesterBlock requesterBlock = new RequesterBlock();
        Registry.register(BuiltInRegistries.BLOCK, blockKey("requester"), requesterBlock);
        net.gotlicked.rsrreforged.content.Blocks.INSTANCE.setRequester(() -> requesterBlock);

        final CraftingEmitterBlock craftingEmitterBlock = new CraftingEmitterBlock();
        Registry.register(BuiltInRegistries.BLOCK, blockKey("crafting_emitter"), craftingEmitterBlock);
        net.gotlicked.rsrreforged.content.Blocks.INSTANCE.setCraftingEmitter(() -> craftingEmitterBlock);
    }

    private void registerItems() {
        final Identifier requesterId = id("requester");
        final BaseBlockItem requesterItem = new BaseBlockItem(requesterId, net.gotlicked.rsrreforged.content.Blocks.INSTANCE.getRequester());
        Registry.register(BuiltInRegistries.ITEM,
                ResourceKey.create(Registries.ITEM, requesterId), requesterItem);
        Items.INSTANCE.setRequester(() -> requesterItem);

        final Identifier craftingEmitterId = id("crafting_emitter");
        final BaseBlockItem craftingEmitterItem = new BaseBlockItem(craftingEmitterId, net.gotlicked.rsrreforged.content.Blocks.INSTANCE.getCraftingEmitter());
        Registry.register(BuiltInRegistries.ITEM,
                ResourceKey.create(Registries.ITEM, craftingEmitterId), craftingEmitterItem);
        Items.INSTANCE.setCraftingEmitter(() -> craftingEmitterItem);
    }

    private void registerBlockEntities() {
        final BlockEntityType<RequesterBlockEntity> requesterBEType =
                FabricBlockEntityTypeBuilder.create(
                        RequesterBlockEntity::new,
                        net.gotlicked.rsrreforged.content.Blocks.INSTANCE.getRequester()
                ).build();
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("requester"), requesterBEType);
        BlockEntities.INSTANCE.setRequester(() -> requesterBEType);

        final BlockEntityType<CraftingEmitterBlockEntity> craftingEmitterBEType =
                FabricBlockEntityTypeBuilder.create(
                        CraftingEmitterBlockEntity::new,
                        Blocks.INSTANCE.getCraftingEmitter()
                ).build();
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("crafting_emitter"), craftingEmitterBEType);
        BlockEntities.INSTANCE.setCraftingEmitter(() -> craftingEmitterBEType);
    }

    private void registerMenus() {
        final StreamCodec<RegistryFriendlyByteBuf, RequesterData> lazyRequesterCodec =
                streamCodec(() -> RequesterData.STREAM_CODEC);
        final ExtendedMenuType<RequesterContainerMenu, RequesterData> requesterMenuType =
                new ExtendedMenuType<>(
                        RequesterContainerMenu::new,
                        lazyRequesterCodec);
        Registry.register(BuiltInRegistries.MENU, id("requester"), requesterMenuType);
        Menus.INSTANCE.setRequester(() -> requesterMenuType);

        final StreamCodec<RegistryFriendlyByteBuf, CraftingEmitterData> lazyCraftingEmitterCodec =
                streamCodec(() -> CraftingEmitterData.STREAM_CODEC);
        final ExtendedMenuType<CraftingEmitterContainerMenu, CraftingEmitterData> craftingEmitterMenuType =
                new ExtendedMenuType<>(
                        CraftingEmitterContainerMenu::new,
                        lazyCraftingEmitterCodec);
        Registry.register(BuiltInRegistries.MENU, id("crafting_emitter"), craftingEmitterMenuType);
        Menus.INSTANCE.setCraftingEmitter(() -> craftingEmitterMenuType);
    }

    private void registerCreativeTab() {
        final CreativeModeTab tab = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 10)
                .title(Component.translatable("creativetab.rsrreforged.rsrreforgedtab"))
                .icon(() -> new ItemStack(Items.INSTANCE.getRequester()))
                .displayItems((parameters, output) -> {
                    output.accept(Items.INSTANCE.getRequester());
                    output.accept(Items.INSTANCE.getCraftingEmitter());
                })
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
                ResourceKey.create(Registries.CREATIVE_MODE_TAB, id("tab")), tab);
    }
}