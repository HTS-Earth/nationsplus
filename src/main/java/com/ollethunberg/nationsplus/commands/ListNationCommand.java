package com.ollethunberg.nationsplus.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

public class ListNationCommand {
    Connection conn;

    public ListNationCommand(Connection _connection) {
        conn = _connection;
    }

    public void execute(Player player) {
        try {
            // Get the nations and list message them to the player
            String getNationsSQL = "SELECT n.*, p.player_name as king FROM nation as n LEFT JOIN player as p on n.king_id=p.uid order by n.balance desc;";
            PreparedStatement prepareStatement = conn.prepareStatement(getNationsSQL);
            prepareStatement.executeQuery();
            player.sendMessage("§2§l- Nations -");
            ResultSet rs = prepareStatement.getResultSet();
            while (rs.next()) {
                player.sendMessage("§r§6[§r" + rs.getString("prefix") + "§6]§r " + rs.getString("name") + " §r(§c"
                        + rs.getString("kills") + "§r kills)" + " §r - §a$" + rs.getString("balance") + "§r - §6§l✮§r: "
                        + rs.getString("king"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
