package gg.nodus.gaslight.mixin;

import net.minecraft.client.report.MessagesListAdder;
import net.minecraft.client.report.ReceivedMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MessagesListAdder.class)
public class MixinMessagesListAdder {

    @Inject(method = "addContextMessages", at = @At("HEAD"), cancellable = true)
    private static void addContextMessages(List<ReceivedMessage.IndexedMessage> messages, MessagesListAdder.MessagesList messagesList, CallbackInfoReturnable<Integer> cir) {
        messagesList.addMessages(messages);
        cir.setReturnValue(messages.size());
    }

}
