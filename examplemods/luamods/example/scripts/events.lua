local hooks = lmflCore.hooks

function onPlayerTick(event)
    local plr = event.player
    local lvl = event.player:level()

    local pos = plr:blockPosition():below(2)
    lvl:setBlock(pos, hooks:getBlockState("minecraft", "stone"), 3)

end

hooks:hookEvent(false, "net.minecraftforge.event.TickEvent$PlayerTickEvent", onPlayerTick)