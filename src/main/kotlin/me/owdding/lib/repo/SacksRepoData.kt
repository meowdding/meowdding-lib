package me.owdding.lib.repo

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import me.owdding.lib.MeowddingLib

@Module
object SacksRepoData {
    val data: List<SackEntry> by MeowddingLib.repoDelegate("sacks", emptyList())

    @GenerateCodec
    data class SackEntry(
        val sack: String,
        val items: List<String>,
    )
}
