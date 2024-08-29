local module = {}

function requireAndLoad(path, id)
    local moduleToLoad = require(path .. id)
    module[id] = moduleToLoad
    moduleToLoad.load()
end

_G.root = module

requireAndLoad("core/registry/", "blocks")
requireAndLoad("core/registry/", "items")
requireAndLoad("core/registry/", "creativetab")
requireAndLoad("core/", "events")

