local module = {}

local hooks = lmflCore.hooks
local itemRegistry = hooks:createResourceLocation("minecraft", "item");
local tabs = hooks:deferredRegistry("minecraft", "creative_mode_tab")


function module.load()
    local acceptItemLike = hooks:getMethod(
            "net.minecraft.world.item.CreativeModeTab$Output",
            "accept",
            {
                "net.minecraft.world.level.ItemLike"
            }
    )

    local gg = hooks:wrap("net.minecraft.world.item.CreativeModeTab$DisplayItemsGenerator", function(a, b)
        acceptItemLike:invoke(
                b,
                {
                    root.items.exampleItem:get()
                }
        )
        acceptItemLike:invoke(
                b,
                {
                    hooks:getRegistryObject(
                            itemRegistry,
                            hooks:createResourceLocation("minecraft", "stone")
                    ):get()
                }
        )
        return nil
    end)


    tabs:register(
            "example",
            hooks:createSupplier(
                    function()
                        return hooks:createCreativeModeTabBuilder()
                                :title(hooks:literal("Test"))
                                :icon(hooks:createSupplier(
                                function()
                                    return root.items.exampleItem:get():getDefaultInstance()
                                end))
                                :displayItems(gg)
                                :build()
                    end
            )
    )
end

return module