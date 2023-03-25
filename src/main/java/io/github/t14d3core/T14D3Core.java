package io.github.t14d3core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class T14D3Core extends JavaPlugin {

    private DatabaseManager dbManager;
    private Map<String, GamemodeInventoryManager> inventoryManagers;
    Plugin plugin = this;


    @Override
    public void onEnable() {
        // Create a new instance of DatabaseManager and initialize it
        dbManager = new DatabaseManager("localhost", "3306", "minecraft", "root", "password");
        try {
            dbManager.initialize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    // Create a new HashMap to hold the inventory managers for each player
        inventoryManagers = new HashMap<>();

        // Register the plugin listener
        try {
            getServer().getPluginManager().registerEvents(new GamemodeInventoryManager((T14D3Core) inventoryManagers, (Map<String, GameModeInventory>) plugin), this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Load the inventory managers for all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            GamemodeInventoryManager inventoryManager = null;
            try {
                inventoryManager = new GamemodeInventoryManager(this, (Map<String, GameModeInventory>) dbManager);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            inventoryManagers.put(player.getUniqueId().toString(), inventoryManager);
            inventoryManager.loadInventory(player, player.getGameMode());
        }
    }

    @Override
    public void onDisable() {
        // Save the inventory data for all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            GamemodeInventoryManager inventoryManager = inventoryManagers.get(player.getUniqueId().toString());
            if (inventoryManager != null) {
                inventoryManager.saveInventory(player, player.getGameMode());
                inventoryManager.close();
            }
        }

        // Close the database connection
        try {
            dbManager.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        // Load the inventory manager for the joining player
        GamemodeInventoryManager inventoryManager = new GamemodeInventoryManager(this, plugin);
        inventoryManagers.put(event.getPlayer().getUniqueId().toString(), inventoryManager);
        inventoryManager.loadInventory(event.getPlayer(), event.getPlayer().getGameMode());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save the inventory of the quitting player and remove their inventory manager
        GamemodeInventoryManager inventoryManager = inventoryManagers.remove(event.getPlayer().getUniqueId().toString());
        if (inventoryManager != null) {
            inventoryManager.saveInventory(event.getPlayer(), event.getPlayer().getGameMode());
        }
    }
}