package org.mangorage.lfml.core.api;

import org.luaj.vm2.LuaTable;

import java.util.function.Supplier;

public interface IPrototype {
    LuaTable extend();
    LuaTable get();
}
