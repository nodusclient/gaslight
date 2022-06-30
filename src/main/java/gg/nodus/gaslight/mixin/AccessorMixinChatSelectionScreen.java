package gg.nodus.gaslight.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.ChatSelectionScreen;
import net.minecraft.client.report.AbuseReportContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChatSelectionScreen.class)
public interface AccessorMixinChatSelectionScreen {

    @Accessor("reporter")
    AbuseReportContext getReporter();

    @Accessor("parent")
    Screen getParent();

    @Invoker("init")
    void init();

}
