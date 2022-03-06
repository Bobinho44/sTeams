package fr.bobinho.steams.listeners;

import fr.bobinho.steams.utils.team.Team;
import fr.bobinho.steams.utils.team.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TeamListener implements Listener {

    /**
     * Listen when a player join the server
     *
     * @param e the player join event
     */
    @EventHandler
    public void onPlayerStartCombat(PlayerJoinEvent e) {
        if (!TeamManager.isInTeam(e.getPlayer().getUniqueId())) {
            return;
        }

        //Sends join message
        TeamManager.getTeam(e.getPlayer().getUniqueId()).get().sendMessage(ChatColor.GREEN + "Teammate Online: " + ChatColor.GRAY + e.getPlayer().getName());
    }

    /**
     * Listen when a player leave the server
     *
     * @param e the player quit event
     */
    @EventHandler
    public void onPlayerStartCombat(PlayerQuitEvent e) {
        if (!TeamManager.isInTeam(e.getPlayer().getUniqueId())) {
            return;
        }

        //Sends quit message
        TeamManager.getTeam(e.getPlayer().getUniqueId()).get().sendMessage(ChatColor.RED + "Teammate Offline: " + ChatColor.GRAY + e.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerAttackPlayer(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player && e.getEntity() instanceof Player) || !TeamManager.isInTeam(e.getEntity().getUniqueId()) || !TeamManager.isInTeam(e.getDamager().getUniqueId())) {
            return;
        }

        Team victimTeam = TeamManager.getTeam(e.getEntity().getUniqueId()).get();
        Team attackerTeam = TeamManager.getTeam(e.getDamager().getUniqueId()).get();

        //Checks if attacker and victim are teammate
        if (victimTeam.equals(attackerTeam)) {
            e.setCancelled(!victimTeam.isFriendlyFire());

            //Checks if friendly fire is off
            if (e.isCancelled()) {
                e.getDamager().sendMessage(ChatColor.RED + "You cannot hurt + " + ChatColor.WHITE + e.getEntity().getName() + ".\n" +
                        ChatColor.RED + "Friendly fire is disabled between " + ChatColor.WHITE + "Team Members!");
            }
        }

        //Checks if attacker and victim are ally
        else if (TeamManager.areAllied(victimTeam, attackerTeam)) {
            e.getDamager().sendMessage(ChatColor.YELLOW + "Watch out! That’s your ally " + ChatColor.BLUE + victimTeam.getName() + ChatColor.YELLOW + ".");
        }
    }

}