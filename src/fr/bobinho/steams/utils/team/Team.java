package fr.bobinho.steams.utils.team;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class Team {

    private String name;
    private final Map<UUID, TeamRole> members = new HashMap<>();
    private final List<Team> allies = new ArrayList<>();
    private final List<Team> enemies = new ArrayList<>();
    private Location HQ;
    private String description =  ChatColor.YELLOW + "No description set :(";

    public Team(@Nonnull String name, @Nonnull UUID leader) {
        Validate.notNull(name, "name is null");
        Validate.notNull(leader, "leader is null");

        this.name = name;
        this.members.put(leader, TeamRole.LEADER);
    }

    public Team(@Nonnull String name) {
        Validate.notNull(name, "name is null");

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<UUID, TeamRole> getMembers() {
        return members;
    }

    public String getMemberName(@Nonnull Map.Entry<UUID, TeamRole> member) {
        Validate.notNull(member, "member is null");

        if (Bukkit.getPlayer(member.getKey()) == null) {
            return member.getValue().getSymbol() + Bukkit.getOfflinePlayer(member.getKey()).getName();
        }

        return member.getValue().getSymbol() + Objects.requireNonNull(Bukkit.getPlayer(member.getKey())).getName();
    }

    public String getMembersOnlineAsString(@Nonnull UUID requester) {
        Validate.notNull(requester, "requester is null");

        return getMembers().entrySet().stream()
                .filter(member -> getOnlineMembers().contains(member.getKey()))
                .map(member -> {
                    ChatColor color = ChatColor.WHITE;
                    if (TeamManager.areTeammate(member.getKey(), requester)) {
                        color = ChatColor.DARK_GREEN;
                    }
                    if (TeamManager.areAllied(member.getKey(), requester)) {
                        color = ChatColor.BLUE;
                    }
                    if (TeamManager.areEnemy(member.getKey(), requester)) {
                        color = ChatColor.RED;
                    }
                    return color + getMemberName(member);
                })
                .collect(Collectors.joining(ChatColor.GRAY + ", "));
    }

    public String getMembersOfflineAsString(@Nonnull UUID requester) {
        Validate.notNull(requester, "requester is null");

        return getMembers().entrySet().stream()
                .filter(member -> !getOnlineMembers().contains(member.getKey()))
                .map(member -> {
                    ChatColor color = ChatColor.WHITE;
                    if (TeamManager.areTeammate(member.getKey(), requester)) {
                        color = ChatColor.DARK_GREEN;
                    }
                    if (TeamManager.areAllied(member.getKey(), requester)) {
                        color = ChatColor.BLUE;
                    }
                    if (TeamManager.areEnemy(member.getKey(), requester)) {
                        color = ChatColor.RED;
                    }
                    return color + getMemberName(member);
                })
                .collect(Collectors.joining(ChatColor.GRAY + ", "));
    }

    public String getAlliesAsString(@Nonnull UUID requester) {
        Validate.notNull(requester, "requester is null");

        return getAllies().stream()
                .map(team -> {
                    ChatColor color = ChatColor.WHITE;
                    if (TeamManager.isTeammate(team, requester)) {
                        color = ChatColor.DARK_GREEN;
                    }
                    if (TeamManager.isAllied(team, requester)) {
                        color = ChatColor.BLUE;
                    }
                    if (TeamManager.isEnnemies(team, requester)) {
                        color = ChatColor.RED;
                    }
                    return color + team.getName();
                })
                .collect(Collectors.joining(ChatColor.GRAY + ", "));
    }

    public String getEnemiesAsString(@Nonnull UUID requester) {
        Validate.notNull(requester, "requester is null");

        return getEnemies().stream()
                .map(team -> {
                    ChatColor color = ChatColor.WHITE;
                    if (TeamManager.isTeammate(team, requester)) {
                        color = ChatColor.DARK_GREEN;
                    }
                    if (TeamManager.isAllied(team, requester)) {
                        color = ChatColor.BLUE;
                    }
                    if (TeamManager.isEnnemies(team, requester)) {
                        color = ChatColor.RED;
                    }
                    return color + team.getName();
                })
                .collect(Collectors.joining(ChatColor.GRAY + ", "));
    }

    public List<UUID> getOnlineMembers() {
        return getMembers().keySet().stream().filter(player -> Bukkit.getPlayer(player) != null).collect(Collectors.toList());
    }

    public void addMember(UUID uuid, TeamRole role) {
        getMembers().put(uuid, role);
    }

    public void removeMember(UUID uuid) {
        getMembers().remove(uuid);
    }

    public List<Team> getAllies() {
        return allies;
    }

    public void addAlly(Team ally) {
        getAllies().add(ally);
    }

    public void removeAlly(Team ally) {
        getAllies().remove(ally);
    }

    public List<Team> getEnemies() {
        return enemies;
    }

    public void addEnemy(Team enemy) {
        getEnemies().add(enemy);
    }

    public void removeEnemy(Team enemy) {
        getEnemies().remove(enemy);
    }

    public Location getHQ() {
        return HQ;
    }

    public void setHQ(Location HQ) {
        this.HQ = HQ;
    }

    public String getInformations(@Nonnull UUID requester) {
        Validate.notNull(requester, "requester is null");

        //Gets color of requester link with this team
        ChatColor color = ChatColor.WHITE;
        if (TeamManager.isTeammate(this, requester)) {
            color = ChatColor.DARK_GREEN;
        }
        if (TeamManager.isAllied(this, requester)) {
            color = ChatColor.BLUE;
        }
        if (TeamManager.isEnnemies(this, requester)) {
            color = ChatColor.RED;
        }

        //Gets team informations

        return ChatColor.GOLD + "============[ " + color + getName() + ChatColor.GOLD + " ]============\n" +
                ChatColor.GOLD + "Desc: " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', getDescription()) + "\n" +
                ChatColor.GOLD + "Allies: " + getAlliesAsString(requester) + "\n" +
                ChatColor.GOLD + "Enemies: " + getEnemiesAsString(requester) + "\n" +
                ChatColor.GOLD + "Members online (" + getOnlineMembers().size() + "): " + getMembersOnlineAsString(requester) + "\n" +
                ChatColor.GOLD + "Members offline (" + (getMembers().size() - getOnlineMembers().size()) + "): " + getMembersOfflineAsString(requester) + "\n";
    }

    public void sendMessage(@Nonnull String message) {
        Validate.notNull(message, "message is null");

        getMembers().keySet().stream().filter(member -> Bukkit.getPlayer(member) != null).collect(Collectors.toList())
                .forEach(member -> Objects.requireNonNull(Bukkit.getPlayer(member)).sendMessage(message));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(@Nonnull String description) {
        Validate.notNull(description, "message is null");

        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Team)) {
            return false;
        }
        Team testedTeam = (Team) o;
        return testedTeam.getName().equalsIgnoreCase(getName());
    }

}