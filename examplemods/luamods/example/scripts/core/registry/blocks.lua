local DeferredRegister = import("net.minecraftforge.registries.DeferredRegister")
local Registries = import("net.minecraft.core.registries.Registries")
local BlockPrototype = import("org.mangorage.lfml.core.lua.prototypes.block.LuaBlockPrototype")
local BlockProperties = import("net.minecraft.world.level.block.state.BlockBehaviour$Properties")
local BlockEntityPrototype = import("org.mangorage.lfml.core.lua.prototypes.block_entity.LuaBlockEntityPrototype")

local BLOCKS = DeferredRegister:create(Registries.BLOCK, mod.modId)
BLOCKS:register(mod.modBus)

function createPrototype()
    return {
        newBlockEntity = function(pos, state)
            return mod.modules.block_entity.example:get():create(pos, state)
        end
    }
end

local blocks = {}

blocks.example = BLOCKS:register(
        "example",
        BlockPrototype:create(
                BlockProperties:of(),
                createPrototype
        )
)

return blocks


