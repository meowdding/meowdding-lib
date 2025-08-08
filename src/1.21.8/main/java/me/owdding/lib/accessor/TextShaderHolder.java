package me.owdding.lib.accessor;

import me.owdding.lib.rendering.text.TextShader;
import me.owdding.lib.rendering.text.TextShaders;
import net.minecraft.network.chat.Style;

public interface TextShaderHolder {

    TextShader meowddinglib$getTextShader();

    Style meowddinglib$withTextShader(TextShader shader);
}
