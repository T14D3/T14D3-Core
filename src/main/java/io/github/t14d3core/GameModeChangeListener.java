package io.github.t14d3core;

import io.github.t14d3core.LoadInvCommand;
import io.github.t14d3core.SaveInvCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GameModeChangeListener implements Listener {

    private final JavaPlugin plugin;

    public GameModeChangeListener(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        GameMode oldGameMode = player.getGameMode();
        GameMode newGameMode = event.getNewGameMode();
        if (oldGameMode != newGameMode) {
            event.setCancelled(true); // cancel the event

            // Unregister the listener temporarily
            HandlerList.unregisterAll(this);

            SaveInvCommand saveInvCommand = new SaveInvCommand((T14D3Core) plugin);
            saveInvCommand.saveInventory(player); // save the player's inventory

            // Change the player's game mode
            player.setGameMode(newGameMode);

            // Register the listener again
            Bukkit.getPluginManager().registerEvents(this, plugin);

            LoadInvCommand loadInvCommand = new LoadInvCommand((T14D3Core) plugin);
            loadInvCommand.loadInventory(player); // load the player's inventory
            player.sendMessage(ChatColor.YELLOW + "Your game mode has been changed.");
        }
    }

}
