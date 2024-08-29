package org.mangorage.lfml.core.lua.prototypes;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.mangorage.lfml.core.api.IPrototype;

import java.util.function.Supplier;

public class ItemPrototype extends Item implements IPrototype {
    private final Supplier<LuaTable> moduleConstructor;
    private final LuaTable module;

    public ItemPrototype(Properties properties, LuaTable module, Supplier<LuaTable> moduleSupplier) {
        super(properties);
        this.moduleConstructor = moduleSupplier;
        this.module = module;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var result = CoerceLuaToJava.coerce(
                getOrThrow("useOnContext").invoke(CoerceJavaToLua.coerce(context)).arg1(),
                Object.class
        );

        if (result instanceof Boolean)
            return super.useOn(context);
        if (result instanceof InteractionResult ir)
            return ir;
        throw new IllegalStateException("Did not pass thru either boolean or InteractionResult");
    }

    private LuaFunction getOrThrow(String funcId) {
        var lv = getProtoTypeImpl().get(funcId);
        if (lv instanceof LuaFunction lf)
            return lf;
        throw new IllegalStateException("funcId %s not a registered function".formatted(funcId));
    }

    @Override
    public LuaTable getProtoTypeImpl() {
        return module;
    }

    @Override
    public LuaTable copyProtoTypeImpl() {
        return moduleConstructor.get();
    }

    @Override
    public Supplier<LuaTable> getCtor() {
        return moduleConstructor;
    }
}
