package gg.nodus.gaslight.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.abusereport.ChatSelectionScreen;
import net.minecraft.client.network.abusereport.AbuseReporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChatSelectionScreen.class)
public interface AccessorMixinChatSelectionScreen {

    @Accessor("reporter")
    AbuseReporter getReporter();

    @Accessor("parent")
    Screen getParent();

    @Invoker("init")
    void init();

}
