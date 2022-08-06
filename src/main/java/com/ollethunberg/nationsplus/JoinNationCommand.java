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
            // Check if there is a nation with that name
            ResultSet rs = sqlHelper.query("SELECT * FROM nation WHERE name = ?", nationName);
            if (!rs.next()) {
                player.sendMessage("There is no nation with that name!");
                return;
            }

            String updatePlayerNationIdSQL = "UPDATE player SET nation = ? WHERE uid = ?";
            sqlHelper.updateAsync(updatePlayerNationIdSQL, new SQLHelper.UpdateCallback() {
                @Override
                public void onQueryDone() {
                    player.sendMessage("§aYou have successfully joined the nation!");

                    // Set the prefix to the player
                    try {
                        // remove the old prefix
                        Events.prefixCache.remove(player.getUniqueId().toString());
                        Events.prefixCache.put(player.getUniqueId().toString(), rs.getString("prefix"));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        // give error to user
                        player.sendMessage("§cThere was an error setting your prefix!");
                    }

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
