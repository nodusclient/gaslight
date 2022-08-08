package gg.nodus.gaslight.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class SendNormalCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("sendnormal").requires((source) -> source.hasPermissionLevel(1)).then(CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.argument("message", MessageArgumentType.message()).executes((context) -> {
            MessageArgumentType.SignedMessage signedMessage = MessageArgumentType.getSignedMessage(context, "message");
            try {
                return execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), signedMessage);
            } catch (Exception var3) {
                signedMessage.sendHeader(context.getSource());
                throw var3;
            }
        }))));
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, MessageArgumentType.SignedMessage signedMessage) {
        MessageType.Parameters parameters = MessageType.params(MessageType.CHAT, source);
        signedMessage.decorate(source, (message) -> {
            SentMessage sentMessage = SentMessage.of(message);
            boolean bl = message.isFullyFiltered();
            Entity entity = source.getEntity();
            boolean bl2 = false;

            ServerPlayerEntity serverPlayerEntity;
            boolean bl3;
            for (ServerPlayerEntity target : targets) {
                serverPlayerEntity = target;
                MessageType.Parameters parameters2 = MessageType.params(MessageType.CHAT, source).withTargetName(serverPlayerEntity.getDisplayName());
                source.sendChatMessage(sentMessage, false, parameters2);
                bl3 = source.shouldFilterText(serverPlayerEntity);
                serverPlayerEntity.sendChatMessage(sentMessage, bl3, parameters);
                bl2 |= bl && bl3 && serverPlayerEntity != entity;
            }

            if (bl2) {
                source.sendMessage(PlayerManager.FILTERED_FULL_TEXT);
            }

            sentMessage.afterPacketsSent(source.getServer().getPlayerManager());
        });
        return targets.size();
    }

}
