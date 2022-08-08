package gg.nodus.gaslight;

import net.fabricmc.api.ModInitializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class Gaslight implements ModInitializer {

    public static final boolean USE_SYSTEM_MESSAGES;
    public static final Set<UUID> RECEIVING_MESSAGES = new HashSet<>();
    public static boolean IGNORE_PACKET_HACK = false;

    static {
        boolean system = false;
        try {
            var str = Files.readString(Path.of("gaslight"));
            system = str.toLowerCase(Locale.ROOT).equals("system=true");
        } catch (IOException e) {}
        USE_SYSTEM_MESSAGES = system;
    }

    public static final Set<Integer> removedMessageIndexes = new HashSet<>();

    @Override
    public void onInitialize() {
    }
}
