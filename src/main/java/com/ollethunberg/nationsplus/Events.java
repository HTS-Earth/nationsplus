package com.ollethunberg.nationsplus;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import java.sql.*;

public class Events implements Listener {

    private Connection conn;
    private Plugin plugin = NationsPlus.getPlugin(NationsPlus.class);

    public Events(Connection _connection) {

        conn = _connection;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        try {
            plugin.getLogger().info("Player joined!" + conn.toString());
            // Check if they are in the database.
            String isPlayerInDatabaseSQL = "SELECT EXISTS ( SELECT FROM player WHERE uid = ? );";
            PreparedStatement preparedStatement = conn.prepareStatement(isPlayerInDatabaseSQL);
            preparedStatement.setString(1, event.getPlayer().getUniqueId().toString());

            ResultSet rs = preparedStatement.executeQuery();
            rs.next();

            if (!rs.getBoolean("exists")) {
                plugin.getLogger().info("New player joined!");
                // Insert into database
                String insertNewPlayerSQL = "INSERT INTO player(uid, last_login, player_name, kills, deaths) VALUES (?, CURRENT_TIMESTAMP, ?, 0,0);";
                PreparedStatement prepareInsertStatement = conn.prepareStatement(insertNewPlayerSQL);
                prepareInsertStatement.setString(1, event.getPlayer().getUniqueId().toString());
                prepareInsertStatement.setString(2, event.getPlayer().getDisplayName());
                prepareInsertStatement.executeUpdate();
            } else {
                plugin.getLogger().info("Player does exist in the database!");

                String updatePlayerLastLoginSQL = "UPDATE player SET last_login=CURRENT_TIMESTAMP, player_name=? where uid = ?";
                PreparedStatement prepareUpdateStatement = conn.prepareStatement(updatePlayerLastLoginSQL);
                prepareUpdateStatement.setString(1, event.getPlayer().getDisplayName());
                prepareUpdateStatement.setString(2, event.getPlayer().getUniqueId().toString());
                prepareUpdateStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
