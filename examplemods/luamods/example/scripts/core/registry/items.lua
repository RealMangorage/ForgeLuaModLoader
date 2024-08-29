local module = {}

local modBus = modInfo.modBus

local DRHelper = import("net.minecraftforge.registries.DeferredRegister")
local registries = import("net.minecraft.core.registries.Registries")
local Blocks = import("net.minecraft.world.level.block.Blocks")

local itemClass = import("org.mangorage.lfml.core.lua.prototypes.ItemPrototype")
local itemPropertiesClass = import("net.minecraft.world.item.Item$Properties")

local itemsDR = DRHelper:create(registries.ITEM, modId)
itemsDR:register(modBus)

function module.init()
    local TestItemProto = require("items/TestItem")
    local examplePrototype = function()
        return TestItemProto:create()
    end

    local exampleBuilder = function(size, extend)
        return function()
            if not extend then
                return itemClass.new(itemPropertiesClass.new():stacksTo(size), examplePrototype(), modCore:createSupplier(examplePrototype))
            else
                local prototype = root.items.exampleItem:get()
                local table = prototype:copyProtoTypeImpl()
                table.block = Blocks.DIAMOND_BLOCK
                print(table.block)
                return itemClass.new(itemPropertiesClass.new():stacksTo(size), table, prototype:getCtor())
            end
        end
    end

    module.exampleItem = itemsDR:register(
           "example",
            modCore:createSupplier(exampleBuilder(2, false))
    )
    module.exampleItem2 = itemsDR:register(
            "example2",
            modCore:createSupplier(exampleBuilder(16, true))
    )
end

return module