package com.ollethunberg.nationsplus;

import org.bukkit.plugin.java.JavaPlugin;
import org.postgresql.Driver;

import java.sql.*;

public final class NationsPlus extends JavaPlugin {
    public Connection connection;
    public DatabaseManager databaseManager;
    private CommandHandler commandHandler;

    @Override
    public void onEnable() {
        loadConfig();
        // Plugin startup logic
        getLogger().info("Nations plus enabled");

        // Setup SQL connection
        try {
            DriverManager.registerDriver(new Driver());
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/nationsplus?stringtype=unspecified", "postgres", "");
            databaseManager = new DatabaseManager(connection);
            getLogger().info(connection.toString() + " connected to DB successfully!");
            // Check if database structure exists.
            if (!getConfig().getBoolean("database-created")) {
                // If the database is not created, create its structure
                databaseManager.createDatabase();
                getConfig().set("database-created", true);
            } else {
                getLogger().info("Database already set up. No need to import structure.");
            }
            // Register command handler
            commandHandler = new CommandHandler(connection);
            // Register events listeners that needs a SQL connection
            getServer().getPluginManager().registerEvents(new Events(connection), this);

            // Register commands
            getCommand("nationsplus").setExecutor(commandHandler);
            getCommand("nation").setExecutor(commandHandler);
            getCommand("nation").setTabCompleter(new NationAutoComplete());

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Nations plus disabled");
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
