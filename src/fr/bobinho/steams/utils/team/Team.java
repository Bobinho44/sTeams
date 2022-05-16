package fr.bobinho.steams.utils.team;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Team {

    private String name;
    private final Map<UUID, TeamRole> members = new HashMap<>();
    private final List<Team> allies = new ArrayList<>();
    private Location HQ;

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
            return ChatColor.GRAY + member.getValue().getSymbol() + Bukkit.getOfflinePlayer(member.getKey()).getName();
        }

        return ChatColor.GREEN + member.getValue().getSymbol() + Objects.requireNonNull(Bukkit.getPlayer(member.getKey())).getName();
    }

    public String getMembersAsString() {
        return getMembers().entrySet().stream().map(this::getMemberName).collect(Collectors.joining(ChatColor.GRAY + ", "));
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

    public Location getHQ() {
        return HQ;
    }

    public void setHQ(Location HQ) {
        this.HQ = HQ;
    }

    public String getInformations(@Nonnull UUID requester) {
        Validate.notNull(requester, "requester is null");

        //Gets color of requester link with this team
        AtomicReference<ChatColor> color = new AtomicReference<>(ChatColor.RED);
        TeamManager.getTeam(requester).ifPresent(team -> {
            if (team.equals(this)) {
                color.set(ChatColor.DARK_GREEN);
            }
            if (TeamManager.areAllied(team, this)) {
                color.set(ChatColor.BLUE);
            }
        });

        //Gets team informations
        return color.get() + getName() +
                ChatColor.GRAY + " [" + getOnlineMembers().size() + "/" + getMembers().size() + "]\n" +
                ChatColor.GRAY + "Allys: \n" + getAllies().stream().map(Team::getName).collect(Collectors.joining(", ")) + "\n" +
                ChatColor.GRAY + "Members: \n" + getMembersAsString() + "\n";
    }

    public void sendMessage(@Nonnull String message) {
        Validate.notNull(message, "message is null");

        getMembers().keySet().stream().filter(member -> Bukkit.getPlayer(member) != null).collect(Collectors.toList())
                .forEach(member -> Objects.requireNonNull(Bukkit.getPlayer(member)).sendMessage(message));
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