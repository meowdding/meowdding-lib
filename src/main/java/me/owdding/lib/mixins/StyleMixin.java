//~ named_identifier
package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Objects;
import me.owdding.lib.helper.TextShaderHolder;
import me.owdding.lib.rendering.text.TextShader;
import net.minecraft.network.chat.*;
//? < 1.21.9
/*import net.minecraft.resources.Identifier;*/
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Style.class)
public abstract class StyleMixin implements TextShaderHolder {

    @Unique
    private final static ThreadLocal<TextShader> meowddinglib$SHADER = new ThreadLocal<>();
    @Shadow
    @Final
    @Nullable
    //? if > 1.21.8 {
    FontDescription font;
    //?} else
    /*Identifier font;*/
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
    }, at = @At(
        value = "NEW",
        //? if > 1.21.8 {
        target = "(Lnet/minecraft/network/chat/TextColor;Ljava/lang/Integer;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Lnet/minecraft/network/chat/ClickEvent;Lnet/minecraft/network/chat/HoverEvent;Ljava/lang/String;Lnet/minecraft/network/chat/FontDescription;)Lnet/minecraft/network/chat/Style;"
        //?} else {
        /*target = "(Lnet/minecraft/network/chat/TextColor;Ljava/lang/Integer;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Lnet/minecraft/network/chat/ClickEvent;Lnet/minecraft/network/chat/HoverEvent;Ljava/lang/String;Lnet/minecraft/resources/Identifier;)Lnet/minecraft/network/chat/Style;"
       *///?}
    ))
    public Style copy(
        TextColor textColor,
        Integer shadowColor,
        Boolean bold,
        Boolean italic,
        Boolean underlined,
        Boolean strikethrough,
        Boolean obfuscated,
        ClickEvent clickEvent,
        HoverEvent hoverEvent,
        String insertion,
        /*? if > 1.21.8 {*/ FontDescription fontDescription, /*?} else {*/ /*Identifier font, *//*?}*/
        Operation<Style> original
    ) {
        var previous = meowddinglib$SHADER.get();
        meowddinglib$SHADER.set(meowddinglib$textShader);
        var style = original.call(
            textColor,
            shadowColor,
            bold,
            italic,
            underlined,
            strikethrough,
            obfuscated,
            clickEvent,
            hoverEvent,
            insertion,
            /*? if > 1.21.8 {*/ fontDescription /*?} else {*/ /*font *//*?}*/
        );
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
                this.font
            ),
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
