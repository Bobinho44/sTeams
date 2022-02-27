package fr.bobinho.steams.commands.team;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("teams")
public class TeamsCommand extends BaseCommand {

    /**
     * Command teams
     *
     * @param commandSender the sender
     */
    @Default
    @Syntax("/teams")
    @CommandPermission("sutils.team.help")
    public void onTeamsCommand(CommandSender commandSender) {
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
                ChatColor.GOLD + "/Team Invite <Player>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Invites player to your team. (Mod)\n" +
                ChatColor.GOLD + "/Team Join <TeamName>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Join team.\n" +
                ChatColor.GOLD + "/Team Leave" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Leave your team..\n" +
                ChatColor.GOLD + "/Team Ally <TeamName>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Request/accept allyship. (Mod)\n" +
                ChatColor.GOLD + "/Team Unally <TeamName>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " remove ally. (Co-Leader)\n" +
                ChatColor.GOLD + "/Team Chat" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Toggle between chat modes\n" +
                ChatColor.GOLD + "/Team Chat <public/ally/team>" + ChatColor.AQUA + " - " + ChatColor.GREEN + " Toggle a specific chat\n" +
                ChatColor.GOLD + "/Team <Info/Show> <TeamName> " + ChatColor.AQUA + " - " + ChatColor.GREEN + " Show basic info of team.\n" +
                ChatColor.GOLD + "/Team FriendlyFire " + ChatColor.AQUA + " - " + ChatColor.GREEN + " Toggles team friendly fire. (Mod)\n" +
                ChatColor.GOLD + "/Team Kick <Player> " + ChatColor.AQUA + " - " + ChatColor.GREEN + " Kick player from your team. (Mod)\n" +
                ChatColor.GOLD + "/Team Disband " + ChatColor.AQUA + " - " + ChatColor.GREEN + " Disband your team. (Leader)\n");
    }

}