package gg.nodus.gaslight;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.bukkit.Bukkit.getPluginManager;

public class GaslightPlugin extends JavaPlugin implements Listener {

    private final Map<UUID, Map<String, String>> triggerWords = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, AsyncChatEvent>> spentTriggerWords = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        getPluginManager().registerEvents(this, this);

        getCommand("gaslight").setExecutor(this);
    }

    @EventHandler
    public void onChat(final AsyncChatEvent event) {
        event.renderer((player, sourceDisplayName, message, viewer) -> {
            final Map<String, String> playerTriggerWords = getPlayerTriggerWords(player);
            final Map<String, AsyncChatEvent> playerSpentTriggerWords = getPlayerSpentTriggerWords(player);
            if (playerTriggerWords == null) {
                return renderChat(sourceDisplayName, message);
            }

            final String plaintext = PlainTextComponentSerializer.plainText().serialize(message);
            final String[] words = plaintext.split(" ");

            for (final String word : words) {
                final AsyncChatEvent maybeOtherEvent = playerSpentTriggerWords.get(word);
                if (maybeOtherEvent != null && maybeOtherEvent != event) {
                    continue;
                }

                final String continuation = playerTriggerWords.get(word);
                if (continuation != null) {
                    playerSpentTriggerWords.put(word, event);
                    if (viewer == player) {
                        return renderChat(sourceDisplayName, message.append(Component.text(" " + continuation, NamedTextColor.RED)));
                    }
                    return renderChat(sourceDisplayName, message.append(Component.text(" " + continuation)));
                }
            }

            return renderChat(sourceDisplayName, message);
        });
    }


    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Must be player");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /gaslight [word] [words to appendedString]");
        }

        final String triggerWord = args[0];
        final String[] joinedArgs = Arrays.copyOfRange(args, 1, args.length);

        final String appendedString = String.join(" ", joinedArgs);
        getPlayerTriggerWords(player).put(triggerWord, appendedString);
        getPlayerSpentTriggerWords(player).remove(triggerWord);

        sender.sendMessage("§a[Gaslight] §7 Registered \"" + appendedString + "\" as a continuation for \"" + triggerWord + "\"");
        return true;
    }

    private Map<String, String> getPlayerTriggerWords(final Player player) {
        return triggerWords.computeIfAbsent(player.getUniqueId(), u -> new ConcurrentHashMap<>());
    }

    private Map<String, AsyncChatEvent> getPlayerSpentTriggerWords(final Player player) {
        return spentTriggerWords.computeIfAbsent(player.getUniqueId(), u -> new ConcurrentHashMap<>());
    }

    private static TextComponent renderChat(final Component sourceDisplayName, final Component message) {
        return Component.text()
                .append(Component.text("<"))
                .append(sourceDisplayName)
                .append(Component.text("> "))
                .append(message)
                .build();
    }

}
