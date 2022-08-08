package gg.nodus.gaslight.mixin;

import com.mojang.brigadier.CommandDispatcher;
import gg.nodus.gaslight.command.SendHiddenCommand;
import gg.nodus.gaslight.command.SendNormalCommand;
import gg.nodus.gaslight.command.SendSystemCommand;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class MixinCommandManager {

    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CommandManager.RegistrationEnvironment environment, CommandRegistryAccess commandRegistryAccess, CallbackInfo ci) {
        SendHiddenCommand.register(dispatcher);
        SendNormalCommand.register(dispatcher);
        SendSystemCommand.register(dispatcher);
    }

}
