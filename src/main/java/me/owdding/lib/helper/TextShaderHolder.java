package me.owdding.lib.helper;

import me.owdding.lib.rendering.text.TextShader;
import net.minecraft.network.chat.Style;
import org.apache.commons.lang3.NotImplementedException;

public interface TextShaderHolder {

    default TextShader meowddinglib$getTextShader() {
        throw new NotImplementedException("Implemented via mixins!");
    }

    default Style meowddinglib$withTextShader(TextShader shader) {
        throw new NotImplementedException("Implemented via mixins!");
    }
}
