package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

    @Shadow
    @Final
    public ClientPacketListener connection;

    public LocalPlayerMixin(ClientLevel $$0, GameProfile $$1) {
        super($$0, $$1);
    }

    /**
     * Fixes an issue where the server can constantly send experience updates, but the values are not changing.
     * This causes the experience bar to always be displayed, even when another bar renderer should take priority.
     */
    @WrapMethod(method = "setExperienceValues")
    private void preventExperienceDisplayStartTick(float progress, int total, int level, Operation<Void> original) {
        if (this.experienceProgress == progress && this.totalExperience == total && this.experienceLevel == level
            && this.connection.getWaypointManager().hasWaypoints()) {
            return;
        }
        original.call(progress, total, level);
    }
}
