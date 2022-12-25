package gg.nodus.gaslight.mixin;

import com.mojang.datafixers.util.Either;
import gg.nodus.gaslight.Gaslight;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.ChatAbuseReport;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ChatLogEntry;
import net.minecraft.client.report.log.ReceivedMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mixin(ChatAbuseReport.class)
public abstract class MixinChatAbuseReport {

    @Shadow
    public abstract IntSet getSelections();

    @Shadow
    public abstract void toggleMessageSelection(int index);

    @Inject(method = "finalizeReport", at = @At("HEAD"))
    public void finalizeReport(final AbuseReportContext reporter, final CallbackInfoReturnable<Either<ChatAbuseReport.ReportWithId, ChatAbuseReport.ValidationError>> cir) {
        var chatLog = ((ChatLog) reporter.getChatLog());
        Gaslight.REMOVED_MESSAGE_INDEXES.stream()
                .sorted(Comparator.comparingInt(i -> (int) i).reversed())
                .forEach(index -> {
                    var message = ((AccessorChatLogImpl) chatLog).getEntries()[index];
                    if (message instanceof ReceivedMessage.ChatMessage) {
                        final ChatLogEntry[] entries = ((AccessorChatLogImpl) chatLog).getEntries();
                        System.arraycopy(entries, index + 1, entries, index, entries.length - index - 1);
                        entries[entries.length - 1] = null;
                        final List<Integer> ints = new ArrayList<>(this.getSelections());
                        this.getSelections().clear();
                        for (final int reportedIndex : ints) {
                            if (reportedIndex >= index) {
                                this.toggleMessageSelection(reportedIndex - 1);
                            } else {
                                this.toggleMessageSelection(reportedIndex);
                            }
                        }
                    }
                });
    }

}
