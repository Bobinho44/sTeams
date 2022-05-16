package fr.bobinho.steams.listeners;

import fr.bobinho.steams.sTeamsCore;
import fr.bobinho.steams.utils.team.Team;
import fr.bobinho.steams.utils.team.TeamManager;
import fr.bobinho.steams.utils.team.chat.Chat;
import fr.bobinho.steams.utils.team.chat.ChatManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
            return ChatColor.BLUE + "[" + team.get().getName() + "] ";
        }

        if (team.isPresent() && TeamManager.isTeammate(team.get(), viewer.getUniqueId())) {
            return ChatColor.DARK_GREEN + "[" + team.get().getName() + "] ";
        }

        return team.map(value -> ChatColor.RED + "[" + value.getName() + "] ").orElse("");
    }

    @EventHandler
    public void onPlayerSendMessage(AsyncChatEvent e) {
        if (e.isCancelled()) {
            return;
        }

        e.setCancelled(true);
        Chat chat = ChatManager.getPlayerChat(e.getPlayer().getUniqueId());

        List<Player> viewers = TeamManager.getPlayers(e.getPlayer().getUniqueId(), chat);
        String chatPrefix = chat.getPrefix();
        String prefix = (chat == Chat.PUBLIC ? sTeamsCore.getLuckPerm().getUser(e.getPlayer()).getCachedData().getMetaData().getPrefix() : null);
        String player = (chat == Chat.PUBLIC ? "" : TeamManager.getPlayerRoleSymbol(e.getPlayer().getUniqueId())) + e.getPlayer().getName();
        String message = e.getPlayer().hasPermission("steams.chatcolor") ? ChatColor.translateAlternateColorCodes('&', LegacyComponentSerializer.legacyAmpersand().serialize(e.message())) : LegacyComponentSerializer.legacyAmpersand().serialize(e.message());
        viewers.forEach(viewer -> viewer.sendMessage(chatPrefix +
                getTeamAsString(TeamManager.getTeam(e.getPlayer().getUniqueId()), viewer) + ChatColor.RESET +
                (prefix == null ? "" : ChatColor.translateAlternateColorCodes('&', getHexPrefix(prefix))) +
                (chat == Chat.PUBLIC ? "" : (chat == Chat.ALLY ? ChatColor.BLUE : ChatColor.DARK_GREEN)) +
                player + ": " +
                (chat == Chat.PUBLIC ? (ChatColor.WHITE + message) : message)));
    }

    private String getHexPrefix(String prefix) {
        StringBuilder hexPrefix = new StringBuilder();
        for (int i = 0; i < prefix.length(); i++) {
            if (prefix.charAt(i) == '#') {
                hexPrefix.append(net.md_5.bungee.api.ChatColor.of(prefix.substring(i, i + 7)).toString());
                i = i + 7;
            }
            else {
                hexPrefix.append(prefix.charAt(i));
            }
        }
        return hexPrefix.toString();
    }

}