//? if <= 1.21.8 {
/*package me.owdding.lib.mixins;

import me.owdding.lib.cosmetics.CosmeticManager;
import me.owdding.lib.cosmetics.MlibCosmetics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("TAIL"))
    public void extractRenderState(AbstractClientPlayer player, PlayerRenderState renderState, float partialTicks, CallbackInfo ci) {
        final var cosmetic = MlibCosmetics.getMlibCosmetics().get(player.getUUID());
        if (cosmetic == null || cosmetic.getCapeTexture() == null) {
            return;
        }

        var image = CosmeticManager.getImageProvider().get(cosmetic.getCapeTexture());
        if (image.equals(MissingTextureAtlasSprite.getLocation())) {
            return;
        }

        final var skin = renderState.skin;
        renderState.skin = new PlayerSkin(
            skin.texture(),
            skin.textureUrl(),
            image,
            skin.elytraTexture(),
            skin.model(),
            skin.secure()
        );
    }
}
*///?}
