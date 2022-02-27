package fr.bobinho.steams.utils.team;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TeamManager {

    private static final List<Team> teams = new ArrayList<>();

    private static List<Team> getTeams() {
        return teams;
    }

    public static Optional<Team> getTeam(@Nonnull String name) {
        Validate.notNull(name, "name is null");

        return getTeams().stream().filter(team -> team.getName().equalsIgnoreCase(name)).findFirst();
    }

    public static Optional<Team> getTeam(@Nonnull UUID uuid) {
        Validate.notNull(uuid, "uuid is null");

        return getTeams().stream().filter(team -> team.getMembers().containsKey(uuid)).findFirst();
    }

    public static boolean isItTeam(@Nonnull String name) {
        Validate.notNull(name, "name is null");

        return getTeam(name).isPresent();
    }

    public static boolean isInTeam(@Nonnull UUID uuid) {
        Validate.notNull(uuid, "uuid is null");

        return getTeam(uuid).isPresent();
    }

    public static void createTeam(@Nonnull String name, @Nonnull UUID leader) {
        Validate.notNull(name, "name is null");
        Validate.notNull(leader, "leader is null");
        Validate.isTrue(!isItTeam(name), "name is already used");
        Validate.isTrue(!isInTeam(leader), "leader is already in a team");

        getTeams().add(new Team(name, leader));
    }

    public static void deleteTeam(@Nonnull String name) {
        Validate.notNull(name, "name is null");
        Validate.isTrue(isItTeam(name), "name is not used");

        getTeams().remove(getTeam(name).get());
    }

    public static void joinTeam(@Nonnull Team team, @Nonnull UUID member) {
        Validate.notNull(team, "team is null");
        Validate.notNull(member, "member is null");
        Validate.isTrue(!isInTeam(member), "member is already in a team");

        team.addMember(member, TeamRole.MEMBER);
    }

    public static void leaveTeam(@Nonnull Team team, @Nonnull UUID member) {
        Validate.notNull(team, "team is null");
        Validate.notNull(member, "member is null");
        Validate.isTrue(isInTeam(member), "member is not in a team");

        team.removeMember(member);
    }

    public static boolean canKick(@Nonnull UUID kicker, @Nonnull UUID kicked) {
        Validate.notNull(kicker, "kicker is null");
        Validate.notNull(kicked, "kicked is null");
        Validate.isTrue(isInTeam(kicker), "kicker is not in a team");
        Validate.isTrue(isInTeam(kicked), "kicked is not in a team");
        Validate.isTrue(getTeam(kicker).equals(kicked), "kicker and kicked are not in the same team");

        return getPlayerRole(kicker).getLevel() >= getPlayerRole(kicked).getLevel();
    }

    public static boolean isAlone(@Nonnull UUID player) {
        Validate.notNull(player, "player is null");
        Validate.isTrue(isInTeam(player), "player is not in a team");

        return getTeam(player).get().getMembers().size() == 1;
    }

    public static TeamRole getPlayerRole(@Nonnull UUID player) {
        Validate.notNull(player, "player is null");
        Validate.isTrue(isInTeam(player), "player is not in a team");

        return getTeam(player).get().getMembers().get(player);
    }

    public static boolean isAtLeastLeader(@Nonnull UUID player) {
        Validate.notNull(player, "player is null");
        Validate.isTrue(isInTeam(player), "player is not in a team");

        return getPlayerRole(player).getLevel() >= 3;
    }

    public static boolean isAtLeastCoLeader(@Nonnull UUID player) {
        Validate.notNull(player, "player is null");
        Validate.isTrue(isInTeam(player), "player is not in a team");

        return getPlayerRole(player).getLevel() >= 2;
    }

    public static boolean isAtLeastMod(@Nonnull UUID player) {
        Validate.notNull(player, "player is null");
        Validate.isTrue(isInTeam(player), "player is not in a team");

        return getPlayerRole(player).getLevel() >= 1;
    }

    public static boolean canPromote(@Nonnull UUID promoter, @Nonnull UUID promoted) {
        Validate.notNull(promoter, "promoter is null");
        Validate.notNull(promoted, "promoted is null");
        Validate.isTrue(isInTeam(promoter), "promoter is not in a team");
        Validate.isTrue(isInTeam(promoted), "promoted is not in a team");
        Validate.isTrue(getTeam(promoter).equals(getTeam(promoted)), "promoter and promoted are not in the same team");

        return getPlayerRole(promoted) != TeamRole.CO_LEADER && getPlayerRole(promoter).getLevel() > getPlayerRole(promoted).getLevel();
    }

    public static void promote(@Nonnull UUID promoted) {
        Validate.notNull(promoted, "promoter is null");
        Validate.isTrue(isInTeam(promoted), "promoted is not in a team");

        getTeam(promoted).get().addMember(promoted, TeamRole.getRoleFromLevel(getPlayerRole(promoted).getLevel() + 1));
    }

    public static boolean canDemote(@Nonnull UUID demoter, @Nonnull UUID demoted) {
        Validate.notNull(demoter, "demoter is null");
        Validate.notNull(demoted, "demoted is null");
        Validate.isTrue(isInTeam(demoter), "demoter is not in a team");
        Validate.isTrue(isInTeam(demoted), "demoted is not in a team");
        Validate.isTrue(getTeam(demoter).equals(getTeam(demoted)), "demoter and demoted are not in the same team");

        return isAtLeastCoLeader(demoter) && isAtLeastMod(demoted) && getPlayerRole(demoter).getLevel() >= getPlayerRole(demoted).getLevel();
    }

    public static void demote(@Nonnull UUID demoted) {
        Validate.notNull(demoted, "demoted is null");
        Validate.isTrue(isInTeam(demoted), "demoted is not in a team");

        getTeam(demoted).get().addMember(demoted, TeamRole.getRoleFromLevel(getPlayerRole(demoted).getLevel() - 1));
    }

    public static void setHQ(@Nonnull UUID player, @Nonnull Location HQ) {
        Validate.notNull(player, "player is null");
        Validate.notNull(HQ, "HQ is null");
        Validate.isTrue(isInTeam(player), "player is not in a team");
        Validate.isTrue(isAtLeastCoLeader(player), "player does not have the necessary rank");

        getTeam(player).get().setHQ(HQ);
    }

    public static Location getHQ(@Nonnull UUID player) {
        Validate.notNull(player, "player is null");
        Validate.isTrue(isInTeam(player), "player is not in a team");

        return getTeam(player).get().getHQ();
    }

    public static boolean hasHQ(@Nonnull UUID player) {
        Validate.notNull(player, "player is null");
        Validate.isTrue(isInTeam(player), "player is not in a team");

        return getHQ(player) != null;
    }

    public static boolean areAllied(@Nonnull Team team1, @Nonnull Team team2) {
        Validate.notNull(team1, "team1 is null");
        Validate.notNull(team2, "team2 is null");

        return team1.getAllies().stream().anyMatch(ally -> ally.equals(team2));
    }

    public static void createAlly(@Nonnull Team team1, @Nonnull Team team2) {
        Validate.notNull(team1, "team1 is null");
        Validate.notNull(team2, "team2 is null");
        Validate.isTrue(!areAllied(team1, team2), "teams are already allied");

        team1.addAlly(team2);
        team2.addAlly(team1);
    }

    public static void deleteAlly(@Nonnull Team team1, @Nonnull Team team2) {
        Validate.notNull(team1, "team1 is null");
        Validate.notNull(team2, "team2 is null");
        Validate.isTrue(areAllied(team1, team2), "teams are not allied");

        team1.removeAlly(team2);
        team2.removeAlly(team1);
    }

    public static void toogleFriendlyFire(@Nonnull Team team) {
        Validate.notNull(team, "team is null");

        team.toogleFriendlyFire();
    }

}