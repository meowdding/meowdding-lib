package me.owdding.lib.repo

import me.owdding.ktcodecs.GenerateCodec

object SacksRepoData {

    public

    @GenerateCodec
    data class Entry(
        val sack: String,
        val items: List<String>,
    )

}
