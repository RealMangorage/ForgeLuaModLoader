package org.mangorage.lfml.core.lua;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.LuajavaLib;
import org.mangorage.lfml.core.LFMLMod;
import org.mangorage.lfml.core.ModInstance;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * lmflCore.hooks:method(args)
 */
public class LuaHooks {

    private final ModInstance modInstance;
    private final ClassLoader classLoader = LFMLMod.class.getClassLoader();

    public LuaHooks(ModInstance modInstance) {
        this.modInstance = modInstance;
    }

    // REGISTRY START
    public <T> DeferredRegister<T> deferredRegistry(String name, String space) {
        DeferredRegister<T> dr = DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(name, space), modInstance.getModInfo().modId());
        dr.register(modInstance.modBus());
        return dr;
    }
    // REGISTRY END


    // BLOCKS START
    public Supplier<Block> createBasicBlock(BlockBehaviour.Properties properties) {
        return () -> new Block(properties);
    }

    public BlockBehaviour.Properties createBlockProperties() {
        return BlockBehaviour.Properties.of();
    }

    public BlockState getBlockState(String modID, String name) {
        return BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modID, name)).defaultBlockState();
    }
    // BLOCKS END

    // ITEMS START
    public Supplier<Item> createBasicItem(Item.Properties properties) {
        return () -> new Item(properties);
    }

    public Supplier<BlockItem> createBlockItem(Supplier<Block> block, Item.Properties properties) {
        return () -> new BlockItem(block.get(), properties);
    }

    public Item.Properties createItemProperties() {
        return new Item.Properties();
    }

    public CreativeModeTab.Builder createCreativeTabBuilder() {
        return CreativeModeTab.builder();
    }

    public Supplier<CreativeModeTab> createCreativeModeTab(CreativeModeTab.Builder builder) {
        return builder::build;
    }

    public Object createGenerator() {
        class generator {
            record ItemHolder(Supplier<Item> itemSupplier) implements ItemLike {
                @Override
                public Item asItem() {
                    return itemSupplier().get();
                }
            }
            private final List<ItemLike> items = new ArrayList<>();

            public generator addItem(Supplier<Item> itemSupplier) {
                items.add(new ItemHolder(itemSupplier));
                return this;
            }

            public CreativeModeTab.DisplayItemsGenerator build() {
                return (a, b) -> {
                    items.forEach(b::accept);
                };
            }
        }
        return new generator();
    }
    // ITEMS END


    // EVENT START
    public void hookEvent(boolean modBus, String eventType, LuaClosure closure) {
        try {
            Class<Event> eventClass = (Class<Event>) Class.forName(eventType);
            IEventBus bus = modBus ? modInstance.modBus() : MinecraftForge.EVENT_BUS;
            bus.addListener(
                    EventPriority.NORMAL,
                    false,
                    eventClass,
                    e -> closure.invoke(LuaValue.varargsOf(new LuaValue[]{CoerceJavaToLua.coerce(e)}))
            );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    // EVENT END

    // HELPERS START
    public Component literal(String string) {
        return Component.literal(string);
    }
    // HELPERS END

}
