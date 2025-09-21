package me.owdding.lib.mixins;

import me.owdding.lib.accessor.PlayerRenderStateAccessor;
import me.owdding.lib.cosmetics.CosmeticParser;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("TAIL"))
    public void extractRenderState(AbstractClientPlayer abstractClientPlayer, PlayerRenderState playerRenderState, float f, CallbackInfo ci) {
        PlayerRenderStateAccessor.setUUID(playerRenderState, abstractClientPlayer.getUUID());

        var contributor = CosmeticParser.INSTANCE.getCosmetics().get(abstractClientPlayer.getUUID());
        if (contributor == null) return;

        var skin = playerRenderState.skin;
        playerRenderState.skin = new PlayerSkin(
            skin.texture(),
            skin.textureUrl(),
            contributor.getCape(),
            skin.elytraTexture(),
            skin.model(),
            skin.secure()
        );
    }

}
