package me.owdding.lib.mixins;

import me.owdding.lib.cosmetics.CosmeticManager;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.ClientAsset;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    public <AvatarlikeEntity extends Avatar & ClientAvatarEntity> void capessssss(
        AvatarlikeEntity player,
        AvatarRenderState renderState,
        float partialTicks,
        CallbackInfo ci
    ) {
        final var cosmetic = CosmeticManager.INSTANCE.getMlibCosmetics().get(player.getUUID());
        if (cosmetic == null || cosmetic.getCapeTexture() == null) {
            System.out.println(cosmetic);
            return;
        }

        var image = CosmeticManager.getImageProvider().get(cosmetic.getCapeTexture());
        if (image == MissingTextureAtlasSprite.getLocation()) {
            System.out.println(image);
            return;
        }
        final var skin = renderState.skin;
        renderState.skin = new PlayerSkin(
            skin.body(),
            new ClientAsset.ResourceTexture(image, image),
            skin.elytra(),
            skin.model(),
            skin.secure()
        );
    }

}
