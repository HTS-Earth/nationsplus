package com.ollethunberg.nationsplus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

public class InfoNationCommand {
    Connection conn;

    public InfoNationCommand(Connection _connection) {
        conn = _connection;
    }

    public void execute(Player player, String nation) {
        try {
            // Get the nations and list message them to the player

            String getNationsSQL = "SELECT n.*, p.player_name as king FROM nation as n LEFT JOIN player as p on n.king_id=p.uid where n.name=? order by n.balance desc ;";
            PreparedStatement prepareStatementNation = conn.prepareStatement(getNationsSQL);
            prepareStatementNation.setString(1, nation);
            prepareStatementNation.executeQuery();
            ResultSet rsNation = prepareStatementNation.getResultSet();
            rsNation.next();

            String getNationMemeberCountSQL = "SELECT count(*) as count FROM player where nation=?;";
            PreparedStatement prepareStatementMemberCount = conn.prepareStatement(getNationMemeberCountSQL);
            prepareStatementMemberCount.setString(1, nation);
            prepareStatementMemberCount.executeQuery();
            ResultSet rsMemberCount = prepareStatementMemberCount.getResultSet();
            rsMemberCount.next();

            player.sendMessage("§2- Nation Inspector -");
            // Display nation info
            player.sendMessage("§2Name§r: " + rsNation.getString("name"));
            player.sendMessage("§2Prefix§r: " + rsNation.getString("prefix"));
            player.sendMessage("§aBalance§r: $" + rsNation.getString("balance"));
            player.sendMessage("§aTax§r: " + rsNation.getString("tax") + "%");
            player.sendMessage("§6King§r: " + rsNation.getString("king"));
            player.sendMessage("§eSuccessor§r: " + rsNation.getString("successor_id"));
            player.sendMessage("§eMembers§r: " + rsMemberCount.getString("count"));
            player.sendMessage("§cKills§r: " + rsNation.getString("kills"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
