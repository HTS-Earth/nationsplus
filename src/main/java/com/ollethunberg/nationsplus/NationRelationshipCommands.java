package com.ollethunberg.nationsplus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

public class NationRelationshipCommands {
    Connection conn;

    public NationRelationshipCommands(Connection _conn) {
        conn = _conn;
    }

    public void execute(Player player, String status) {
        try {
            // Find which nation the player belongs to
            String getPlayerNationIdSQL = "SELECT nation FROM player WHERE uid = ?";
            PreparedStatement prepareStatement = conn.prepareStatement(getPlayerNationIdSQL);
            prepareStatement.setString(1, player.getUniqueId().toString());
            prepareStatement.executeQuery();
            ResultSet rs = prepareStatement.getResultSet();
            String playerNationId = "";
            if (rs.next()) {
                playerNationId = rs.getString("nation");
            } else {
                player.sendMessage("§cYou are not in a nation.");
                return;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
