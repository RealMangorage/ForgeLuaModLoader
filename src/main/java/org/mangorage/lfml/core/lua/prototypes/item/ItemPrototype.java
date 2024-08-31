package org.mangorage.lfml.core.lua.prototypes.item;


import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.mangorage.lfml.core.LFMLUtils;
import org.mangorage.lfml.core.api.IPrototype;
import org.mangorage.lfml.core.api.IPrototypeHolder;
import org.mangorage.lfml.core.api.ReturnResult;
import org.mangorage.lfml.core.api.annotations.HasBefore;
import org.mangorage.lfml.core.lua.prototypes.PrototypeHolder;
import org.mangorage.lfml.core.lua.prototypes.PrototypeUtil;

import java.util.Optional;
import java.util.function.Supplier;

public class ItemPrototype extends Item implements IPrototypeHolder {

    public static Supplier<Object> create(Properties properties, LuaFunction moduleFunc) {
        return () -> new ItemPrototype(properties, () -> (LuaTable) moduleFunc.invoke().arg1());
    }

    private final PrototypeHolder prototypeHolder;

    public ItemPrototype(Properties properties, Supplier<LuaTable> moduleSupplier) {
        super(properties);
        this.prototypeHolder = PrototypeHolder.of(moduleSupplier);
    }

    @HasBefore
    @Override
    public InteractionResult useOn(UseOnContext context) {
        var beforeResult = ofBefore("useOnContext");
        if (!beforeResult.isPresent()) return super.useOn(context);
        var resultFunc = beforeResult.get();
        ReturnResult<InteractionResult> returnResult = PrototypeUtil.getResult(resultFunc.invoke(LFMLUtils.convertToVarargs(getPrototypeHolder().get(), context)).arg1());
        switch (returnResult.returnType()) {
            case SUPER_OVERRIDE -> {
                return returnResult.returnObject();
            }
            case SUPER_NORMAL -> {
                return super.useOn(context);
            }
            default -> throw new IllegalStateException("Need SUPER_OVERRIDE or SUPER_NORMAL");
        }
    }

    private Optional<LuaFunction> ofBefore(String funcId) {
        var result = getPrototypeHolder().get().get("before");
        if (result != null && result instanceof LuaTable table)
            return of(table, funcId);
        return Optional.empty();
    }

    private Optional<LuaFunction> ofAfter(String funcId) {
        var result = getPrototypeHolder().get().get("after");
        if (result != null && result instanceof LuaTable table)
            return of(table, funcId);
        return Optional.empty();
    }

    private Optional<LuaFunction> of(LuaTable module, String funcId) {
        var result = module.get(funcId);
        if (result instanceof LuaFunction function) {
            return Optional.of(function);
        }
        return Optional.empty();
    }

    @Override
    public IPrototype getPrototypeHolder() {
        return prototypeHolder;
    }
}
