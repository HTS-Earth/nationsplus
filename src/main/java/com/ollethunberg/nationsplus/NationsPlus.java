package com.ollethunberg.nationsplus;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import org.postgresql.Driver;

import com.ollethunberg.nationsplus.commands.crown.CrownAutoComplete;
import com.ollethunberg.nationsplus.commands.crown.CrownHandler;
import com.ollethunberg.nationsplus.commands.nation.NationAutoComplete;
import com.ollethunberg.nationsplus.commands.nation.NationGUI;
import com.ollethunberg.nationsplus.commands.nation.NationHandler;
import com.ollethunberg.nationsplus.commands.reinforce.ReinforceHandler;
import com.ollethunberg.nationsplus.lib.SQLHelper;

import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Logger;

public final class NationsPlus extends JavaPlugin {
    public Connection connection;
    public DatabaseManager databaseManager;
    public Configuration config;
    public static final Logger LOGGER = Logger.getLogger("NationsPlus");
    private static Locale usa = new Locale("en", "US");
    public static NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(usa);
    /* GUI */
    NationGUI nationGUI;

    /* CommandHandlers */
    NationHandler nationHandler;
    CrownHandler crownHandler;
    ReinforceHandler reinforceHandler;

    @Override
    public void onEnable() {
        loadConfig();
        // Plugin startup logic
        getLogger().info("Nations plus enabled");

        // Setup SQL connection
        try {
            DriverManager.registerDriver(new Driver());
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + config.getString("database.ip") + ":" + config.getInt("database.port") + "/"
                            + config.getString("database.database") + "?stringtype=unspecified",
                    config.getString("database.username"), config.getString("database.password"));
            databaseManager = new DatabaseManager(connection);
            SQLHelper.conn = connection;
            getLogger().info(connection.toString() + " connected to DB successfully!");
            // Check if database structure exists.
            if (!getConfig().getBoolean("database-created")) {
                // If the database is not created, create its structure
                databaseManager.createDatabase();
                getConfig().set("database-created", true);
            } else {
                getLogger().info("Database already set up. No need to import structure.");
            }
            // Register command handlers
            nationHandler = new NationHandler();
            crownHandler = new CrownHandler();
            reinforceHandler = new ReinforceHandler();
            nationGUI = new NationGUI();

            // Register events listeners that needs a SQL connection
            getServer().getPluginManager().registerEvents(new Events(), this);
            getServer().getPluginManager().registerEvents(nationGUI, this);

            // Register commands
            getCommand("nationsplus").setExecutor(nationHandler);
            getCommand("nation").setExecutor(nationHandler);
            getCommand("crown").setExecutor(crownHandler);
            getCommand("reinforce").setExecutor(reinforceHandler);

            getCommand("nation").setTabCompleter(new NationAutoComplete());
            getCommand("crown").setTabCompleter(new CrownAutoComplete());

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            NationsPlus.LOGGER.warning(getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Nations plus disabled");

        // Close SQL connection
        SQLHelper.closeConnection();
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        config = getConfig();

    }
}
