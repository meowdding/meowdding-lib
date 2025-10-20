package me.owdding.lib.repo

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import me.owdding.lib.events.FinishRepoLoadingEvent
import me.owdding.lib.generated.MeowddingLibCodecs
import me.owdding.repo.RemoteRepo
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import java.util.concurrent.atomic.AtomicReference

@Module
object SacksRepoData {

    private val _data = AtomicReference<List<SackEntry>>(emptyList())
    val data: List<SackEntry> get() = _data.get()

    @GenerateCodec
    data class SackEntry(
        val sack: String,
        val items: List<String>,
    )

    @Subscription
    fun finishRepoLoading(event: FinishRepoLoadingEvent) {
        _data.setRelease(RemoteRepo.getFileContentAsJson("sacks.json")?.toData(MeowddingLibCodecs.SackEntryCodec.codec().listOf()) ?: return)
    }
}
