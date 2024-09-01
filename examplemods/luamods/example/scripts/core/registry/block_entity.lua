local DeferredRegister = import("net.minecraftforge.registries.DeferredRegister")
local Registries = import("net.minecraft.core.registries.Registries")
local BlockEntityPrototype = import("org.mangorage.lfml.core.lua.prototypes.block_entity.LuaBlockEntityPrototype")
local Blocks = import("BlockList")

local BLOCK_ENTITY = DeferredRegister:create(Registries.BLOCK_ENTITY_TYPE, mod.modId)
BLOCK_ENTITY:register(mod.modBus)

function createPrototype()
    return {
        tick = function(data, entity)
            if data.ticks == nil then
                data.ticks = 0
            end
            data.ticks = data.ticks + 1

            local lvl = entity:getLevel()
            local pos = entity:getBlockPos():above()

            if data.ticks % 20 == 0 then
                lvl:setBlock(pos, Blocks.COBBLESTONE:defaultBlockState(), 3)
            end
        end
    }
end

local types = {}

types.example = BLOCK_ENTITY:register(
        "example",
        BlockEntityPrototype:create(
                function()
                    return {
                        mod.modules.blocks.example:get()
                    }
                end,
                function()
                    return types.example:get()
                end,
                createPrototype
        )
)

return types