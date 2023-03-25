package io.github.t14d3core;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private final MysqlDataSource dataSource;
    private Connection connection;

    public DatabaseManager(String hostname, String port, String database, String username, String password) {
        dataSource = new MysqlDataSource();
        dataSource.setServerName(hostname);
        dataSource.setPort(Integer.parseInt(port));
        dataSource.setDatabaseName(database);
        dataSource.setUser(username);
        dataSource.setPassword(password);
    }

    public DatabaseManager(String string, int anInt, String string1, String string2, String string3, String url, String username, String password, MysqlDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void initialize() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS gamemode_inventory (" +
                    "player_uuid VARCHAR(36) NOT NULL, " +
                    "gamemode VARCHAR(10) NOT NULL, " +
                    "inventory TEXT NOT NULL, " +
                    "timestamp TIMESTAMP NOT NULL, " +
                    "PRIMARY KEY (player_uuid, gamemode)" +
                    ")");
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public DataSource getDataSource() {
        return null;
    }
    public void close() throws SQLException {
        connection.close();
    }


    public void executeUpdate(String s) {
    }
}
