package tech.thatgravyboat.lib.mixins;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.locale.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.lib.internal.LanguageHelper;
import tech.thatgravyboat.lib.internal.LanguageMetadata;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(Language.class)
public class LanguageMixin {

    @Inject(method = "loadFromJson", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;entrySet()Ljava/util/Set;", remap = false), cancellable = true)
    private static void loadFromJson(
        CallbackInfo ci,
        @Share("meow") LocalRef<LanguageMetadata> metadata,
        @Local JsonObject jsonObject,
        @Local(argsOnly = true) BiConsumer<String, String> output
    ) {
        if (jsonObject.has("@meowdding:metadata")) {
            metadata.set(LanguageMetadata.fromJson(jsonObject.getAsJsonObject("@meowdding:metadata")));
            jsonObject.remove("@meowdding:metadata");
        }

        final LanguageMetadata languageMetadata = metadata.get();
        if (languageMetadata == null || !languageMetadata.getObjectsAsKeys()) {
            return;
        }

        ci.cancel();
        discover(
            jsonObject,
            new LinkedList<>(Stream.of(languageMetadata.getPrefix()).filter(Objects::nonNull).toList()),
            (key, value) ->{
                if (languageMetadata.getAllowTags()) {
                    LanguageHelper.getComponentKeyList().add(key);
                }
                output.accept(key, value);
            },
            languageMetadata);
    }

    @Unique
    private static void discover(JsonObject object, Deque<String> path, BiConsumer<String, String> consumer, LanguageMetadata languageMetadata) {
        for (String s : object.keySet()) {
            final JsonElement jsonElement = object.get(s);
            if (s.equals("@value")) {
                consumer.accept(String.join(".", path), resolve(jsonElement, languageMetadata));
                continue;
            }
            if (s.equals("@id")) {
                consumer.accept(String.join(".", path), resolve(object, languageMetadata));
                continue;
            }
            if (jsonElement.isJsonObject()) {
                path.addLast(s);
                discover(jsonElement.getAsJsonObject(), path, consumer, languageMetadata);
                path.removeLast();
                continue;
            }

            path.addLast(s);
            consumer.accept(String.join(".", path), resolve(jsonElement, languageMetadata));
            path.removeLast();
        }
    }

    @WrapOperation(method = "loadFromJson", at = @At(value = "INVOKE", target = "Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"))
    private static <T, U> void accept(
        BiConsumer<T, U> instance,
        T t,
        U u,
        Operation<Void> original,
        @Share("meow") LocalRef<LanguageMetadata> metadataRef
    ) {
        final LanguageMetadata languageMetadata = metadataRef.get();
        if (languageMetadata == null || languageMetadata.getPrefix() == null) {
            //noinspection MixinExtrasOperationParameters
            original.call(instance, t, u);
            return;
        }
        //noinspection MixinExtrasOperationParameters
        original.call(instance, languageMetadata.getPrefix() + "." + t, u);
    }

    @WrapOperation(method = "loadFromJson", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/GsonHelper;convertToString(Lcom/google/gson/JsonElement;Ljava/lang/String;)Ljava/lang/String;"))
    private static String loadFromJson(
        JsonElement json,
        String key,
        Operation<String> original,
        @Local JsonObject jsonObject,
        @Share("meow") LocalRef<LanguageMetadata> metadataRef
    ) {
        final LanguageMetadata languageMetadata = metadataRef.get();
        if (languageMetadata == null) {
            return original.call(json, key);
        }

        if (languageMetadata.getAllowTags()) {
            LanguageHelper.getComponentKeyList().add(languageMetadata.getPrefix() != null ? languageMetadata.getPrefix() + "." + key : key);
        }

        return resolve(json, languageMetadata);
    }

    @Unique
    private static String resolve(JsonElement element, LanguageMetadata languageMetadata) {
        if (element.isJsonPrimitive()) {
            return element.getAsString();
        } else if (element.isJsonObject() && languageMetadata.getReferences() != null) {
            return resolve(languageMetadata.getReferences().get(element.getAsJsonObject().get("@id").getAsString()), languageMetadata);
        } else if (element.isJsonArray() && languageMetadata.getAllowMultiline()) {
            return element.getAsJsonArray()
                .asList()
                .stream()
                .map(it -> resolve(it, languageMetadata))
                .collect(Collectors.joining("\n"));
        }
        throw new UnsupportedOperationException(element.toString());
    }

}
