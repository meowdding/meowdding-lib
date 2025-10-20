package me.owdding.lib.cosmetics

import com.google.common.hash.Hashing
import com.mojang.blaze3d.platform.NativeImage
import com.teamresourceful.resourcefullib.common.utils.files.GlobalStorage
import earth.terrarium.olympus.client.images.ImageProvider
import earth.terrarium.olympus.client.images.ImageProviders
import me.owdding.lib.MeowddingLib
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.time.Duration
import java.util.concurrent.CompletableFuture
import kotlin.io.path.exists
import kotlin.io.path.readBytes

private val HTTP = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()
private val CACHE: Path = GlobalStorage.getCacheDirectory(MeowddingLib.MOD_ID).resolve("cosmetics").resolve("textures")
internal val CosmeticImageProvider: ImageProvider<URI> = ImageProviders.register(
    "mlib_cosmetic_url",
    ::fetchImage,
    { url -> Hashing.sha256().hashUnencodedChars(url.toString()) },
    Duration.ofHours(1),
)

private fun fetchImage(url: URI): CompletableFuture<NativeImage> {
    val file = CACHE.resolve(Hashing.sha256().hashUnencodedChars(url.toString()).toString())
    if (file.exists()) {
        return CompletableFuture.supplyAsync { NativeImage.read(file.readBytes()) }
    } else {
        return HTTP.sendAsync(HttpRequest.newBuilder(url).build(), HttpResponse.BodyHandlers.ofInputStream()).thenApply { response ->
            if (response.statusCode() in 200..299) {
                try {
                    NativeImage.read(response.body())
                } catch (e: Exception) {
                    MeowddingLib.error("Failed to read image!", e)
                    throw RuntimeException("Failed to read image from URL: $url", e)
                }
            } else {
                throw RuntimeException("Failed to fetch image from URL: $url, Status Code: ${response.statusCode()}")
            }
        }
    }
}
