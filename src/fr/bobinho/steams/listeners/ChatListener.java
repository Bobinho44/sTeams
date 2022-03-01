package fr.bobinho.steams.listeners;

import fr.bobinho.steams.utils.team.Team;
import fr.bobinho.steams.utils.team.TeamManager;
import fr.bobinho.steams.utils.team.chat.Chat;
import fr.bobinho.steams.utils.team.chat.ChatManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class ChatListener implements Listener {

    private String getTeamAsString(@Nonnull Optional<Team> team, @Nonnull Player viewer) {
        if (team.isPresent() && TeamManager.isAllied(team.get(), viewer.getUniqueId())) {
            return ChatColor.GRAY + "[" + team.get().getName() + "] ";
        }

        if (team.isPresent() && TeamManager.isTeammate(team.get(), viewer.getUniqueId())) {
            return ChatColor.BLUE + "[" + team.get().getName() + "] ";
        }

        return team.map(value -> ChatColor.RED + "[" + value.getName() + "] ").orElse("");
    }

    @EventHandler
    public void onPlayerSendMessage(AsyncChatEvent e) {
        e.setCancelled(true);
        Chat chat = ChatManager.getPlayerChat(e.getPlayer().getUniqueId());

        List<Player> viewers = TeamManager.getPlayers(e.getPlayer().getUniqueId(), chat);
        String chatPrefix = chat.getPrefix();
        String prefix = LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(e.getPlayer()).getCachedData().getMetaData().getPrefix();
        String player = TeamManager.getPlayerRoleSymbol(e.getPlayer().getUniqueId()) + e.getPlayer().getName();
        String message = ChatColor.translateAlternateColorCodes('&', LegacyComponentSerializer.legacyAmpersand().serialize(e.message()));
        viewers.forEach(viewer -> viewer.sendMessage(chatPrefix +
                getTeamAsString(TeamManager.getTeam(e.getPlayer().getUniqueId()), viewer) + ChatColor.RESET +
                (prefix == null ? "" : prefix + " ") + (chat == Chat.PUBLIC ? "" : (chat == Chat.ALLY ? ChatColor.GRAY : ChatColor.BLUE)) +
                player + " : " +
                message));
    }
}