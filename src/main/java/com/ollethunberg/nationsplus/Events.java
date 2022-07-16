package com.ollethunberg.nationsplus;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

public class Events implements Listener {

    private SQLHelper sqlHelper;
    private Plugin plugin = NationsPlus.getPlugin(NationsPlus.class);

    HashMap<String, String> prefixCache = new HashMap<String, String>();

    public Events(Connection _connection) {

        sqlHelper = new SQLHelper(_connection);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        try {

            // Check if they are in the database.
            String isPlayerInDatabaseSQL = "SELECT EXISTS ( SELECT FROM player WHERE uid = ? );";
            ResultSet rs = sqlHelper.query(isPlayerInDatabaseSQL, event.getPlayer().getUniqueId().toString());

            rs.next();

            if (!rs.getBoolean("exists")) {
                plugin.getLogger().info("New player joined!");
                // Insert into database
                String insertNewPlayerSQL = "INSERT INTO player(uid, last_login, player_name, kills, deaths) VALUES (?, CURRENT_TIMESTAMP, ?, 0,0);";
                sqlHelper.update(insertNewPlayerSQL, event.getPlayer().getUniqueId().toString(),
                        event.getPlayer().getName());
                // Teleport the player to the spawn
            } else {
                plugin.getLogger().info("Player does exist in the database!");
                // Check if the player has a ban on them on the player_bans table
                String playerBannedUntil = "SELECT banned_date + (banned_minutes * interval '1 minute') as banned_until, player_id FROM player_bans WHERE player_id = ? order by banned_date DESC;";
                ResultSet rsPlayerBannedUntil = sqlHelper.query(playerBannedUntil,
                        event.getPlayer().getUniqueId().toString());
                rsPlayerBannedUntil.next();
                /*
                 * if (rsPlayerBannedUntil.getTimestamp("banned_until") != null) {
                 * if (rsPlayerBannedUntil.getTimestamp("banned_until")
                 * .after(new Timestamp(System.currentTimeMillis()))) {
                 * plugin.getLogger().info("Player is banned!");
                 * event.getPlayer().kickPlayer("§cYou are not able to join the server, ");
                 * return;
                 * }
                 * } else {
                 * // log that the player is not banned
                 * plugin.getLogger().info("Player is not banned!");
                 * }
                 */
                String updatePlayerLastLoginSQL = "UPDATE player SET last_login=CURRENT_TIMESTAMP, player_name=? where uid = ?";
                sqlHelper.update(updatePlayerLastLoginSQL, event.getPlayer().getDisplayName(),
                        event.getPlayer().getUniqueId().toString());
                // Update our prefix cache with the prefix of the nation that the player is in.
                String getPlayerNationSQL = "SELECT n.prefix FROM player as p inner join nation as n on p.nation=n.name WHERE p.uid = ?;";
                ResultSet rsPlayerNation = sqlHelper.query(getPlayerNationSQL,
                        event.getPlayer().getUniqueId().toString());
                if (rsPlayerNation.next()) {
                    prefixCache.put(event.getPlayer().getUniqueId().toString(), rsPlayerNation.getString("prefix"));
                } else {
                    plugin.getLogger().info("Player is not in a nation!");
                    event.getPlayer().sendMessage("§aPlease join a nation!");
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // remove player from cache
        prefixCache.remove(event.getPlayer().getUniqueId().toString());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        // Get preifix from the cache.
        String prefix = prefixCache.get(e.getPlayer().getUniqueId().toString());
        if (prefix != null) {
            e.setFormat("§e[§r" + prefix + "§e]§r %s : %s");
        }
    }
}
