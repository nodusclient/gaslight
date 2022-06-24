package gg.nodus.fakereport.mixin;

import gg.nodus.fakereport.Fakereport;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    @Inject(method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    public void onChat(String message, Text preview, CallbackInfo ci) {
        System.out.println(message);
        if (message.startsWith(".context ")) {
            Fakereport.context.add(message.split(" ", 2)[1]);
            ci.cancel();
        }
        if (message.startsWith(".clearcontext")) {
            Fakereport.context.clear();
            ci.cancel();
        }
    }

}
