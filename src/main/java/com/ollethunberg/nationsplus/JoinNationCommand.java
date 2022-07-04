package com.ollethunberg.nationsplus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class JoinNationCommand {
    Connection conn;

    public JoinNationCommand(Connection _connection) {
        conn = _connection;
    }

    public void execute(Player player, String nationName) {
        try {
            String updatePlayerNationIdSQL = "UPDATE player SET nation_id = ? WHERE player_id = ?";
            PreparedStatement prepareUpdateStatement = conn.prepareStatement(updatePlayerNationIdSQL);
            prepareUpdateStatement.setString(1, nationName);
            prepareUpdateStatement.setString(2, player.getUniqueId().toString());
            prepareUpdateStatement.executeUpdate();
            // Message the player that he has successfully joined a nation
            player.sendMessage("§aYou have successfully joined the nation!");

            // Get the coordinates of the nation and teleport the player there.
            String getNationCoordinatesSQL = "SELECT x, y, z FROM nation WHERE name = ?";
            PreparedStatement prepareGetCoordinatesStatement = conn.prepareStatement(getNationCoordinatesSQL);
            prepareGetCoordinatesStatement.setString(1, nationName);
            prepareGetCoordinatesStatement.executeQuery();
            ResultSet rs = prepareGetCoordinatesStatement.getResultSet();

            if (rs.next()) {
                player.teleport(new Location(player.getWorld(), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")));
            } else {
                player.sendMessage("§cSomething went wrong. Please try again.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
