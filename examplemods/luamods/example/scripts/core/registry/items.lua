local module = {}

local hooks = lmflCore.hooks
local itemsDR = hooks:deferredRegistry("minecraft", "item")

function module.load()
   module.exampleItem = itemsDR:register(
           "example",
           hooks:createBlockItem(
                   root.blocks.exampleBlock,
                   hooks:createItemProperties()
           )
   )
end

return module