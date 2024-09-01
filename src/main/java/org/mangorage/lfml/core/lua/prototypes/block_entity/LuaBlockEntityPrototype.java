package org.mangorage.lfml.core.lua.prototypes.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.mangorage.lfml.core.LFMLUtils;
import org.mangorage.lfml.core.api.IPrototype;
import org.mangorage.lfml.core.api.IPrototypeHolder;
import org.mangorage.lfml.core.api.ITicker;
import org.mangorage.lfml.core.lua.prototypes.PrototypeHolder;
import java.util.function.Supplier;

public class LuaBlockEntityPrototype extends BlockEntity implements ITicker, IPrototypeHolder {
    public static LuaFunction function;

    public static Supplier<Object> create(LuaFunction blocksFunction, LuaFunction blockTypeGetter, LuaFunction moduleFunc) {
        function = blocksFunction;
        return () -> BlockEntityType.Builder.of(
                (pos, state) -> new LuaBlockEntityPrototype((BlockEntityType<?>) CoerceLuaToJava.coerce(blockTypeGetter.invoke().arg1(), BlockEntityType.class), pos, state, () -> (LuaTable) moduleFunc.invoke()),
                LFMLUtils.extractJavaObjects((LuaTable) blocksFunction.invoke().arg1())
                        .stream()
                        .filter(o -> o instanceof Block)
                        .map(o -> (Block) o)
                        .toArray(Block[]::new)
        ).build(null);
    }

    private final PrototypeHolder prototypeHolder;

    public LuaBlockEntityPrototype(BlockEntityType<?> type, BlockPos blockPos, BlockState state, Supplier<LuaTable> prototype) {
        super(type, blockPos, state);
        this.prototypeHolder = PrototypeHolder.of(prototype);
    }

    @Override
    public IPrototype getPrototypeHolder() {
        return prototypeHolder;
    }

    @Override
    public void tick() {
        var func = prototypeHolder.get().get("tick");
        if (!func.isnil())
            func.invoke(LFMLUtils.convertToVarargs(prototypeHolder.get(), this));
    }
}
