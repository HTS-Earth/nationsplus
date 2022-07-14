package com.ollethunberg.nationsplus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

public class NationRelationshipCommands {
    SQLHelper sqlHelper;

    public NationRelationshipCommands(Connection conn) {
        sqlHelper = new SQLHelper(conn);
    }

    public void execute(Player executor, String targetNation, String status) {
        try {
            // Find which nation the player belongs to
            ResultSet rs = sqlHelper.query(
                    "SELECT n.king_id, n.name as nation FROM player as p inner join nation as n on n.name = p.nation WHERE p.uid = ?",
                    executor.getUniqueId().toString());

            if (rs.next()) {
                // Check if the requesting player is the king
                if (rs.getString("king_id").equalsIgnoreCase(executor.getUniqueId().toString())) {
                    // Player is the king
                    // Check if a relationshop already exists in the database
                    ResultSet rsRelationship = sqlHelper.query(
                            "SELECT * FROM nation_relations WHERE (nation_one = ? AND nation_second = ?) OR (nation_one   = ? AND nation_second = ?)",
                            rs.getString("nation"), targetNation, targetNation, rs.getString("nation"));
                    if (rsRelationship.next()) {
                        // Relationship already exists
                        // Update the relationship
                        sqlHelper.query(
                                "UPDATE nation_relations SET status = ? WHERE (nation_one = ? AND nation_second = ?) OR (nation_one = ? AND nation_second = ?)",
                                status, rs.getString("nation"), targetNation, targetNation, rs.getString("nation"));
                        executor.sendMessage("Updated realationship to nation " + targetNation);
                    } else {
                        // No relationship exists
                        // Insert a new relationship
                        sqlHelper.query(
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
}
