plugins {
    id("net.fabricmc.fabric-loom-remap")
    `mlib-setup`
}


dependencies {
    mappings(loom.layered {
        officialMojangMappings()
        parchment(variantOf(versionedCatalog["parchment"]) {
            artifactType("zip")
        })
    })
}
