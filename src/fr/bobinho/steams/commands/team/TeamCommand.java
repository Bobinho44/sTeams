package fr.bobinho.steams.commands.team;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import fr.bobinho.steams.sTeamsCore;
import fr.bobinho.steams.utils.team.Team;
import fr.bobinho.steams.utils.team.TeamManager;
import fr.bobinho.steams.utils.team.TeamRole;
import fr.bobinho.steams.utils.team.chat.Chat;
import fr.bobinho.steams.utils.team.chat.ChatManager;
import fr.bobinho.steams.utils.team.request.RequestManager;
import net.william278.huskhomes.api.HuskHomesAPI;
import net.william278.huskhomes.player.OnlineUser;
import net.william278.huskhomes.position.Position;
import net.william278.huskhomes.position.Server;
import net.william278.huskhomes.position.World;
import net.william278.huskhomes.teleport.TimedTeleport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("team|t")
public class TeamCommand extends BaseCommand {

    private final HuskHomesAPI huskHomesAPI = HuskHomesAPI.getInstance();

    // This teleports a player to 128, 64, 128 on the server "server"
    public void teleportPlayer(Player player, Location location) {
        OnlineUser user = huskHomesAPI.adaptUser(player);

        Position position = new Position(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(),
                new World("world", location.getWorld().getUID()),
                new Server());

        huskHomesAPI.teleportBuilder(user)
                .setTarget(position)
                .toTimedTeleport()
                .thenAccept(TimedTeleport::execute);
    }

    /**
     * Command team help
     *
     * @param commandSender the sender
     */
    @Default
    @Syntax("/team Help")
    @Subcommand("Help")
    @CommandPermission("steams.team.help")
    public void onTeamHelpCommand(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;

        sender.sendMessage(ChatColor.GOLD + "/Team Help" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Show help page.\n" +
                ChatColor.GOLD + "/Team Create <TeamName>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Creates a team.\n" +
                ChatColor.GOLD + "/Team Promote <Player>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Promotes player in your team. (Mod)\n" +
                ChatColor.GOLD + "/Team Demote <Player>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Demotes player in your team. (Co-Leader)\n" +
                ChatColor.GOLD + "/Team SetHQ" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Sets team's HQ warp. (Co-Leader)\n" +
                ChatColor.GOLD + "/Team HQ" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Teleport to team HQ warp.\n" +
                ChatColor.GOLD + "/Team Desc <TeamDesc>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Sets team's description. (Co-Leader)\n" +
                ChatColor.GOLD + "/Team Invite <Player>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Invites player to your team. (Mod)\n" +
                ChatColor.GOLD + "/Team Join <TeamName>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Join team.\n" +
                ChatColor.GOLD + "/Team Leave" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Leave your team..\n" +
                ChatColor.GOLD + "/Team Ally <TeamName>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Request/accept allyship. (Mod)\n" +
                ChatColor.GOLD + "/Team Enemy <TeamName>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Add enemy. (Mod)\n" +
                ChatColor.GOLD + "/Team Neutral <TeamName>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Request/accept neutralship. (Co-Leader)\n" +
                ChatColor.GOLD + "/Team Chat" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Toggle between chat modes\n" +
                ChatColor.GOLD + "/Team Chat <public/ally/team>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Toggle a specific chat\n" +
                ChatColor.GOLD + "/Team <Info/Show> <TeamName> " + ChatColor.AQUA + " - " + ChatColor.GREEN + " Show basic info of team.\n" +
                ChatColor.GOLD + "/Team Kick <Player> " + ChatColor.AQUA + " - " + ChatColor.GREEN + " Kick player from your team. (Mod)\n" +
                ChatColor.GOLD + "/Team Disband " + ChatColor.AQUA + " - " + ChatColor.GREEN + " Disband your team. (Leader)\n" +
                ChatColor.GOLD + "/Team Rename <TeamName> " + ChatColor.AQUA + " - " + ChatColor.GREEN + " Rename your team. (Leader)\n");
    }

    /**
     * Command team create
     *
     * @param commandSender the sender
     */
    @Syntax("/team Create <TeamName>")
    @Subcommand("Create")
    @CommandPermission("steams.team.create")
    public void onTeamCreateCommand(CommandSender commandSender, String name) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;

