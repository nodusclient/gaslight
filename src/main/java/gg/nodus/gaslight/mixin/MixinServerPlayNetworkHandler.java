package gg.nodus.gaslight.mixin;

import gg.nodus.gaslight.Gaslight;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.HideMessageS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler {


    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow public ServerPlayerEntity player;

    @Shadow public abstract void sendPacket(Packet<?> packet, @Nullable PacketCallbacks callbacks);

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("TAIL"))
    private void sendPacket(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        if (Gaslight.USE_SYSTEM_MESSAGES &&
                !Gaslight.RECEIVING_MESSAGES.contains(this.player.getUuid()) &&
                !Gaslight.IGNORE_PACKET_HACK &&
                packet instanceof ChatMessageS2CPacket chat) {
            var text = chat.serializedParameters().toParameters(this.server.getRegistryManager()).get().applyChatDecoration(chat.message().getContent());
            sendPacket(new HideMessageS2CPacket(chat.message().headerSignature()), null);
            sendPacket(new GameMessageS2CPacket(text, false), null);
        }
    }

}