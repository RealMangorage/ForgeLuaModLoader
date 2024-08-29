local module = {}

local modBus = modInfo.modBus

local DRHelper = import("net.minecraftforge.registries.DeferredRegister")
local registries = import("net.minecraft.core.registries.Registries")

local blockClass = import("net.minecraft.world.level.block.Block")
local blockPropertiesClass = import("net.minecraft.world.level.block.state.BlockBehaviour$Properties")

local blocksDR = DRHelper:create(registries.BLOCK, modId)
blocksDR:register(modBus)

function module.init()
    local blockSupplier = function()
        return blockClass.new(blockPropertiesClass:of())
    end

    module.exampleBlock = blocksDR:register(
            "test",
            modCore:createSupplier(blockSupplier)
    )
end

return module