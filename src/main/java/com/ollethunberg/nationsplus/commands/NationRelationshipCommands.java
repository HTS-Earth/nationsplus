package com.ollethunberg.nationsplus.commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.ollethunberg.nationsplus.NationsPlus;
import com.ollethunberg.nationsplus.lib.SQLHelper;

import net.md_5.bungee.api.ChatColor;

public class NationRelationshipCommands {

    private Plugin plugin = NationsPlus.getPlugin(NationsPlus.class);
    static String[] statusStrings = { "war", "peace", "ally", "neutral", "enemy", "peace_requested" };

    public static Boolean isStatusValid(String status) {
        for (String s : statusStrings) {
            if (s.equals(status)) {
                return true;
            }
        }
        return false;
    }

    public static String statusWithColor(String _status) {
        String status = _status.toUpperCase();
        if (status.equalsIgnoreCase("war")) {
            return ChatColor.RED + status;
        } else if (status.equalsIgnoreCase("peace")) {
            return ChatColor.GREEN + status;
        } else if (status.equalsIgnoreCase("ally")) {
            return ChatColor.BLUE + status;
        } else if (status.equalsIgnoreCase("neutral")) {
            return ChatColor.YELLOW + status;
        } else if (status.equalsIgnoreCase("enemy")) {
            return ChatColor.DARK_RED + status;
        } else if (status.equalsIgnoreCase("peace-requested")) {
            return ChatColor.GRAY + status;
        } else {
            return status;
        }
    }

    public void executeStatus(Player player) {
        // List all the statuses of the nations to the player
        try {
            ResultSet rs = SQLHelper.query("SELECT * FROM nation_relations ");
            while (rs.next()) {
                String nation1 = rs.getString("nation_one");
                String nation2 = rs.getString("nation_second");
                String status = rs.getString("status");
                // Print it to user with colors
                player.sendMessage(ChatColor.GREEN + nation1 + " §2§l→§r " + ChatColor.GREEN +
                        nation2 + " " + ChatColor.GOLD + ": §r§l" + NationRelationshipCommands.statusWithColor(status));
            }
        } catch (SQLException e) {
            // Print to the user that we could not get all the nation relationships
            player.sendMessage("Could not get all the nation relationships");
            e.printStackTrace();
        }

    }

