package me.owdding.lib.mixins.compat.rei;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@IfModLoaded("roughlyenoughitems")
@Mixin(targets = "me.shedaniel.rei.impl.client.gui.widget.search.OverlaySearchField", remap = false)
public interface OverlaySearchFieldAccessor {

    @Accessor(value = "isHighlighting")
    boolean mlib$isHighlighting();

}
