package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.lib.accessor.TextShaderHolder;
import me.owdding.lib.rendering.text.TextShader;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(Style.class)
public abstract class StyleMixin implements TextShaderHolder {

    private final static ThreadLocal<TextShader> meowddinglib$SHADER = new ThreadLocal<>();
    @Shadow
    @Final
    @Nullable
    ResourceLocation font;
    @Shadow
    @Final
    @Nullable
    String insertion;
    @Shadow
    @Final
    @Nullable
    HoverEvent hoverEvent;
    @Shadow
    @Final
    @Nullable
    ClickEvent clickEvent;
    @Shadow
    @Final
    @Nullable
    Boolean obfuscated;
    @Shadow
    @Final
    @Nullable
    Boolean strikethrough;
    @Shadow
    @Final
    @Nullable
    Boolean underlined;
    @Shadow
    @Final
    @Nullable
    Boolean italic;
    @Shadow
    @Final
    @Nullable
    Boolean bold;
    @Shadow
    @Final
    @Nullable
    Integer shadowColor;
    @Shadow
    @Final
    @Nullable
    TextColor color;
    @Unique
    @Nullable
    private TextShader meowddinglib$textShader;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        if (meowddinglib$SHADER == null) {
            return;
        }
        this.meowddinglib$textShader = meowddinglib$SHADER.get();
    }

    @WrapOperation(method = {
        "withColor(Lnet/minecraft/network/chat/TextColor;)Lnet/minecraft/network/chat/Style;",
        "withShadowColor",
        "withBold",
        "withItalic",
        "withUnderlined",
        "withStrikethrough",
        "withObfuscated",
        "withClickEvent",
        "withHoverEvent",
        "withInsertion",
        "withFont",
        "applyFormat",
        "applyFormats",
        "applyLegacyFormat",
        "applyTo"
    }, at = @At(value = "NEW", target = "(Lnet/minecraft/network/chat/TextColor;Ljava/lang/Integer;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Lnet/minecraft/network/chat/ClickEvent;Lnet/minecraft/network/chat/HoverEvent;Ljava/lang/String;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/network/chat/Style;"))
    public Style copy(
        TextColor color,
        Integer shadowColor,
        Boolean bold,
        Boolean italic,
        Boolean underlined,
        Boolean strikethrough,
        Boolean obfuscated,
        ClickEvent clickEvent,
        HoverEvent hoverEvent,
        String insertion,
        ResourceLocation font,
        Operation<Style> original
    ) {
        var previous = meowddinglib$SHADER.get();
        meowddinglib$SHADER.set(meowddinglib$textShader);
        var style = original.call(
            color,
            shadowColor,
            bold,
            italic,
            underlined,
            strikethrough,
            obfuscated,
            clickEvent,
            hoverEvent,
            insertion,
            font);
        meowddinglib$SHADER.set(previous);
        return style;
    }

    @Override
    public TextShader meowddinglib$getTextShader() {
        return this.meowddinglib$textShader;
    }

    @Override
    public Style meowddinglib$withTextShader(TextShader shader) {
        var previous = meowddinglib$SHADER.get();
        meowddinglib$SHADER.set(shader);
        var value = Objects.equals(this.meowddinglib$textShader, shader)
            ? ((Style) (Object) this)
            : Style.checkEmptyAfterChange(
            new Style(
                this.color,
                this.shadowColor,
                this.bold,
                this.italic,
                this.underlined,
                this.strikethrough,
                this.obfuscated,
                this.clickEvent,
                this.hoverEvent,
                this.insertion,
                this.font),
            this.meowddinglib$textShader,
            meowddinglib$textShader);
        meowddinglib$SHADER.set(previous);
        return value;
    }

    @ModifyReturnValue(method = "equals", at = @At(value = "RETURN"))
    public boolean equals(boolean original, @Local(argsOnly = true) Object other) {
        if (other instanceof TextShaderHolder otherHolder) {
            return original && otherHolder.meowddinglib$getTextShader() == this.meowddinglib$textShader;
        }
        return original;
    }
}
