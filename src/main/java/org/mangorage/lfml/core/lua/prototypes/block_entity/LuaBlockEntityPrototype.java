package org.mangorage.lfml.core.lua.prototypes.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.mangorage.lfml.core.LFMLUtils;
import org.mangorage.lfml.core.api.ITicker;
import org.mangorage.lfml.core.lua.prototypes.LuaBlockEntityType;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LuaBlockEntityPrototype extends BlockEntity implements ITicker {

    public static Supplier<Object> create(LuaFunction blocksFunction, LuaFunction blockTypeGetter, LuaFunction moduleFunc) {
        return () -> {
            return new LuaBlockEntityType<>(
                    (pos, state) -> new LuaBlockEntityPrototype((LuaBlockEntityType<?>) CoerceLuaToJava.coerce(blockTypeGetter.invoke().arg1(), LuaBlockEntityType.class), pos, state),
                    LFMLUtils.extractJavaObjects((LuaTable) blocksFunction.invoke().arg1())
                            .stream()
                            .filter(o -> o instanceof Block)
                            .map(o -> (Block) o)
                            .collect(Collectors.toSet()),
                    null,
                    () -> (LuaTable) moduleFunc.invoke().arg1()
            );
        };
    }

    private final LuaBlockEntityType<?> type;
    private final LuaTable dataTable = new LuaTable();

    public LuaBlockEntityPrototype(LuaBlockEntityType<?> type, BlockPos blockPos, BlockState state) {
        super(type, blockPos, state);
        this.type = type;
    }


    @Override
    public void tick() {
        var func = type.getPrototypeHolder().get().get("tick");
        if (!func.isnil())
            func.invoke(LFMLUtils.convertToVarargs(type.getPrototypeHolder().get(), dataTable, this));
    }
}
