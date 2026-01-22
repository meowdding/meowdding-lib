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
            var pipeline = FontPipelineHolder.ACTIVE_PIPELINE;

            var previous = pipeline.get();
            var next = holder.meowddinglib$getPipeline();

            var changed = previous != next;
            if(changed) pipeline.set(next);

            try {
                return original.call();
            } finally {
                if(changed) pipeline.set(previous);
            }
        }
        return original.call();
    }
}
//?}
