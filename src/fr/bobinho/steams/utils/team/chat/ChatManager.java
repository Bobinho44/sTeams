package fr.bobinho.steams.utils.team.chat;

import org.apache.commons.lang.Validate;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager {

    private static final Map<UUID, Chat> playersChat = new HashMap<>();

    private static final Map<UUID, Chat> getPlayersChat() {
        return playersChat;
    }

    public static Chat getPlayerChat(@Nonnull UUID player) {
        Validate.notNull(player, "player is null");

        return getPlayersChat().entrySet().stream()
                .filter(playerChat -> playerChat.getKey().equals(player)).map(Map.Entry::getValue).findFirst().orElse(Chat.PUBLIC);
    }

    public static void setPlayerChat(@Nonnull UUID player, @Nonnull Chat chat) {
        Validate.notNull(player, "player is null");
        Validate.notNull(chat, "chat is null");

        getPlayersChat().put(player, chat);
    }

}