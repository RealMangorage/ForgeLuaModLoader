package org.mangorage.lfml.core.lua.prototypes;

import com.mojang.datafixers.types.Type;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;
import org.mangorage.lfml.core.api.IPrototype;
import org.mangorage.lfml.core.api.IPrototypeHolder;
import org.mangorage.lfml.core.lua.prototypes.block_entity.LuaBlockEntityPrototype;

import java.util.Set;
import java.util.function.Supplier;

public final class LuaBlockEntityType<T extends BlockEntity> extends BlockEntityType<T> implements IPrototypeHolder {
    private final PrototypeHolder prototypeHolder;

    public LuaBlockEntityType(BlockEntitySupplier<? extends T> p_155259_, Set<Block> p_155260_, Type<?> p_155261_, Supplier<LuaTable> luaTableSupplier) {
        super(p_155259_, p_155260_, p_155261_);
        this.prototypeHolder = PrototypeHolder.of(luaTableSupplier);
    }

    @Nullable
    @Override
    public T create(BlockPos p_155265_, BlockState p_155266_) {
        return super.create(p_155265_, p_155266_);
    }

    @Nullable
    @Override
    public T getBlockEntity(BlockGetter p_58950_, BlockPos p_58951_) {
        return super.getBlockEntity(p_58950_, p_58951_);
    }

    @Override
    public boolean isValid(BlockState p_155263_) {
        return super.isValid(p_155263_);
    }

    @Nullable
    @Override
    public Holder.Reference<BlockEntityType<?>> builtInRegistryHolder() {
        return super.builtInRegistryHolder();
    }



    @Override
    public IPrototype getPrototypeHolder() {
        return prototypeHolder;
    }
}
