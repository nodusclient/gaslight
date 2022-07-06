package gg.nodus.gaslight.mixin;

import net.minecraft.client.gui.screen.report.ChatReportScreen;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.ChatAbuseReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChatReportScreen.class)
public interface AccessorChatReportScreen {

    @Accessor("report")
    ChatAbuseReport getReport();

    @Accessor("report")
    void setReport(ChatAbuseReport report);

    @Accessor("reporter")
    AbuseReportContext getAbuseReporter();

    @Invoker("onChange")
    void onChange();

}
