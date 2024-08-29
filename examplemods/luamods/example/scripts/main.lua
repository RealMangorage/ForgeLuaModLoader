local module = {}

function requireAndLoad(path, id)
    local moduleToLoad = require(path .. id)
    module[id] = moduleToLoad
    moduleToLoad.init()
end

function import(class)
    return modCore:JavaToLua(modCore:getClass(class))
end


_G.import = import
_G.root = module

requireAndLoad("core/registry/", "blocks")
requireAndLoad("core/registry/", "items")
requireAndLoad("core/registry/", "creativetab")
requireAndLoad("core/", "events")
