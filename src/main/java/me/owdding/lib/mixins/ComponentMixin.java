package me.owdding.lib.mixins;

import me.owdding.lib.internal.LanguageHelper;
import me.owdding.lib.internal.PlaceholderLanguageProvider;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Component.class)
public interface ComponentMixin {

    @Unique
    private static void handle(String key, String fallback, Object[] args, CallbackInfoReturnable<MutableComponent> cir) {
        if (!LanguageHelper.getComponentKeyList().contains(key)) {
            return;
        }

        String translation = Language.getInstance().getOrDefault(key, fallback);

        for (int i = 0; i < args.length; i++) {
            translation = translation.replaceAll("<!" + i + ">", String.valueOf(args[i]));
        }

        cir.setReturnValue(PlaceholderLanguageProvider.INSTANCE.parse(translation, args).copy());
    }

    @Inject(method = "translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;", at = @At("HEAD"), cancellable = true)
    private static void translatable(String key, CallbackInfoReturnable<MutableComponent> cir) {
        handle(key, null, TranslatableContents.NO_ARGS, cir);
    }
    @Inject(method = "translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;", at = @At("HEAD"), cancellable = true)
    private static void translatable(String key, Object[] args, CallbackInfoReturnable<MutableComponent> cir) {
        handle(key, null, args, cir);
    }
    @Inject(method = "translatableWithFallback(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;", at = @At("HEAD"), cancellable = true)
    private static void translatable(String key, String fallback, CallbackInfoReturnable<MutableComponent> cir) {
        handle(key, fallback, TranslatableContents.NO_ARGS, cir);
    }
    @Inject(method = "translatableWithFallback(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;", at = @At("HEAD"), cancellable = true)
    private static void translatable(String key, String fallback, Object[] args, CallbackInfoReturnable<MutableComponent> cir) {
        handle(key, fallback, args, cir);
    }

}
