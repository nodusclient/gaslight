package gg.nodus.gaslight.mixin;

import com.mojang.datafixers.util.Either;
import gg.nodus.gaslight.Gaslight;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.ChatAbuseReport;
import net.minecraft.client.report.log.ChatLogImpl;
import net.minecraft.client.report.log.HeaderEntry;
import net.minecraft.client.report.log.ReceivedMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatAbuseReport.class)
public class MixinChatAbuseReport {

    @Inject(method = "finalizeReport", at = @At("HEAD"))
    public void finalizeReport(AbuseReportContext reporter, CallbackInfoReturnable<Either<ChatAbuseReport.ReportWithId, ChatAbuseReport.ValidationError>> cir) {
        var chatLog = ((ChatLogImpl) reporter.chatLog());
        for (var index : Gaslight.removedMessageIndexes) {
            var message = ((AccessorChatLogImpl) chatLog).getEntries()[index];
            if (message instanceof ReceivedMessage.ChatMessage chatMessage) {
                var header = HeaderEntry.of(chatMessage.header(), chatMessage.headerSignature(), chatMessage.bodyDigest());
                ((AccessorChatLogImpl) chatLog).getEntries()[index] = header;
            }
        }
    }

}
