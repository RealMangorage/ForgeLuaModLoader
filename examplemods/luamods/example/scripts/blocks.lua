local data = {}

local hooks = lmflCore.hooks
local blocks = hooks:deferredRegistry("minecraft", "block")

data.exampleBlock = blocks:register(
        "test",
        hooks:createBlock(
                hooks:createBlockProperties()
                        :instabreak()
        )
)

function data.getExampleBlock()
    return data.exampleBlock
end

return data