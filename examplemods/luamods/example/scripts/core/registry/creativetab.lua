local module = {}

local modBus = modInfo.modBus

local DRHelper = import("net.minecraftforge.registries.DeferredRegister")
local registries = import("net.minecraft.core.registries.Registries")

local Items = import("net.minecraft.world.item.Items")

local CreativeModeTabClass = import("net.minecraft.world.item.CreativeModeTab")
local ComponentClass = import("net.minecraft.network.chat.Component")

local creativeTabDR = DRHelper:create(registries.CREATIVE_MODE_TAB, modId)
creativeTabDR:register(modBus)

function module.init()
    local display = function(a, b)
        b:accept(root.items.exampleItem:get())
    end

    local icon = function()
        return Items.IRON_INGOT:getDefaultInstance()
    end

    creativeTabDR:register(
            "example",
            modCore:createSupplier(
                    function()
                        return CreativeModeTabClass:builder()
                                :title(ComponentClass:literal("TEST"))
                                :icon(modCore:createSupplier(icon))
                                :displayItems(modCore:wrap( "net.minecraft.world.item.CreativeModeTab$DisplayItemsGenerator", display))
                                :build()
                    end
            )
    )
end

return module