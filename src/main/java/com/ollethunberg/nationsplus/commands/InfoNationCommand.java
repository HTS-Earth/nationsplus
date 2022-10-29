package com.ollethunberg.nationsplus.commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import com.ollethunberg.nationsplus.lib.SQLHelper;

public class InfoNationCommand {

    public void execute(Player player, String nation) {
        try {
            // Get the nations and list message them to the player

            String getNationsSQL = "SELECT n.*, p.player_name as king FROM nation as n LEFT JOIN player as p on n.king_id=p.uid where UPPER(n.name)=UPPER(?) order by n.balance desc ;";
            ResultSet rsNation = SQLHelper.query(getNationsSQL, nation);
            rsNation.next();

            // Use sqlHelper
            String getNationMemeberCountSQL = "SELECT count(*) as count FROM player where UPPER(nation)=UPPER(?);";
            ResultSet rsMemberCount = SQLHelper.query(getNationMemeberCountSQL, nation);
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
            // If nation could not be found, display error message
            player.sendMessage("§cNation not found.");
            e.printStackTrace();

        }
    }
}
