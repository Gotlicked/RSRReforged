package net.gotlicked.rsrreforged;


import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.support.BaseBlockItem;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod(RSRReforgedCommon.MOD_ID)
public class RSRReforgedNeoforge {

    private static final DeferredRegister<Block> BLOCK_REGISTER =
            DeferredRegister.create(Registries.BLOCK, RSRReforgedCommon.MOD_ID);
    private static final DeferredRegister<Item> ITEM_REGISTER =
            DeferredRegister.create(Registries.ITEM, RSRReforgedCommon.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RSRReforgedCommon.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENU_REGISTER =
            DeferredRegister.create(Registries.MENU, RSRReforgedCommon.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TAB_REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RSRReforgedCommon.MOD_ID);

    public RSRReforgedNeoforge(IEventBus eventBus) {
        RSRReforgedCommon.LOG.info("Hello NeoForge world!");

        eventBus.addListener(this::registerCapabilitiesEvent);
        eventBus.addListener(this::upgradeDestination);

        registerBlocks();
        registerItems();
        registerBlockEntities();
        registerMenus();
        registerCreativeTab();

        BLOCK_REGISTER.register(eventBus);
        ITEM_REGISTER.register(eventBus);
        BLOCK_ENTITY_REGISTER.register(eventBus);
        MENU_REGISTER.register(eventBus);
        CREATIVE_TAB_REGISTER.register(eventBus);

        RSRReforgedCommon.init();
    }

    public void registerCapabilitiesEvent(RegisterCapabilitiesEvent event) {
        event.registerBlock(
                RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                (_, _, _, blockEntity, _) -> {
                    if (blockEntity instanceof RequesterBlockEntity requesterBlockEntity) {
                        return requesterBlockEntity.getContainerProvider();
                    }
                    return null;
                }, Blocks.INSTANCE.getRequester());
        event.registerBlock(
                RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                (_, _, _, blockEntity, _) -> {
                    if (blockEntity instanceof CraftingEmitterBlockEntity craftingEmitterBlockEntity) {
                        return craftingEmitterBlockEntity.getContainerProvider();
                    }
                    return null;
                }, Blocks.INSTANCE.getCraftingEmitter());
    }

    private void upgradeDestination(FMLCommonSetupEvent event) {

        RefinedStorageApi.INSTANCE.getUpgradeRegistry().forDestination(
                RequesterBlockEntity.REQUESTER_DESTINATION).add(
                com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getStackUpgrade(), 4);

    }

    private void registerBlocks() {
        final Supplier<RequesterBlock> requester =
                BLOCK_REGISTER.register("requester", registryName -> new RequesterBlock());
        Blocks.INSTANCE.setRequester(requester);

        final Supplier<CraftingEmitterBlock> craftingEmitter =
                BLOCK_REGISTER.register("crafting_emitter", registryName -> new CraftingEmitterBlock());
        Blocks.INSTANCE.setCraftingEmitter(craftingEmitter);
    }

    private void registerItems() {
        final Supplier<BaseBlockItem> requesterItem =
                ITEM_REGISTER.register("requester", registryName ->
                        new BaseBlockItem(registryName, Blocks.INSTANCE.getRequester()));
        Items.INSTANCE.setRequester(requesterItem);

        final Supplier<BaseBlockItem> craftingEmitterItem =
                ITEM_REGISTER.register("crafting_emitter", registryName ->
                        new BaseBlockItem(registryName, Blocks.INSTANCE.getCraftingEmitter()));
        Items.INSTANCE.setCraftingEmitter(craftingEmitterItem);
    }

    private void registerBlockEntities() {
        final Supplier<BlockEntityType<RequesterBlockEntity>> requesterBE =
                BLOCK_ENTITY_REGISTER.register("requester",
                        registryName -> new BlockEntityType<>(
                                RequesterBlockEntity::new,
                                false,
                                Blocks.INSTANCE.getRequester()));
        BlockEntities.INSTANCE.setRequester(requesterBE);

        final Supplier<BlockEntityType<CraftingEmitterBlockEntity>> craftingEmitterBE =
                BLOCK_ENTITY_REGISTER.register("crafting_emitter",
                        registryName -> new BlockEntityType<>(
                                CraftingEmitterBlockEntity::new,
                                false,
                                Blocks.INSTANCE.getCraftingEmitter()));
        BlockEntities.INSTANCE.setCraftingEmitter(craftingEmitterBE);
    }

    private void registerMenus() {
        final Supplier<MenuType<RequesterContainerMenu>> requesterMenu =
                MENU_REGISTER.register("requester",
                        registryName -> IMenuTypeExtension.create(
                                (syncId, playerInventory, buf) -> {
                                    RequesterData data = RequesterData.STREAM_CODEC.decode(buf);
                                    return new RequesterContainerMenu(syncId, playerInventory, data);
                                }));
        Menus.INSTANCE.setRequester(requesterMenu);

        final Supplier<MenuType<CraftingEmitterContainerMenu>> craftingEmitterMenu =
                MENU_REGISTER.register("crafting_emitter",
                        registryName -> IMenuTypeExtension.create(
                                (syncId, playerInventory, buf) -> {
                                    CraftingEmitterData data = CraftingEmitterData.STREAM_CODEC.decode(buf);
                                    return new CraftingEmitterContainerMenu(syncId, playerInventory, data);
                                }));
        Menus.INSTANCE.setCraftingEmitter(craftingEmitterMenu);
    }

    private void registerCreativeTab() {
        CREATIVE_TAB_REGISTER.register("tab", () ->
                CreativeModeTab.builder()
                        .title(Component.translatable("creativetab.rsrreforged.rsrreforgedtab"))
                        .icon(() -> new ItemStack(Items.INSTANCE.getRequester()))
                        .displayItems((parameters, output) -> {
                            output.accept(Items.INSTANCE.getRequester());
                            output.accept(Items.INSTANCE.getCraftingEmitter());
                        })
                        .build());
    }
}