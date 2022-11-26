package com.ollethunberg.nationsplus.commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ollethunberg.nationsplus.Events;
import com.ollethunberg.nationsplus.lib.SQLHelper;

public class JoinNationCommand {

    public void execute(Player player, String nationName) {
        try {
            // Check if there is a nation with that name
            ResultSet rs = SQLHelper.query("SELECT * FROM nation WHERE name = ?", nationName);
            if (!rs.next()) {
                player.sendMessage("There is no nation with that name!");
                return;
            }

            String updatePlayerNationIdSQL = "UPDATE player SET nation = ? WHERE uid = ?";
            SQLHelper.update(updatePlayerNationIdSQL, nationName, player.getUniqueId().toString());
            Events.nationPrefixCache.remove(player.getUniqueId().toString());
            Events.nationPrefixCache.put(player.getUniqueId().toString(), rs.getString("prefix"));
            String getNationCoordinatesSQL = "SELECT x, y, z FROM nation WHERE name = ?";
            ResultSet nationCoordinates = SQLHelper.query(getNationCoordinatesSQL, nationName);
            if (!nationCoordinates.next()) {
                player.sendMessage("§4Error: Nation coordinates not found!");
                return;
            }
            Location nationLocation = new Location(Bukkit.getWorld("world"), nationCoordinates.getDouble("x"),
                    nationCoordinates.getDouble("y"), nationCoordinates.getDouble("z"));

            player.teleport(nationLocation);

            player.sendMessage("§aYou have successfully joined the nation!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
