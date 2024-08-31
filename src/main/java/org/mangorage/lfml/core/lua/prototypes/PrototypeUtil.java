package org.mangorage.lfml.core.lua.prototypes;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.mangorage.lfml.core.api.ReturnResult;
import org.mangorage.lfml.core.api.ReturnType;

public class PrototypeUtil {
    public static <T> ReturnResult<T> getResult(LuaValue luaValue) {
        if (luaValue.istable()) {
            return new ReturnResult<>(
                    (ReturnType) CoerceLuaToJava.coerce(luaValue.get(1), ReturnType.class),
                    (T) CoerceLuaToJava.coerce(luaValue.get(2), Object.class)
            );
        }

        return ReturnResult.empty();
    }
}
