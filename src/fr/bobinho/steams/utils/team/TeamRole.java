package fr.bobinho.steams.utils.team;

import org.apache.commons.lang.Validate;

import javax.annotation.Nonnull;
import java.util.Arrays;

public enum TeamRole {
    LEADER("$", "Leader", 3),
    CO_LEADER("**", "Co-Leader", 2),
    MOD("*", "Mod", 1),
    MEMBER("", "Member", 0);

    /**
     * Fields
     */
    private final String symbol;
    private final String name;
    private final int level;

    /**
     * Creates a new role
     *
     * @param symbol the symbol
     * @param name the name
     * @param level  the level
     */
    TeamRole(@Nonnull String symbol, @Nonnull String name, int level) {
        Validate.notNull(symbol, "symbol is null");

        this.symbol = symbol;
        this.name = name;
        this.level = level;
    }

    /**
     * Gets the symbol
     *
     * @return the symbol
     */
    @Nonnull
    public String getSymbol() {
        return symbol;
    }

    /**
     * Gets the name
     *
     * @return the name
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Gets the level
     *
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets the role from a level
     *
     * @param level the level
     * @return the role
     */
    @Nonnull
    public static TeamRole getRoleFromLevel(int level) {
        return Arrays.stream(values()).filter(role -> role.getLevel() == level).findFirst().orElse(MEMBER);
    }

}