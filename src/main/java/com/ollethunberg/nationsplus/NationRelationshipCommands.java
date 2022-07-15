package com.ollethunberg.nationsplus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NationRelationshipCommands {
    SQLHelper sqlHelper;
    private Plugin plugin = NationsPlus.getPlugin(NationsPlus.class);

    public NationRelationshipCommands(Connection conn) {
        sqlHelper = new SQLHelper(conn);
    }

    public void execute(Player executor, String targetNation, String status) {
        try {
            // Find which nation the player belongs to
            ResultSet rs = sqlHelper.query(
                    "SELECT n.king_id, n.name as nation FROM player as p inner join nation as n on n.name = p.nation WHERE p.uid = ?",
                    executor.getUniqueId().toString());
            plugin.getLogger().info("Executing nation relationship command");
            if (rs.next()) {
                // Check if the requesting player is the king
                plugin.getLogger().info("Checking if the player is the king");
                if (rs.getString("king_id").equalsIgnoreCase(executor.getUniqueId().toString())) {
                    // Player is the king
                    // Check if a relationshop already exists in the database
                    ResultSet rsRelationship = sqlHelper.query(
                            "SELECT * FROM nation_relations WHERE (nation_one = ? AND nation_second = ?) OR (nation_one   = ? AND nation_second = ?)",
                            rs.getString("nation"), targetNation, targetNation, rs.getString("nation"));
                    if (rsRelationship.next()) {
                        // Relationship already exists
                        // Check if the nations are at war, if they are, then the only available status
                        // is "peace"
                        if (rsRelationship.getString("status").equalsIgnoreCase("war")) {
                            if (status.equalsIgnoreCase("peace")) {
                                // Update the relationship status to peace
                                sqlHelper.update(
                                        "UPDATE nation_relations SET status = ? WHERE (nation_one = ? AND nation_second = ?) OR (nation_one = ? AND nation_second = ?)",
                                        "peace", rs.getString("nation"), targetNation, targetNation,
                                        rs.getString("nation"));
                                executor.sendMessage("§aYou have set the relationship between " + targetNation + " and "
                                        + rs.getString("nation") + " to peace.");
                            } else {
                                executor.sendMessage("§cYou cannot set the relationship between " + targetNation
                                        + " and " + rs.getString("nation") + " to " + status
                                        + " because they are at war.");
                            }
                        } else {
                            // Update the relationship status to the new status
                            sqlHelper.update(
                                    "UPDATE nation_relations SET status = ? WHERE (nation_one = ? AND nation_second = ?) OR (nation_one = ? AND nation_second = ?)",
                                    status, rs.getString("nation"), targetNation, targetNation, rs.getString("nation"));
                            executor.sendMessage("§aYou have set the relationship between " + targetNation + " and "
                                    + rs.getString("nation") + " to " + status + ".");

                        }
                        /*
                         * // Update the relationship
                         * plugin.getLogger().info("Updating the relationship with " + targetNation +
                         * " and status "
                         * + status + " between nation " + rs.getString("nation"));
                         * sqlHelper.update(
                         * "UPDATE nation_relations SET status = ? WHERE (nation_one = ? AND nation_second = ?) OR (nation_one = ? AND nation_second = ?)"
                         * ,
                         * status, rs.getString("nation"), targetNation, targetNation,
                         * rs.getString("nation"));
                         * executor.sendMessage("Updated realationship to nation " + targetNation);
                         */
                    } else {
                        // No relationship exists
                        // Insert a new relationship
                        sqlHelper.update(
                                "INSERT INTO nation_relations (nation_one, nation_second, status) VALUES (?, ?, ?)",
                                rs.getString("nation"), targetNation, status);
                        executor.sendMessage("Updated realationship to nation " + targetNation);
                    }
                    executor.sendMessage("You have set the relationship with " + targetNation + " to " + status);
                } else {
                    executor.sendMessage("You are not the king of this nation!");
                }

            } else {
                executor.sendMessage("§cYou are not in a nation.");
                return;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void announceNewStatus(String declaringNation, String targetNation, String status) {
        // Announce to all players on the server the new nation status
        plugin.getLogger().info("Announcing new status");
        if (status.equals("war")) {
            plugin.getServer().broadcastMessage("§e" + declaringNation + " has declared &c&lWAR&r on " + targetNation);
        } else if (status.equals("peace")) {
            plugin.getServer()
                    .broadcastMessage("§e" + declaringNation + " has declared &2&lPEACE&r with " + targetNation);
        } else {
            plugin.getServer()
                    .broadcastMessage("§e" + declaringNation + " has declared neutrality with " + targetNation);
        }
    }
}
