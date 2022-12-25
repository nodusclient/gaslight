package gg.nodus.gaslight.mixin;

import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ChatLogEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatLog.class)
public interface AccessorChatLogImpl {

    @Accessor
    ChatLogEntry[] getEntries();

}
