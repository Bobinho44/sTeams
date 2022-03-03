package fr.bobinho.steams.utils.team;

import fr.bobinho.steams.sTeamsCore;
import fr.bobinho.steams.utils.location.BLocationUtil;
import fr.bobinho.steams.utils.team.chat.Chat;
import fr.bobinho.steams.utils.team.chat.ChatManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

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

    public static Team createTeam(@Nonnull String name) {
        Validate.notNull(name, "name is null");
        Validate.isTrue(!isItTeam(name), "name is already used");

        Team team = new Team(name);
        getTeams().add(team);
        return team;
    }

    public static void deleteTeam(@Nonnull String name) {
        Validate.notNull(name, "name is null");
        Validate.isTrue(isItTeam(name), "name is not used");

        getTeam(name).get().getMembers().keySet().stream().collect(Collectors.toList()).forEach(member -> leaveTeam(getTeam(name).get(), member));
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

        ChatManager.setPlayerChat(member, Chat.PUBLIC);
        team.removeMember(member);
    }

    public static boolean canKick(@Nonnull UUID kicker, @Nonnull UUID kicked) {
        Validate.notNull(kicker, "kicker is null");
        Validate.notNull(kicked, "kicked is null");
        Validate.isTrue(isInTeam(kicker), "kicker is not in a team");
        Validate.isTrue(isInTeam(kicked), "kicked is not in a team");
        Validate.isTrue(getTeam(kicker).get().equals(getTeam(kicked).get()), "kicker and kicked are not in the same team");

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


    public static String getPlayerRoleSymbol(@Nonnull UUID player) {
        Validate.notNull(player, "player is null");

        return isInTeam(player) ? getPlayerRole(player).getSymbol() : "";
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

    public static boolean isAllied(@Nonnull Team team, @Nonnull UUID player) {
        Validate.notNull(team, "team is null");
        Validate.notNull(player, "player is null");

        return team.getAllies().stream().map(Team::getMembers).anyMatch(members -> members.keySet().stream().anyMatch(member -> member.equals(player)));
    }

    public static boolean isTeammate(@Nonnull Team team, @Nonnull UUID player) {
        Validate.notNull(team, "team is null");
        Validate.notNull(player, "player is null");

        return team.getMembers().keySet().stream().anyMatch(member -> member.equals(player));
    }

    public static List<Player> getPlayers(@Nonnull UUID basePlayer, @Nonnull Chat chat) {
        Validate.notNull(basePlayer, "player is null");
        Validate.notNull(chat, "chat is null");

        Optional<Team> team = getTeam(basePlayer);

        if (team.isPresent() && chat == Chat.ALLY) {
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player -> isAllied(team.get(), player.getUniqueId()) || isTeammate(team.get(), player.getUniqueId())).collect(Collectors.toList());
        }

        if (team.isPresent() && chat == Chat.TEAM) {
            return Bukkit.getOnlinePlayers().stream().filter(player -> isTeammate(team.get(), player.getUniqueId())).collect(Collectors.toList());
        }

        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    public static void loadTeams() {
        YamlConfiguration configuration = sTeamsCore.getTeamsSettings().getConfiguration();

        //Loads teams
        for (String teamName : configuration.getKeys(false)) {

            Team team = createTeam(teamName);
            for (String memberUUID : Objects.requireNonNull(configuration.getConfigurationSection(teamName + ".members")).getKeys(false)) {
                team.addMember(UUID.fromString(memberUUID), TeamRole.valueOf(configuration.getString(teamName + ".members." + memberUUID)));
            }
            if (configuration.getString(teamName + ".HQ") != null) {
                team.setHQ(BLocationUtil.getAsLocation(configuration.getString(teamName + ".HQ", "world:0:0:0:0:0")));
            }
            if (configuration.getBoolean(teamName + ".isFriendlyFire")) {
                team.toogleFriendlyFire();
            }
        }

        for (Team team : getTeams()) {
            List<String> alliesName = configuration.getStringList(team.getName() + ".allies");

            for (String allyName : alliesName) {
                if (!areAllied(team, getTeam(allyName).get())) {
                    createAlly(team, getTeam(allyName).get());
                }
            }
        }
    }

    public static void saveTeams() {
        YamlConfiguration configuration = sTeamsCore.getTeamsSettings().getConfiguration();
        sTeamsCore.getTeamsSettings().clear();

        //Saves teams
        for (Team team : getTeams()) {

            //Saves members
            for (Map.Entry<UUID, TeamRole> member : team.getMembers().entrySet()) {
                configuration.set(team.getName() + ".members." + member.getKey(), member.getValue().name());
            }

            //Saves allies
            configuration.set(team.getName() + ".allies", team.getAllies().stream().map(Team::getName).collect(Collectors.toList()));

            //Saves HQ
            configuration.set(team.getName() + ".HQ", team.getHQ() == null ? null : BLocationUtil.getAsString(team.getHQ()));

            //Saves friendly fire statue
            configuration.set(team.getName() + ".isFriendlyFire", team.isFriendlyFire());
        }

        sTeamsCore.getTeamsSettings().save();
    }

}