package me.owdding.lib.dev

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.serialization.Codec
import me.owdding.lib.DevModule
import me.owdding.lib.MeowddingLib
import me.owdding.lib.events.NewHypixelAlphaDetectedEvent
import me.owdding.lib.generated.CodecUtils
import net.minecraft.util.TriState
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.utils.command.EnumArgument
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send

internal var alphaOverride = TriState.DEFAULT

@DevModule
internal object StorageDataTest {


    fun path(name: String) = "storage_test/${name}_storage_test"

    val NORMAL_STORAGE = MeowddingLib.storage(
        path("normal"),
        defaultData = ::ArrayList,
        codec = CodecUtils.mutableList(Codec.STRING)
    )

    val PROFILE_STORAGE = MeowddingLib.profileStorage(
        path("profile"),
        defaultData = ::LinkedHashMap,
        codec = CodecUtils.map(Codec.STRING, Codec.STRING),
    )

    val FOLDER_STORAGE = MeowddingLib.folderStorage(
        path("folder"),
        codec = Codec.INT,
    )

    @Subscription
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        event.register("meowdding dev storage_test") {
            then("alpha") {
                thenCallback("override_state", EnumArgument<TriState>()) {
                    val state = argument<TriState>("override_state")
                    alphaOverride = state
                }
                thenCallback("trigger_new_alpha") {
                    if (alphaOverride.toBoolean(LocationAPI.onAlpha)) {
                        NewHypixelAlphaDetectedEvent.post(SkyBlockAPI.eventBus)
                    }
                }
            }

            then("normal") {
                thenCallback("add string", StringArgumentType.string()) {
                    NORMAL_STORAGE.get().add(argument("string"))
                    NORMAL_STORAGE.save()
                }
                thenCallback("delete") {
                    NORMAL_STORAGE.delete()
                }
            }
            then("profile") {
                then("set key", StringArgumentType.string()) {
                    thenCallback("value", StringArgumentType.string()) {
                        val map = PROFILE_STORAGE.get() ?: run {
                            Text.of("Profile storage not active!").send()
                            return@thenCallback
                        }
                        map[argument("key")] = argument("value")
                        PROFILE_STORAGE.save()
                    }
                }
            }
            then("folder") {
                thenCallback("add int", IntegerArgumentType.integer()) {
                    FOLDER_STORAGE.add(argument("int"))
                }
                then("set id", StringArgumentType.string()) {
                    thenCallback("int", IntegerArgumentType.integer()) {
                        FOLDER_STORAGE.set(argument("id"), argument("int"))
                    }
                }
                thenCallback("remove id", StringArgumentType.string()) {
                    FOLDER_STORAGE.remove(argument("id"))
                }
                thenCallback("get id", StringArgumentType.string()) {
                    val id = argument<String>("id")
                    Text.of("Value in folder storage of id $id is ${FOLDER_STORAGE.get(id)}").send()
                }
                thenCallback("list") {
                    Text.of(FOLDER_STORAGE.getAll().toString()).send()
                }
            }
        }
    }

}
