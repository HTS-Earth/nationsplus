package com.ollethunberg.nationsplus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinNationCommand extends DatabaseInteractor {

    public JoinNationCommand(Connection connection) {
        super(connection);
    }

    public void execute(Player player, String nationName) {
        try {

            String updatePlayerNationIdSQL = "UPDATE player SET nation = ? WHERE uid = ?";
            sqlHelper.updateAsync(updatePlayerNationIdSQL, new SQLHelper.UpdateCallback() {
                @Override
                public void onQueryDone() {
                    player.sendMessage("§aYou have successfully joined the nation!");
                    // Get the coordinates of the nation and teleport the player there.
                    String getNationCoordinatesSQL = "SELECT x, y, z FROM nation WHERE name = ?";
                    try {
                        sqlHelper.queryAsync(getNationCoordinatesSQL, new SQLHelper.QueryCallback() {
                            @Override
                            public void onQueryDone(ResultSet rs) throws SQLException {
                                if (rs.next()) {
                                    Location nationLocation = new Location(Bukkit.getWorld("world"), rs.getDouble("x"),
                                            rs.getDouble("y"), rs.getDouble("z"));

                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            player.teleport(nationLocation);
                                        }
                                    }.runTask(plugin);

                                } else {
                                    player.sendMessage("§cSomething went wrong. Please try again.");
                                }
                            }
                        }, nationName);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }, nationName, player.getUniqueId().toString());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
