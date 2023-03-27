package io.github.t14d3core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class DatabaseManager {
    private final String url;
    private final String username;
    private final String password;

    public DatabaseManager() throws IOException {
        // Load the config file
        File configFile = new File("config.yml");
        Yaml yaml = new Yaml(new Constructor(Config.class));
        Config config = yaml.load(new FileInputStream(configFile));

        // Set the database connection details
        this.url = config.dbUrl;
        this.username = config.dbUsername;
        this.password = config.dbPassword;

        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS inventories ("
                    + "player_id VARCHAR(36) NOT NULL,"
                    + "note VARCHAR(16) NOT NULL,"
                    + "inventory TEXT NOT NULL,"
                    + "PRIMARY KEY (player_id, note)"
                    + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveInventory(UUID playerId, String note, String serializedInventory) {
        String query = "INSERT INTO inventories (player_id, note, inventory) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE inventory = VALUES(inventory)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerId.toString());
            statement.setString(2, note);
            statement.setString(3, serializedInventory);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String loadInventory(UUID playerId, String note) {
        String query = "SELECT inventory FROM inventories WHERE player_id = ? AND note = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerId.toString());
            statement.setString(2, note);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("inventory");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class Config {
        public String dbUrl;
        public String dbUsername;
        public String dbPassword;
    }
}
