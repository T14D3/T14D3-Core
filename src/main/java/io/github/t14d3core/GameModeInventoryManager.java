package io.github.t14d3core;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.sql.*;
import java.util.*;

public class GameModeInventoryManager {
    private final JavaPlugin plugin;
    private final Gson gson = new Gson();
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public GameModeInventoryManager(JavaPlugin plugin) {
        this.plugin = plugin;
        // register events
        plugin.getServer().getPluginManager().registerEvents(new GameModeChangeListener(plugin), plugin);
    }


    private Connection getConnection() throws SQLException {
        // Create a new connection to your database
        Connection connection = DriverManager.getConnection("jdbc:mysql://172.18.0.1/testdb", "server", "IbTSadN3");

        // Return the connection
        return connection;
    }


    private PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData(player));
    }

    private void savePlayerData(Player player) {
        PlayerData playerData = getPlayerData(player);
        try (Connection connection = getConnection()) {
            // delete existing data
            String deleteSql = "DELETE FROM player_inventory WHERE uuid = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
                deleteStatement.setString(1, player.getUniqueId().toString());
                deleteStatement.executeUpdate();
            }

            // insert new data
            String insertSql = "INSERT INTO player_inventory (uuid, game_mode, inventory, armor, offhand, experience, total_experience, level, health, food_level, saturation, potion_effects) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                insertStatement.setString(1, player.getUniqueId().toString());
                insertStatement.setString(2, player.getGameMode().name());
                insertStatement.setString(3, gson.toJson(player.getInventory().getContents()));
                insertStatement.setString(4, gson.toJson(player.getInventory().getArmorContents()));
                insertStatement.setString(5, gson.toJson(player.getInventory().getItemInOffHand()));
                insertStatement.setFloat(6, player.getExp());
                insertStatement.setInt(7, player.getTotalExperience());
                insertStatement.setInt(8, player.getLevel());
                insertStatement.setDouble(9, player.getHealth());
                insertStatement.setInt(10, player.getFoodLevel());
                insertStatement.setFloat(11, player.getSaturation());
                insertStatement.setString(12, gson.toJson(player.getActivePotionEffects()));
                insertStatement.executeUpdate();
            }

            // save player data to map
            playerData.save();
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage("An error occurred while saving your inventory to the database. Please contact an administrator.");
        }
    }


    private void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        try (Connection connection = getConnection()) {
            String sql = "SELECT * FROM player_inventory WHERE uuid = ? AND game_mode = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                statement.setString(2, player.getGameMode().name());

                ResultSet resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    // Player inventory data not found, nothing to load
                    return;
                }

                ItemStack[] inventoryContents = gson.fromJson(resultSet.getString("inventory"), ItemStack[].class);
                ItemStack[] armorContents = gson.fromJson(resultSet.getString("armor"), ItemStack[].class);
                ItemStack offhand = gson.fromJson(resultSet.getString("offhand"), ItemStack.class);

                player.getInventory().setContents(inventoryContents);
                player.getInventory().setArmorContents(armorContents);
                player.getInventory().setItemInOffHand(offhand);

                player.setExp(resultSet.getFloat("experience"));
                player.setTotalExperience(resultSet.getInt("total_experience"));
                player.setLevel(resultSet.getInt("level"));
                player.setHealth(resultSet.getDouble("health"));
                player.setFoodLevel(resultSet.getInt("food_level"));
                player.setSaturation(resultSet.getFloat("saturation"));

                String potionEffectsData = resultSet.getString("potion_effects");
                List<PotionEffect> potionEffects = gson.fromJson(potionEffectsData, new TypeToken<List<PotionEffect>>() {
                }.getType());

                for (PotionEffect effect : potionEffects) {
                    player.addPotionEffect(effect);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "An error occurred while loading your inventory from the database. Please contact an administrator.");
        }
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        GameMode newGameMode = event.getNewGameMode();
        GameMode oldGameMode = player.getGameMode();

        savePlayerData(player);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            loadPlayerData(player);
            player.setGameMode(newGameMode);
        }, 1L);

        event.setCancelled(true);
    }
}
