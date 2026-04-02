package io.github.paulmrtnz;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageUtils {
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    /**
     * Convertit un message avec codes couleur & en Component Adventure
     * @param message Le message avec codes & (&c = rouge, &a = vert, &e = jaune, &b = bleu, etc.)
     * @return Component formaté
     */
    public static Component colorize(String message) {
        return SERIALIZER.deserialize(message);
    }
}
