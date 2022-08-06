package gg.nodus.gaslight.mixin;

import net.minecraft.client.report.log.ChatLogEntry;
import net.minecraft.client.report.log.ChatLogImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatLogImpl.class)
public interface AccessorChatLogImpl {

    @Accessor
    ChatLogEntry[] getEntries();

}
