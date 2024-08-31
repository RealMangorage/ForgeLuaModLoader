package org.mangorage.lfml.core.lua.prototypes;

import org.luaj.vm2.LuaTable;
import org.mangorage.lfml.core.api.IPrototype;

import java.util.function.Supplier;

import static org.mangorage.lfml.core.LFMLUtils.makeReadOnly;

public final class PrototypeHolder implements IPrototype {

    public static PrototypeHolder of(Supplier<LuaTable> prototypeSupplier) {
        return new PrototypeHolder(prototypeSupplier);
    }

    private final LuaTable prototype;
    private final Supplier<LuaTable> prototypeSupplier;

    private PrototypeHolder(Supplier<LuaTable> prototypeSupplier) {
        this.prototype = makeReadOnly(prototypeSupplier.get());
        this.prototypeSupplier = prototypeSupplier;
    }

    @Override
    public LuaTable extend() {
        return prototypeSupplier.get();
    }

    @Override
    public LuaTable get() {
        return prototype;
    }
}
