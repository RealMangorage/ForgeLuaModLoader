package org.mangorage.lfml.core.lua.prototypes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.mangorage.lfml.core.LFMLUtils;
import org.mangorage.lfml.core.api.IPrototype;
import org.mangorage.lfml.core.api.IPrototypeHolder;
import org.mangorage.lfml.core.api.ITicker;
import org.mangorage.lfml.core.lua.prototypes.PrototypeHolder;
import org.mangorage.lfml.core.lua.prototypes.block_entity.LuaBlockEntityPrototype;

import java.util.function.Supplier;

public class LuaBlockPrototype extends Block implements IPrototypeHolder, EntityBlock {

    public static Supplier<Object> create(Properties properties, LuaFunction moduleFunc) {
        return () -> new LuaBlockPrototype(properties, () -> (LuaTable) moduleFunc.invoke().arg1());
    }

    private final PrototypeHolder prototypeHolder;

    public LuaBlockPrototype(Properties properties, Supplier<LuaTable> prototype) {
        super(properties);
        this.prototypeHolder = PrototypeHolder.of(prototype);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        var func = getPrototypeHolder().get().get("newBlockEntity");
        if (!func.isnil() && func instanceof LuaFunction lf) {
            return (BlockEntity) CoerceLuaToJava.coerce(lf.invoke(LFMLUtils.convertToVarargs(pos, state)).arg1(), BlockEntity.class);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (level, pos, state, entity) -> {
            if (entity instanceof ITicker ticker)
                ticker.tick();
        };
    }

    @Override
    public IPrototype getPrototypeHolder() {
        return prototypeHolder;
    }
}
