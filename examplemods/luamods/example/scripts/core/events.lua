local module = {}

local hooks = lmflCore.hooks

function module.load()
end

function onPlayerTick(event)
    local plr = event.player
    local lvl = event.player:level()

    local pos = plr:blockPosition():below()
    lvl:setBlock(pos, root.blocks.exampleBlock:get():defaultBlockState(), 3)

end

hooks:hookEvent(false, "net.minecraftforge.event.TickEvent$PlayerTickEvent", onPlayerTick)

return module