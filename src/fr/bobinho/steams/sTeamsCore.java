package fr.bobinho.steams;

import co.aikar.commands.PaperCommandManager;
import fr.bobinho.steams.commands.team.TeamCommand;
import fr.bobinho.steams.commands.team.TeamsCommand;
import fr.bobinho.steams.listeners.ChatListener;
import fr.bobinho.steams.listeners.TeamListener;
import fr.bobinho.steams.utils.settings.BSettings;
import fr.bobinho.steams.utils.team.TeamManager;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.platform.PlayerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nonnull;

public class sTeamsCore extends JavaPlugin {

    /**
     * Fields
     */
    private static sTeamsCore instance;
    private static BSettings mainSettings;
    private static BSettings teamsSettings;
    private static PlayerAdapter<Player> luckperm;

    /**
     * Gets the sutils core instance
     *
     * @return the sutils core instance
     */
    @Nonnull
    public static sTeamsCore getInstance() {
        return instance;
    }

    /**
     * Gets the main settings
     *
     * @return the main settings
     */
    @Nonnull
    public static BSettings getMainSettings() {
        return mainSettings;
    }

    /**
     * Gets the teams settings
     *
     * @return the teams settings
     */
    @Nonnull
    public static BSettings getTeamsSettings() {
        return teamsSettings;
    }

    public static PlayerAdapter<Player>  getLuckPerm() {
        return luckperm;
    }
    /**
     * Enable and initialize the plugin
     */
    public void onEnable() {
        instance = this;

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[sTeams] Loading the plugin...");

        //Registers commands and listeners
        registerCommands();
        registerListeners();

        //Registers files settings
        mainSettings = new BSettings("settings");
        teamsSettings = new BSettings("teams");
        luckperm = LuckPermsProvider.get().getPlayerAdapter(Player.class);

        TeamManager.loadTeams();
    }

    /**
     * Disable the plugin and save data
     */
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[sTeams] Unloading the plugin...");

        TeamManager.saveTeams();
    }

    /**
     * Register listeners
     */
    private void registerListeners() {

        //Registers test listener
        Bukkit.getServer().getPluginManager().registerEvents(new TeamListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatListener(), this);

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Successfully loaded listeners");
    }

    /**
     * Register commands
     */
    private void registerCommands() {
        final PaperCommandManager commandManager = new PaperCommandManager(this);

        //Registers teams command
        commandManager.registerCommand(new TeamCommand());
        commandManager.registerCommand(new TeamsCommand());

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Successfully loaded commands");
    }

}