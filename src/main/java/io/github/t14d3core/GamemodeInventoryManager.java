package io.github.t14d3core;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;
import java.util.logging.Level;

public class GamemodeInventoryManager implements Listener {
    private final JavaPlugin plugin;
    private final Map<String, GameModeInventory> inventories;
    private DataSource dataSource = null;
    private DatabaseManager dbManager;

    String url = "jdbc:mysql://localhost:3306/mydatabase";
    String username = "myuser";
    String password = "mypassword";

    public GamemodeInventoryManager(T14D3Core plugin, Map<String, GameModeInventory> inventories) throws SQLException {
        this.plugin = plugin;
        this.inventories = inventories;

        // Initialize database connection
        this.dbManager = new DatabaseManager(plugin.getConfig().getString("database.host"),
                plugin.getConfig().getInt("database.port"),
                plugin.getConfig().getString("database.database"),
                plugin.getConfig().getString("database.username"),
                plugin.getConfig().getString("database.password"), url, username, password, (MysqlDataSource) dataSource);

        this.dataSource = dbManager.getDataSource();

        // Register events
        Bukkit.getPluginManager().registerEvents(this, plugin);

        // Create table if it doesn't exist
        dbManager.executeUpdate("CREATE TABLE IF NOT EXISTS gamemode_inventory (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "player_uuid VARCHAR(36)," +
                "gamemode VARCHAR(20)," +
                "inventory TEXT," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (player_uuid) REFERENCES players(uuid)" +
                ")");
    }



    private void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateTable(String query) {
        try {
            Connection connection = dbManager.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            close(statement);
            close(connection);
        } catch (SQLException e) {
            // Handle the exception
            e.printStackTrace();
        }
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

            saveInventory(player, oldGameMode); // save the player's inventory for old gamemode
            loadInventory(player, newGameMode); // load the player's inventory for new gamemode

            // Change the player's game mode
            player.setGameMode(newGameMode);

            // Register the listener again
            Bukkit.getPluginManager().registerEvents(this, plugin);

            player.sendMessage(ChatColor.YELLOW + "Your game mode has been changed.");
        }
    }

    void saveInventory(Player player, GameMode gameMode) {
        ItemStack[] items = player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();

        // Serialize the inventory and armor contents to Base64 strings
        String invSerialized = InventorySerializer.toBase64(items);
        String armorSerialized = InventorySerializer.toBase64(armor);

        // Save the serialized inventory and armor contents to the database
        try (Connection connection = dataSource.getConnection()) {
            String query = "INSERT INTO player_inventories (player_uuid, game_mode, inventory_contents, armor_contents) VALUES (?, ?, ?, ?)" +
                    " ON DUPLICATE KEY UPDATE inventory_contents=VALUES(inventory_contents), armor_contents=VALUES(armor_contents)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, gameMode.toString());
                statement.setString(3, invSerialized);
                statement.setString(4, armorSerialized);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save inventory for " + player.getName() + " in game mode " + gameMode, e);
        }
    }

    void loadInventory(Player player, GameMode gameMode) {
        // Load the serialized inventory and armor contents from the database
        String query = "SELECT inventory_contents, armor_contents FROM player_inventories WHERE player_uuid=? AND game_mode=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, gameMode.toString());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String invSerialized = result.getString("inventory_contents");
                    String armorSerialized = result.getString("armor_contents");

                    // Deserialize the inventory and armor contents from the Base64 strings
                    ItemStack[] items = InventorySerializer.fromBase64(invSerialized);
                    ItemStack[] armor = InventorySerializer.fromBase64(armorSerialized);

                    // Set the player's inventory and armor contents to the deserialized contents
                    player.getInventory().setContents(items);
                    player.getInventory().setArmorContents(armor);
                } else {
                    // No inventory data found for this player and game mode
                    plugin.getLogger().warning("No inventory data found for " + player.getName() + " in game mode " + gameMode);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load inventory for " + player.getName() + " in game mode " + gameMode, e);
        }
    }




    /**
     * Creates the required tables in the database.
     */


    /**
     * Closes the database connection.
     */



}
