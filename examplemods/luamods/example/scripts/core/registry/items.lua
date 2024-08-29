local module = {}

local hooks = lfmlCore.hooks
local modBus = lfmlCore.modBus

local RLHelper = hooks:JavaToLua(hooks:getClass("net.minecraft.resources.ResourceLocation"))
local DRHelper = hooks:JavaToLua(hooks:getClass("net.minecraftforge.registries.DeferredRegister"))
local registries = hooks:JavaToLua(hooks:getClass("net.minecraft.core.registries.Registries"))

local itemClass = hooks:JavaToLua(hooks:getClass("net.minecraft.world.item.Item"))
local itemPropertiesClass = hooks:JavaToLua(hooks:getClass("net.minecraft.world.item.Item$Properties"))

local itemsDR = DRHelper:create(registries.ITEM, "example")
itemsDR:register(modBus)

function module.load()

    local exampleBuilder = function(size)
        return function()
            return itemClass.new(itemPropertiesClass.new():stacksTo(size))
        end
    end

    module.exampleItem = itemsDR:register(
           "example",
            hooks:createSupplier(exampleBuilder(2))
    )
    module.exampleItem2 = itemsDR:register(
            "example2",
            hooks:createSupplier(exampleBuilder(16))
    )
end

return module