package fr.bobinho.steams.utils.team.chat;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.Arrays;

public enum Chat {
    PUBLIC("", ChatColor.GOLD + "Public"),
    ALLY(ChatColor.YELLOW + "(" + ChatColor.BLUE + "AC" + ChatColor.YELLOW + ") ", ChatColor.BLUE + "Ally"),
    TEAM(ChatColor.YELLOW + "(" + ChatColor.GREEN + "TC" + ChatColor.YELLOW + ") ", ChatColor.GREEN + "Team");

    private final String prefix;
    private final String name;

    Chat(@Nonnull String prefix, @Nonnull String name) {
        Validate.notNull(prefix, "prefix is null");
        Validate.notNull(name, "name is null");

        this.prefix = prefix;
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public static Chat getNextChat(@Nonnull Chat chat) {
        Validate.notNull(chat, "chat is null");

        //Gets next chat
        return switch (chat) {
            case PUBLIC -> ALLY;
            case ALLY -> TEAM;
            case TEAM -> PUBLIC;
        };
    }

    public static boolean isValidChat(@Nonnull String testedChat) {
        return Arrays.stream(values()).anyMatch(chat -> chat.name().equalsIgnoreCase(testedChat));
    }

}