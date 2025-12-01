//? if > 1.21.5 {
package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.owdding.lib.accessor.FontPipelineHolder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiTextRenderState.class)
public class GuiTextRenderStateMixin {

    @WrapMethod(method = "ensurePrepared")
    private Font.PreparedText ensurePrepared(Operation<Font.PreparedText> original) {
        var holder = FontPipelineHolder.getHolder(this);
        if (holder != null) {
            FontPipelineHolder.ACTIVE_PIPELINE.set(holder.meowddinglib$getPipeline());
            var preparedText = original.call();
            FontPipelineHolder.ACTIVE_PIPELINE.remove();
            return preparedText;
        }
        return original.call();
    }
}
//?}
