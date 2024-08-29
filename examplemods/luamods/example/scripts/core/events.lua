local module = {}

local Blocks = import("net.minecraft.world.level.block.Blocks")
local MCF = import("net.minecraftforge.common.MinecraftForge")

function module.init()
    reload()
end

function onPlayerTick(event)
    local plr = event.player
    local lvl = event.player:level()

    local pos = plr:blockPosition():below()
    lvl:setBlock(pos, Blocks.IRON_BLOCK:defaultBlockState(), 3)
end

modCore:hookEvent(MCF.EVENT_BUS, "net.minecraftforge.event.TickEvent$PlayerTickEvent", onPlayerTick)

return module