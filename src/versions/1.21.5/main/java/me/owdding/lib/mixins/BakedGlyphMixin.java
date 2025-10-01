package me.owdding.lib.mixins;

import me.owdding.lib.helper.TextShaderHolder;
import me.owdding.lib.helper.TextShaderMixinHelper;
import me.owdding.lib.rendering.text.TextShader;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    targets = {
        "net.minecraft.client.gui.font.glyphs.BakedGlyph$GlyphInstance",
        "net.minecraft.client.gui.font.glyphs.BakedGlyph$Effect",
    }
)
public class BakedGlyphMixin implements TextShaderHolder {

    @Unique
    private TextShader meowddinglib$textShader;

    @Inject(method = "<init>*", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        meowddinglib$textShader = TextShaderMixinHelper.meowddinglib$SHADER.get();
    }

    @Override
    public TextShader meowddinglib$getTextShader() {
        return meowddinglib$textShader;
    }

    @Override
    public Style meowddinglib$withTextShader(TextShader shader) {
        return null;
    }
}
