package io.github.t14d3core;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;

public class T14D3Core extends JavaPlugin implements Listener {

    private DatabaseManager databaseManager;
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {

        // Load the config file
        File configFile = new File("config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // Read the database connection details from the config file
        String dbUrl = config.getString("dbUrl");
        String dbUsername = config.getString("dbUsername");
        String dbPassword = config.getString("dbPassword");

        // Initialize the database manager and inventory manager
        databaseManager = new DatabaseManager(dbUrl, dbUsername, dbPassword);
        inventoryManager = new InventoryManager(databaseManager);

        // Register the event listener
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        GameMode newGameMode = event.getNewGameMode();
        GameMode oldGameMode = player.getGameMode(); // Store the old game mode as a separate variable
        if (newGameMode == GameMode.CREATIVE && oldGameMode == GameMode.SURVIVAL) {
            // Save the player's survival inventory to a database
            UUID playerId = player.getUniqueId();
            String serializedInventory = InventorySerializer.serializeInventory(player.getInventory().getContents());
            inventoryManager.saveInventory(player, "survival", player.getInventory().getContents());
            // Load the player's creative inventory from the database
            String creativeInventory = inventoryManager.loadInventory(playerId, "creative");
            if (creativeInventory != null) {
                player.getInventory().clear();
                ItemStack[] inventoryContents = InventorySerializer.deserializeInventory(creativeInventory);
                player.getInventory().setContents(inventoryContents);
            }
        } else if (newGameMode == GameMode.SURVIVAL && oldGameMode == GameMode.CREATIVE) {
            // Save the player's creative inventory to a database
            UUID playerId = player.getUniqueId();
            String serializedInventory = InventorySerializer.serializeInventory(player.getInventory().getContents());
            inventoryManager.saveInventory(playerId, "creative", serializedInventory);
            // Load the player's survival inventory from the database
            String survivalInventory = inventoryManager.loadInventory(playerId, "survival");
            if (survivalInventory != null) {
                player.getInventory().clear();
                ItemStack[] inventoryContents = InventorySerializer.deserializeInventory(survivalInventory);
                player.getInventory().setContents(inventoryContents);
            }
        }
    }



}
