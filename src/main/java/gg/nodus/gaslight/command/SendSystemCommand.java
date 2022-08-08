package gg.nodus.gaslight.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class SendSystemCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("sendsystem").requires((source) -> source.hasPermissionLevel(1)).then(CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.argument("message", StringArgumentType.greedyString()).executes((context) -> {
            var message = StringArgumentType.getString(context, "message");
            return execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), message);
        }))));
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String message) {
        MessageType.Parameters parameters = MessageType.params(MessageType.CHAT, source);
        var text = parameters.applyChatDecoration(Text.literal(message));
        var packet = new GameMessageS2CPacket(text, false);
        for (ServerPlayerEntity target : targets) {
            target.networkHandler.sendPacket(packet);
        }
        source.sendMessage(text);

        return targets.size();
    }

}
