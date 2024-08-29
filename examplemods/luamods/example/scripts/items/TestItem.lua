local item = {}

local Blocks = import("net.minecraft.world.level.block.Blocks")

item.create = function()
    local data = {}

    data.block = Blocks.IRON_BLOCK
    data.useOnContext = function(c)
        local lvl = c:getLevel()
        if not lvl.isClientSide then
            lvl:setBlock(c:getClickedPos(), data.block:defaultBlockState(), 3)
        end
        return true
    end

    return data
end

return item