        //Checks if sender have team
        if (TeamManager.isInTeam(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are already in a team!");
            return;
        }

        //Checks if the name is valid
        if (name.replaceAll(" ", "").length() < 3) {
            sender.sendMessage(ChatColor.RED + "The name " + name + " is to short!");
            return;
        }

        //Checks if the name is allowed
        if (sTeamsCore.getMainSettings().getConfiguration().getStringList("TeamNameNotAllowed").contains(name.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + name + " is not an allowed name!");
            return;
        }

        //Checks if name is use
        if (TeamManager.isItTeam(name)) {
            sender.sendMessage(ChatColor.RED + "The name " + name + " is already use!");
            return;
        }

        //Creates the team
        TeamManager.createTeam(name, sender.getUniqueId());

        //Sends message
        sender.sendMessage(ChatColor.GREEN + "You created the team " + name + ".");
    }

    /**
     * Command team promote
     *
     * @param commandSender the sender
     */
    @Syntax("/team Promote <Player>")
    @Subcommand("Promote")
    @CommandCompletion("@players")
    @CommandPermission("steams.team.promote")
    public void onTeamPromoteCommand(CommandSender commandSender, @Single OnlinePlayer commandTarget) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;
        Player target = commandTarget.getPlayer();

        //Checks if sender have team
        if (!TeamManager.isInTeam(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not in a team!");
            return;
        }

        //Checks if target and sender are the same player
        if (sender.equals(target)) {
            sender.sendMessage(ChatColor.RED + "You can't promote yourself!");
            return;
        }

        //Checks if target have team
        if (!TeamManager.isInTeam(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not in a team!");
            return;
        }

        //Checks if sender and target are in the same team
        if (!TeamManager.areTeammate(sender.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not in the same team as " + target.getName() + "!");
            return;
        }

        //Checks if sender can promote target
        if (!TeamManager.canPromote(sender.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You do not have sufficient rank to promote " + target.getName() + ",or he has reached the maximum rank!");
            return;
        }

        //Promotes target
        TeamManager.promote(target.getUniqueId());

        TeamRole newRole = TeamManager.getPlayerRole(target.getUniqueId());

        //Sends message
        sender.sendMessage(ChatColor.GREEN + "You promoted " + target.getName() + " to " + newRole.getName() + ".");
        target.sendMessage(ChatColor.GREEN + "You have been promoted by " + sender.getName() + " to " + newRole.getName() + ".");
    }

    /**
     * Command team demote
     *
     * @param commandSender the sender
     */
    @Syntax("/team Demote <Player>")
    @Subcommand("Demote")
    @CommandCompletion("@players")
    @CommandPermission("steams.team.demote")
    public void onTeamDemoteCommand(CommandSender commandSender, @Single OnlinePlayer commandTarget) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;
        Player target = commandTarget.getPlayer();

        //Checks if sender have team
        if (!TeamManager.isInTeam(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not in a team!");
            return;
        }

        //Checks if target and sender are the same player
        if (sender.equals(target)) {
            sender.sendMessage(ChatColor.RED + "You can't demote yourself!");
            return;
        }

        //Checks if target have team
        if (!TeamManager.isInTeam(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not in a team!");
            return;
        }

        //Checks if sender and target are in the same team
        if (!TeamManager.areTeammate(sender.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not in the same team as " + target.getName() + "!");
            return;
        }

        //Checks if sender can promote target
        if (!TeamManager.canDemote(sender.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You do not have sufficient rank to demote " + target.getName() + ",or he has reached the minimum rank!");
            return;
        }

        //Promotes target
        TeamManager.demote(target.getUniqueId());

        TeamRole newRole = TeamManager.getPlayerRole(target.getUniqueId());

        //Sends message
        sender.sendMessage(ChatColor.GREEN + "You demoted " + target.getName() + " to " + newRole.getName() + ".");
        target.sendMessage(ChatColor.RED + "You have been demoted by " + sender.getName() + " to " + newRole.getName() + "!");
    }

    /**
     * Command team sethq
     *
     * @param commandSender the sender
     */
    @Syntax("/team SetHQ")
    @Subcommand("SetHQ")
    @CommandPermission("steams.team.sethq")
    public void onTeamSetHQCommand(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;

        //Checks if sender have team
        if (!TeamManager.isInTeam(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not in a team!");
            return;
        }

        //Checks if sender is at least a co-leader
        if (!TeamManager.isAtLeastCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not at least a co-leader of your team!");
            return;
        }

        //Sets HQ
        TeamManager.setHQ(sender.getUniqueId(), sender.getLocation());

        //Sends message
        TeamManager.getTeam(sender.getUniqueId()).ifPresent(team -> team.sendMessage(ChatColor.AQUA + sender.getName() + " has updated your team’s HQ point!"));
        sender.sendMessage(ChatColor.AQUA + "Headquarters set!");
    }

    /**
     * Command team hq
     *
     * @param commandSender the sender
     */
    @Syntax("/team HQ")
    @Subcommand("HQ")
    @CommandPermission("steams.team.hq")
    public void onTeamHQCommand(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;

        //Checks if sender have team
        if (!TeamManager.isInTeam(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not in a team!");
            return;
        }

        //Checks if is HQ is set
        if (!TeamManager.hasHQ(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Your team does not have a HQ!");
            return;
        }

        //Teleports sender
        teleportPlayer(sender, TeamManager.getHQ(sender.getUniqueId()));
    }

    /**
     * Command team invite
     *
     * @param commandSender the sender
     */
    @Syntax("/team Invite <Player>")
    @Subcommand("Invite")
    @CommandCompletion("@players")
    @CommandPermission("steams.team.invite")
    public void onTeamInviteCommand(CommandSender commandSender, @Single OnlinePlayer commandTarget) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;
        Player target = commandTarget.getPlayer();

        //Checks if sender have team
        if (!TeamManager.isInTeam(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not in a team!");
            return;
        }

        //Checks if target have team
        if (TeamManager.isInTeam(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is already in a team!");
            return;
        }

        //Checks if sender is at least a mod
        if (!TeamManager.isAtLeastMod(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not at least a mod of your team!");
            return;
        }

        TeamManager.getTeam(sender.getUniqueId()).ifPresent(team -> {

            //Checks if target already has a join request
            if (RequestManager.hasJoinRequest(team, target.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You have already sent an invitation to " + target.getName() + "!");
                return;
            }

            //Sends join request
            RequestManager.sendJoinRequest(team, target.getUniqueId());

            //Sends message
            sender.sendMessage(ChatColor.GREEN + "You have invited " + target.getName() + " to your team.");
            target.sendMessage(ChatColor.GREEN + sender.getName() + " invited you to join the " + team.getName() + " team.");
        });
    }

    /**
     * Command team join
     *
     * @param commandSender the sender
     */
    @Syntax("/team Join <TeamName>")
    @Subcommand("Join")
    @CommandPermission("steams.team.join")
    public void onTeamJoinCommand(CommandSender commandSender, @Single String commandTarget) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;

        if (!TeamManager.isItTeam(commandTarget)) {
            sender.sendMessage(ChatColor.RED + "The " + commandTarget + " team does not exist!");
            return;
        }

        TeamManager.getTeam(commandTarget).ifPresent(target -> {

            //Checks if sender have team
            if (TeamManager.isInTeam(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You are already in a team!");
                return;
            }

            //Checks if sender already has a join request
            if (!RequestManager.hasJoinRequest(target, sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You have not received an invitation from the " + target.getName() + " team!");
                return;
            }

            //Sends message
            sender.sendMessage(ChatColor.GREEN + "You have joined the " + target.getName() + " team.");
            target.sendMessage(ChatColor.GREEN + sender.getName() + " joined the team.");

            //Accepts join request
            RequestManager.acceptJoinRequest(target, sender.getUniqueId());
        });
    }

    /**
     * Command team leave
     *
     * @param commandSender the sender
     */
    @Syntax("/team Leave")
    @Subcommand("Leave")
    @CommandPermission("steams.team.leave")
    public void onTeamLeaveCommand(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;

        //Checks if sender have team
        if (!TeamManager.isInTeam(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not in a team!");
            return;
        }

        //Checks if the sender is the leader
        if (TeamManager.isAtLeastLeader(sender.getUniqueId()) || TeamManager.isAlone(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You cannot leave your team because you are the owner. Do " + ChatColor.YELLOW + "/team disband " + ChatColor.RED + "to disband your team.");
            return;
        }

        TeamManager.getTeam(sender.getUniqueId()).ifPresent(team -> {

            //Leaves team
            TeamManager.leaveTeam(team, sender.getUniqueId());

            //Sends message
            sender.sendMessage(ChatColor.GREEN + sender.getName() + "You have left the " + team.getName() + " team.");
            team.sendMessage(ChatColor.GREEN + sender.getName() + "  has left the team.");
        });
    }

    /**
     * Command team ally
     *
     * @param commandSender the sender
     */
    @Syntax("/team Ally <TeamName>")
    @CommandCompletion("@players")
    @Subcommand("Ally")
    @CommandPermission("steams.team.ally")
    public void onTeamAllyCommand(CommandSender commandSender, @Single String commandTarget) {
        if (!(commandSender instanceof Player)) {
            return;
        }

        Player sender = (Player) commandSender;

        OfflinePlayer TeamTarget = Bukkit.getOfflinePlayerIfCached(commandTarget);

        if (!TeamManager.isItTeam(commandTarget) && TeamTarget == null) {
            sender.sendMessage(ChatColor.RED + "The " + commandTarget + " team does not exist!");
            return;
        }

        if (TeamTarget != null && !TeamManager.isInTeam(TeamTarget.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + TeamTarget.getName() + " doesn't have a team!");
            return;
        }

        Team target = (TeamManager.isItTeam(commandTarget) ? TeamManager.getTeam(commandTarget) : TeamManager.getTeam(TeamTarget.getUniqueId())).get();

        TeamManager.getTeam(sender.getUniqueId()).ifPresent(team -> {

            if (team.equals(target)) {
                    sender.sendMessage(ChatColor.RED + "You cannot be allied with your own team!");
                    return;
                }

                if (TeamManager.areAllied(team, target)) {
                    sender.sendMessage(ChatColor.RED + "You are already allied with the team " + target.getName() + "!");
                    return;
                }

                //Checks if sender is at least a mod
                if (!TeamManager.isAtLeastMod(sender.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "You are not at least a mod of your team!");
                    return;
                }

                //Checks if team already send an ally request to target
                if (RequestManager.hasAllyRequest(team, target)) {
                    sender.sendMessage(ChatColor.RED + "You have already sent an alliance invitation to the " + target.getName() + " team!");
                    return;
                }

                //Checks if team has an ally request from target
                if (RequestManager.hasAllyRequest(target, team)) {

                    //Sends message
                    team.sendMessage(ChatColor.GREEN + "You are now allied with the " + target.getName() + " team.");
                    target.sendMessage(ChatColor.GREEN + "You are now allied with the " + team.getName() + " team.");

                    //Accepts join request
                    RequestManager.acceptAllyRequest(target, team);
                    return;
                }

                //Sends ally request
                RequestManager.sendAllyRequest(team, target);

                //Sends message
                sender.sendMessage(ChatColor.GREEN + "You have sent an alliance request to the " + target.getName() + " team.");
                target.sendMessage(ChatColor.GREEN + "You have received an alliance request from the " + team.getName() + " team.");
            });
    }

    /**
     * Command team ally
     *
     * @param commandSender the sender
     */
    @Syntax("/team Neutral <TeamName>")
    @CommandCompletion("@players")
    @Subcommand("Neutral")
    @CommandPermission("steams.team.neutral")
    public void onTeamNeutralCommand(CommandSender commandSender, @Single String commandTarget) {
        if (!(commandSender instanceof Player)) {
            return;
        }

        Player sender = (Player) commandSender;

        OfflinePlayer TeamTarget = Bukkit.getOfflinePlayerIfCached(commandTarget);

        if (!TeamManager.isItTeam(commandTarget) && TeamTarget == null) {
            sender.sendMessage(ChatColor.RED + "The " + commandTarget + " team does not exist!");
            return;
        }

        if (TeamTarget != null && !TeamManager.isInTeam(TeamTarget.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + TeamTarget.getName() + " doesn't have a team!");
            return;
        }

        Team target = (TeamManager.isItTeam(commandTarget) ? TeamManager.getTeam(commandTarget) : TeamManager.getTeam(TeamTarget.getUniqueId())).get();

        TeamManager.getTeam(sender.getUniqueId()).ifPresent(team -> {

            if (team.equals(target)) {
                sender.sendMessage(ChatColor.RED + "You cannot be neutral with your own team!");
                return;
            }

            //Checks if sender is at least a co leader
            if (!TeamManager.isAtLeastCoLeader(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You are not at least the co-leader of your team!");
                return;
            }

            //Checks if teams are allied or enemy
            if (!TeamManager.areAllied(team, target) && !TeamManager.areEnemy(team, target)) {
                sender.sendMessage(ChatColor.RED + "You are already neutral with the " + target.getName() + " team!");
                return;
            }

            if (TeamManager.areAllied(team, target)) {
                //Sends message
                team.sendMessage(ChatColor.GREEN + "You are no longer allied with the " + target.getName() + " team.");
                target.sendMessage(ChatColor.GREEN + "You are no longer allied with the " + team.getName() + " team.");

                //Deletes team
                TeamManager.deleteAlly(team, target);
                return;
            }

            //Checks if team already send a neutral request to target
            if (RequestManager.hasNeutralRequest(team, target)) {
                sender.sendMessage(ChatColor.RED + "You have already sent a neutral invitation to the " + target.getName() + " team!");
                return;
            }

            //Checks if team has a neutral request from target
            if (RequestManager.hasNeutralRequest(target, team)) {

                //Sends message
                team.sendMessage(ChatColor.GREEN + "You are now neutral with the " + target.getName() + " team.");
                target.sendMessage(ChatColor.GREEN + "You are now neutral with the " + team.getName() + " team.");

                //Accepts join request
                RequestManager.acceptNeutralRequest(target, team);
                return;
            }

            //Sends neutral request
            RequestManager.sendNeutralRequest(team, target);

            //Sends message
            sender.sendMessage(ChatColor.GREEN + "You have sent a neutral request to the " + target.getName() + " team.");
            target.sendMessage(ChatColor.GREEN + "You have received a neutral request from the " + team.getName() + " team.");
        });
    }

    /**
     * Command team disband
     *
     * @param commandSender the sender
     */
    @Syntax("/team Enemy <TeamName>")
    @CommandCompletion("@players")
    @Subcommand("Enemy")
    @CommandPermission("steams.team.enemy")
    public void onTeamEnemyCommand(CommandSender commandSender, @Single String commandTarget) {
        if (!(commandSender instanceof Player)) {
            return;
        }

        Player sender = (Player) commandSender;

        OfflinePlayer TeamTarget = Bukkit.getOfflinePlayerIfCached(commandTarget);

        if (!TeamManager.isItTeam(commandTarget) && TeamTarget == null) {
            sender.sendMessage(ChatColor.RED + "The " + commandTarget + " team does not exist!");
            return;
        }

        if (TeamTarget != null && !TeamManager.isInTeam(TeamTarget.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + TeamTarget.getName() + " doesn't have a team!");
            return;
        }

        Team target = (TeamManager.isItTeam(commandTarget) ? TeamManager.getTeam(commandTarget) : TeamManager.getTeam(TeamTarget.getUniqueId())).get();

        TeamManager.getTeam(sender.getUniqueId()).ifPresent(team -> {

            //Checks if the sender is the leader
            if (!TeamManager.isAtLeastMod(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You are not at least the mod of your team!");
                return;
            }

            //Checks if teams are allied
            if (TeamManager.areEnemy(team, target)) {
                sender.sendMessage(ChatColor.RED + "You are already enemy with the " + target.getName() + " team!");
                return;
            }

            //Sends message
            team.sendMessage(ChatColor.GREEN + "You are now enemy with the " + target.getName() + " team.");
            target.sendMessage(ChatColor.GREEN + "You are now enemy with the " + team.getName() + " team.");

            //Set enemy
            TeamManager.createEnemy(team, target);
        });
    }

    /**
     * Command team chat
     *
     * @param commandSender the sender
     */
    @Syntax("/team Chat [public/ally/team]")
    @Subcommand("Chat")
    @CommandPermission("steams.team.chat")
    public void onTeamChatCommand(CommandSender commandSender, @Optional String commandTarget) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;

        //Checks if the chat is valid
        if (commandTarget != null && !Chat.isValidChat(commandTarget)) {
            sender.sendMessage(ChatColor.RED + "The " + commandTarget + " chat does not exist!");
            return;
        }

        Chat chat = commandTarget == null ? Chat.getNextChat(ChatManager.getPlayerChat(sender.getUniqueId())) : Chat.valueOf(commandTarget.toUpperCase());

        //Checks if sender has team
        if (!TeamManager.isInTeam(sender.getUniqueId()) && chat != Chat.PUBLIC) {
            sender.sendMessage(ChatColor.RED + "You can't change your chat without a team!");
            return;
        }

        //Checks if sender has team
        if (!TeamManager.isInTeam(sender.getUniqueId()) && chat != Chat.PUBLIC) {
            sender.sendMessage(ChatColor.RED + "You can't change your chat without a team!");
            return;
        }

        //Skips ally chat
        if (chat == Chat.ALLY && TeamManager.getTeam(sender.getUniqueId()).get().getAllies().size() == 0) {

            //Checks if sender team has allies
            if (commandTarget != null) {
                sender.sendMessage(ChatColor.RED + "You can't change your chat to ally chat without allies!");
                return;
            }

            chat = Chat.TEAM;
        }

        //Sets sender chat
        ChatManager.setPlayerChat(sender.getUniqueId(), chat);

        //Sends message
        sender.sendMessage(ChatColor.GREEN + "You are now in " + chat.getName() + ChatColor.GREEN + " chat.");

    }

    /**
     * Command team info
     *
     * @param commandSender the sender
     */
    @Syntax("/team <Info/Show> <TeamName>")
    @Subcommand("Info|Show")
    @CommandCompletion("@players")
    @CommandPermission("steams.team.info")
    public void onTeamInfoCommand(CommandSender commandSender, @Single String commandTarget) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;

        OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(commandTarget);

        if (!TeamManager.isItTeam(commandTarget) && target == null) {
            sender.sendMessage(ChatColor.RED + "The " + commandTarget + " team does not exist!");
            return;
        }

        if (target != null && !TeamManager.isInTeam(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " doesn't have a team!");
            return;
        }

        Team team = (TeamManager.isItTeam(commandTarget) ? TeamManager.getTeam(commandTarget) : TeamManager.getTeam(target.getUniqueId())).get();

        //Sends message
        sender.sendMessage(team.getInformations(sender.getUniqueId()));
    }

    /**
     * Command team kick
     *
     * @param commandSender the sender
     */
    @Syntax("/team Kick <Player>")
    @Subcommand("Kick")
    @CommandCompletion("@players")
    @CommandPermission("steams.team.demote")
    public void onTeamKickCommand(CommandSender commandSender, @Single String commandTarget) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;
        OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(commandTarget);

        if (!TeamManager.isItTeam(commandTarget) && target == null) {
            sender.sendMessage(ChatColor.RED + "The " + commandTarget + " team does not exist!");
            return;
        }

        //Checks if sender have team
        if (!TeamManager.isInTeam(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not in a team!");
            return;
        }

        //Checks if target and sender are the same player
        if (sender.equals(target)) {
            sender.sendMessage(ChatColor.RED + "You can't kick yourself!");
            return;
        }

        //Checks if target have team
        if (!TeamManager.isInTeam(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not in a team!");
            return;
        }

        //Checks if sender and target are in the same team
        if (!TeamManager.areTeammate(sender.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not in the same team as " + target.getName() + "!");
            return;
        }

        //Checks if sender can promote target
        if (!TeamManager.canKick(sender.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You do not have sufficient rank to kick " + target.getName() + "!");
            return;
        }

        //Kicks target
        Team team = TeamManager.getTeam(sender.getUniqueId()).get();
        TeamManager.leaveTeam(team, target.getUniqueId());

        //Sends message
        team.sendMessage(ChatColor.GREEN + target.getName() + " has been excluded from the team.");

        if (Bukkit.getPlayer(target.getUniqueId()) != null) {
            Bukkit.getPlayer(target.getUniqueId()).sendMessage(ChatColor.RED + "You have been excluded from the " + team.getName() + " team!");
        }
    }

    /**
     * Command team disband
     *
     * @param commandSender the sender
     */
    @Syntax("/team Disband")
    @Subcommand("Disband")
    @CommandPermission("steams.team.disband")
    public void onTeamDisbandCommand(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;

        //Checks if sender have team
        if (!TeamManager.isInTeam(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not in a team!");
            return;
        }

        //Checks if the sender is the leader
        if (!TeamManager.isAtLeastLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not the leader of your team!");
            return;
        }

        Team team = TeamManager.getTeam(sender.getUniqueId()).get();

        //Sends message
        team.sendMessage(ChatColor.RED + "Your team has been disbanded!");

        //Deletes team
        TeamManager.deleteTeam(team.getName());
    }

    /**
     * Command team rename
     *
     * @param commandSender the sender
     */
    @Syntax("/team Rename <TeamName>")
    @Subcommand("Rename")
    @CommandPermission("steams.team.rename")
    public void onTeamRenameCommand(CommandSender commandSender, String name) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;

        //Checks if sender have team
        if (!TeamManager.isInTeam(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not in a team!");
            return;
        }

        //Checks if the sender is a leader of his team
        if (!TeamManager.isAtLeastLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not the leader of your team!");
            return;
        }

        //Checks if the name is valid
        if (name.replaceAll(" ", "").length() < 3) {
            sender.sendMessage(ChatColor.RED + "The name " + name + " is to short!");
            return;
        }

        //Checks if the name is allowed
        if (sTeamsCore.getMainSettings().getConfiguration().getStringList("TeamNameNotAllowed").contains(name.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + name + " is not an allowed name!");
            return;
        }

        //Checks if name is use
        if (TeamManager.isItTeam(name)) {
            sender.sendMessage(ChatColor.RED + "The name " + name + " is already use!");
            return;
        }

        //Creates the team
        TeamManager.changeTeamName(TeamManager.getTeam(sender.getUniqueId()).get(), name);

        //Sends message
        sender.sendMessage(ChatColor.GREEN + "you have renamed your team to " + name + ".");
    }

    /**
     * Command team rename
     *
     * @param commandSender the sender
     */
    @Syntax("/team Setleader <TeamName>")
    @Subcommand("Setleader")
    @CommandPermission("steams.team.setleader")
    public void onTeamSetleaderCommand(CommandSender commandSender, @Single String commandTarget) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;
        OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(commandTarget);

        if (target == null || TeamManager.areTeammate(sender.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + commandTarget + " Is not in your team!");
            return;
        }

        //Checks if sender have team
        if (!TeamManager.isInTeam(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not in a team!");
            return;
        }

        //Checks if the sender is a leader of his team
        if (!TeamManager.isAtLeastLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not the leader of your team!");
            return;
        }

        //Sets the new leader
        TeamManager.setLeader(target.getUniqueId());

        //Sends message
        TeamManager.getTeam(sender.getUniqueId()).get().sendMessage(ChatColor.GREEN + target.getName() + " is the new leader of your team!");
    }

    /**
     * Command team rename
     *
     * @param commandSender the sender
     */
    @Syntax("/team desc <desc>")
    @Subcommand("desc")
    @CommandPermission("steams.team.desc")
    public void onTeamDescCommand(CommandSender commandSender, String desc) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player sender = (Player) commandSender;

        //Checks if sender have team
        if (!TeamManager.isInTeam(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not in a team!");
            return;
        }

        //Checks if the sender is a leader of his team
        if (!TeamManager.isAtLeastCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not at least the co-leader of your team!");
            return;
        }

        //Sets the new leader
        TeamManager.changeTeamDescription(TeamManager.getTeam(sender.getUniqueId()).get(), desc);

        //Sends message
        TeamManager.getTeam(sender.getUniqueId()).get().sendMessage(ChatColor.GREEN + "The team description has been changed! (" + ChatColor.translateAlternateColorCodes('&', desc) + ChatColor.GREEN + ")");
    }

}