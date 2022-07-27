package gg.nodus.gaslight.mixin;

import net.minecraft.client.report.MessagesListAdder;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ReceivedMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MessagesListAdder.class)
public class MixinMessagesListAdder<T extends ReceivedMessage> {

    @Inject(method = "addContextMessages", at = @At("HEAD"), cancellable = true)
    private void addContextMessages(List<ChatLog.IndexedEntry<T>> list, MessagesListAdder.MessagesList<T> messagesList, CallbackInfoReturnable<Integer> cir) {
        messagesList.addMessages(list);
        cir.setReturnValue(list.size());
    }

}
