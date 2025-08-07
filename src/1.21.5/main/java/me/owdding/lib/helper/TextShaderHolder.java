package me.owdding.lib.helper;

import me.owdding.lib.rendering.text.TextShader;
import net.minecraft.network.chat.Style;

public interface TextShaderHolder {

    TextShader meowddinglib$getTextShader();

    Style meowddinglib$withTextShader(TextShader shader);

}
