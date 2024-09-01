local registry = "core/registry/"

function initRegistry(id)
    init(_G, require(registry .. id), id)
end

initRegistry("items")
initRegistry("blocks")
initRegistry("block_entity")