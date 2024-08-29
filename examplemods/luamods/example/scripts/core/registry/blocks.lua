local module = {}

local hooks = lmflCore.hooks
local blocks = hooks:deferredRegistry("minecraft", "block")

function module.load()
    module.exampleBlock = blocks:register(
            "test",
            hooks:createBasicBlock(
                    hooks:createBlockProperties()
                         :instabreak()
            )
    )
end

return module