package gg.nodus.gaslight.mixin;

import net.minecraft.client.gui.screen.abusereport.ChatReportScreen;
import net.minecraft.client.network.abusereport.AbuseReporter;
import net.minecraft.client.network.abusereport.ChatAbuseReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChatReportScreen.class)
public interface AccessorChatReportScreen {

    @Accessor("report")
    ChatAbuseReport getReport();

    @Accessor("reporter")
    AbuseReporter getAbuseReporter();

    @Accessor("report")
    void setReport(ChatAbuseReport report);

    @Invoker("onChange")
    void onChange();

}