    public void execute(Player executor, String targetNation, String status) {

        try {
            // Find which nation the player belongs to
            ResultSet rs = SQLHelper.query(
                    "SELECT n.king_id, n.name as nation FROM player as p inner join nation as n on n.name = p.nation WHERE p.uid = ?",
                    executor.getUniqueId().toString());
            plugin.getLogger().info("Executing nation relationship command");
            if (rs.next()) {
                // Check if the requesting player is the king
                plugin.getLogger().info("Checking if the player is the king");
                if (rs.getString("king_id").equalsIgnoreCase(executor.getUniqueId().toString())) {
                    // Player is the king
                    // Check if a relationshop already exists in the database
                    ResultSet rsRelationship = SQLHelper.query(
                            "SELECT * FROM nation_relations WHERE (UPPER(nation_one) = UPPER(?) AND UPPER(nation_second) = UPPER(?)) OR (UPPER(nation_one) = UPPER(?) AND UPPER(nation_second) = UPPER(?))",
                            rs.getString("nation"), targetNation, targetNation, rs.getString("nation"));
                    plugin.getLogger().info("Checking if a relationship already exists");
                    if (rsRelationship.next()) {
                        // Relationship already exists.
                        // Check if the current status is the same as the new status
                        plugin.getLogger().info("Checking if the status is the same");
                        if (rsRelationship.getString("status").equalsIgnoreCase(status)) {
                            // Status is the same, do nothing
                            plugin.getLogger().info("Status is the same");
                            executor.sendMessage("You already have a relationship with " + targetNation
                                    + " with the status " + status);
                            return;
                        }
                        // Check if the nations are at war, if they are, then the only available status
                        // is "peace"
                        if (rsRelationship.getString("status").equalsIgnoreCase("war")
                                || rsRelationship.getString("status").equalsIgnoreCase("peace-requested")) {
                            if (status.equalsIgnoreCase("peace")) {
                                // Check if the targetNation wants peace, and if peace is available.
                                // If it is, then update the database to reflect the new status.
                                String nationWantsPeace = rsRelationship.getString("wants_peace");
                                Boolean isPeaceAvailable = rsRelationship.getBoolean("peace_available");
                                if (nationWantsPeace != null && nationWantsPeace.equalsIgnoreCase(targetNation)
                                        && isPeaceAvailable) {
                                    SQLHelper.update(
                                            "UPDATE nation_relations SET status = ?, peace_available=false, wants_peace=null WHERE (nation_one = ? AND nation_second = ?) OR (nation_one = ? AND nation_second = ?)",
                                            "peace", rs.getString("nation"), targetNation, targetNation,
                                            rs.getString("nation"));
                                    executor.sendMessage("§eYou have accepted " + targetNation + "'s peace offer.");
                                    // Announce the peace treaty
                                    this.announceNewStatus(rs.getString("nation"), targetNation, status);

                                } else if (nationWantsPeace != null
                                        && nationWantsPeace.equals(rs.getString("nation"))) {
                                    executor.sendMessage(
                                            "§cYou have already requested peace with " + targetNation + ".");
                                } else {
                                    // Request peace with the targetNation
                                    plugin.getLogger().info("Requesting peace with " + targetNation);

                                    SQLHelper.update(
                                            "UPDATE nation_relations SET wants_peace = ?, peace_available = ?, status = 'peace-requested' WHERE (nation_one = ? AND nation_second = ?) OR (nation_one = ? AND nation_second = ?)",
                                            rs.getString("nation"), true, rs.getString("nation"), targetNation,
                                            targetNation,
                                            rs.getString("nation"));
                                    executor.sendMessage("§eYou have requested peace with " + targetNation + ".");
                                    // Announce the peace request
                                    announceNewStatus(rs.getString("nation"), targetNation, "peace-requested");
                                }

                            } else {
                                executor.sendMessage("§cYou cannot set the relationship between " + targetNation
                                        + " and " + rs.getString("nation") + " to " + status
                                        + " because they are at war.");
                            }
                        } else {
                            // Update the relationship status to the new status
                            SQLHelper.update(
                                    "UPDATE nation_relations SET status = ? WHERE (nation_one = ? AND nation_second = ?) OR (nation_one = ? AND nation_second = ?)",
                                    status, rs.getString("nation"), targetNation, targetNation, rs.getString("nation"));
                            executor.sendMessage("§aYou have set the relationship between " + targetNation + " and "
                                    + rs.getString("nation") + " to " + status + ".");
                            this.announceNewStatus(rs.getString("nation"), targetNation, status);
                        }
                        /*
                         * // Update the relationship
                         * plugin.getLogger().info("Updating the relationship with " + targetNation +
                         * " and status "
                         * + status + " between nation " + rs.getString("nation"));
                         * SQLHelper.update(
                         * "UPDATE nation_relations SET status = ? WHERE (nation_one = ? AND nation_second = ?) OR (nation_one = ? AND nation_second = ?)"
                         * ,
                         * status, rs.getString("nation"), targetNation, targetNation,
                         * rs.getString("nation"));
                         * executor.sendMessage("Updated realationship to nation " + targetNation);
                         */
                    } else {
                        // No relationship exists
                        // Insert a new relationship
                        SQLHelper.update(
                                "INSERT INTO nation_relations (nation_one, nation_second, status) VALUES (?, ?, ?)",
                                rs.getString("nation"), targetNation, status);
                        executor.sendMessage("Updated realationship to nation " + targetNation);
                        this.announceNewStatus(rs.getString("nation"), targetNation, status);
                    }
                    // executor.sendMessage("You have set the relationship with " + targetNation + "
                    // to " + status);
                } else {
                    executor.sendMessage("You are not the king of this nation!");
                }

            } else {
                executor.sendMessage("§cYou are not in a nation.");
                return;
            }

        } catch (SQLException e) {

            // Print stacktrace
            e.printStackTrace();

            // Send to the user that his command was incorrectly used
            executor.sendMessage("§cYou have incorrectly used this command. Please refer to /nationplus for help!");
            if (executor.hasPermission("nationsplus.tester")) {
                executor.sendMessage("§cDetails: " + e.getLocalizedMessage());
            }
        }
    }

    private void announceNewStatus(String declaringNation, String targetNation, String status) {
        // Announce to all players on the server the new nation status
        plugin.getLogger().info("Announcing new status");
        switch (status) {
            case "war":
                plugin.getServer()
                        .broadcastMessage(
                                "§2§l→ §r§e" + declaringNation + " has declared §c§lWAR§r§e on " + targetNation);
                break;
            case "peace":
                plugin.getServer()
                        .broadcastMessage(
                                "§2§l→ §r§e" + declaringNation + " has accepted " + targetNation
                                        + "'s' §a§lPEACE§r§e offer ");
                break;
            case "ally":
                plugin.getServer()
                        .broadcastMessage(
                                "§2§l→ §r§e" + declaringNation + " has declared §a§lALLY§r§e on " + targetNation);
                break;
            case "enemy":
                plugin.getServer()
                        .broadcastMessage(
                                "§2§l→ §r§e" + declaringNation + " has declared §c§lENEMY§r§e on " + targetNation);
                break;
            case "peace-requested":
                plugin.getServer()
                        .broadcastMessage("§2§l→ §r§e" + declaringNation + " has requested peace with " + targetNation);
                break;
            default:
                plugin.getServer()
                        .broadcastMessage(
                                "§2§l→ §r§e" + declaringNation + " has declared §a§lNEUTRAL§r§e on " + targetNation);
                break;
        }

    }
}
