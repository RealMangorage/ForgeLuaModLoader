local DeferredRegister = import("net.minecraftforge.registries.DeferredRegister")
local Registries = import("net.minecraft.core.registries.Registries")
local ItemPrototype = import("org.mangorage.lfml.core.lua.prototypes.item.ItemPrototype")
local ItemProperties = import("net.minecraft.world.item.Item$Properties")
local MC_BLOCKS = import("BlockList")
local ReturnType = import("org.mangorage.lfml.core.api.ReturnType")

local ITEMS = DeferredRegister:create(Registries.ITEM, mod.modId)

print(mod.modBus)
ITEMS:register(mod.modBus)

function createPrototype()
    return {
        properties = {
            replaceBlock = MC_BLOCKS.GOLD_BLOCK
        },
        before = {
            useOnContext = function(self, context)
                context:getLevel():setBlock(context:getClickedPos(), self.properties.replaceBlock:defaultBlockState(), 3)
                return {
                    ReturnType.SUPER_NORMAL,
                    nil
                }
            end
        }
    }
end

local items = {}

function createOverriddenPrototype()
    local prototype = mod.modules.items.example:get():getPrototypeHolder():extend()
    prototype.properties.replaceBlock = MC_BLOCKS.NETHERITE_BLOCK
    return prototype
end

items.example = ITEMS:register(
        "example",
        ItemPrototype:create(
                ItemProperties.new(),
                createPrototype
        )
)

items.example2 = ITEMS:register(
        "example2",
        ItemPrototype:create(
                ItemProperties.new(),
                createOverriddenPrototype
        )
)

return items