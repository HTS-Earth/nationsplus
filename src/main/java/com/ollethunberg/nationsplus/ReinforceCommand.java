package com.ollethunberg.nationsplus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ReinforceCommand {
    SQLHelper sqlHelper;
    Plugin plugin = NationsPlus.getPlugin(NationsPlus.class);

    public ReinforceCommand(Connection _connection) {
        sqlHelper = new SQLHelper(_connection);
    }

    public void execute(Player player, String reinforcementMode) {

        try {
            // check if reinforceTarget has a value
            if (reinforcementMode == null) {
                // toggle reinforce mode
                ResultSet currentTarget = sqlHelper.query("SELECT reinforcement_mode FROM player WHERE uid = ?",
                        player.getUniqueId().toString());
                if (currentTarget.next()) {
                    String newReinforcementMode = currentTarget.getString("reinforcement_mode").equals("NATION")
                            ? "PRIVATE"
                            : "NATION";
                    sqlHelper.update("UPDATE player SET reinforcement_mode = ? WHERE uid = ?",
                            newReinforcementMode, player.getUniqueId().toString());
                    player.sendMessage("§2Reinforcement mode set to " + newReinforcementMode);
                }
            } else {
                String newReinforcementMode = reinforcementMode.toUpperCase();
                // check if newReinforcementMode is "N" or "P" and set it to "NATION" or
                // "PRIVATE"
                if (newReinforcementMode.equals("N")) {
                    newReinforcementMode = "NATION";
                } else if (newReinforcementMode.equals("P")) {
                    newReinforcementMode = "PRIVATE";
                }
                if (newReinforcementMode.equals("PRIVATE") || newReinforcementMode.equals("NATION")) {
                    sqlHelper.update("UPDATE player SET reinforcement_mode = ? WHERE uid = ?",
                            newReinforcementMode, player.getUniqueId().toString());
                    player.sendMessage("§2Reinforcement mode set to " + newReinforcementMode);
                } else {
                    player.sendMessage("§4Invalid reinforcement mode. Valid modes are: NATION, PRIVATE");
                }

            }
        } catch (SQLException e) {
            // print error
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }
}
