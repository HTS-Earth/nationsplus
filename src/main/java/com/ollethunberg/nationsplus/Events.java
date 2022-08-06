package com.ollethunberg.nationsplus;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Events implements Listener {

    private SQLHelper sqlHelper;
    private Plugin plugin = NationsPlus.getPlugin(NationsPlus.class);
    private Configuration config = plugin.getConfig();
    static HashMap<String, String> prefixCache = new HashMap<String, String>();

    public Events(Connection _connection) {

        sqlHelper = new SQLHelper(_connection);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {

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
    public void onPlayerQuit(final PlayerQuitEvent event) {
        // remove player from cache
        prefixCache.remove(event.getPlayer().getUniqueId().toString());
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        // Get preifix from the cache.
        String prefix = prefixCache.get(e.getPlayer().getUniqueId().toString());
        if (prefix != null) {
            e.setFormat("§e[§r" + prefix + "§e]§r %s : %s");
        }
    }

    // Block reinforcement eventhandler
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.AIR)
            return;
        // Check if there is a reinforcement on that block
        String getReinforcementSQL = "SELECT * FROM block_reinforcement WHERE block_id = ? AND world = ?;";
        String block_id = event.getBlock().getLocation().getBlockX() + "," + event.getBlock().getLocation().getBlockY()
                + "," + event.getBlock().getLocation().getBlockZ();
        try (ResultSet rs = sqlHelper.query(getReinforcementSQL, block_id,
                event.getBlock().getLocation().getWorld().getName())) {
            if (rs.next()) {
                int newHealth = rs.getInt("health") - 1;
                if (newHealth > 0) {
                    event.setCancelled(true);
                    // Instanstiate particles at the blocks position
                    event.getBlock().getWorld().spawnParticle(org.bukkit.Particle.HEART,
                            event.getBlock().getLocation(), 3, 0, 0, 0);
                    // Update the health of the block reinforcement async
                    String updateReinforcementHealthSQL = "UPDATE block_reinforcement SET health = health - 1 WHERE block_id = ? AND world = ?;";
                    sqlHelper.updateAsync(updateReinforcementHealthSQL, new SQLHelper.UpdateCallback() {
                        @Override
                        public void onQueryDone() throws SQLException {
                            // If the reinforced blocks health is dividable with 5, tell the player that
                            // are on the way to bream the block
                            if (newHealth % 5 == 0) {
                                event.getPlayer()
                                        .sendMessage("§aYou are on the way to break the block! It has " + newHealth
                                                + " health left!");
                            }
                        }
                    }, block_id,
                            event.getBlock().getLocation().getWorld().getName());
                } else {
                    // If the block is broken, remove the reinforcement from the database
                    String removeReinforcementSQL = "DELETE FROM block_reinforcement WHERE block_id = ? AND world = ?;";
                    sqlHelper.updateAsync(removeReinforcementSQL, () -> {
                    }, block_id,
                            event.getBlock().getLocation().getWorld().getName());
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // New Block reinforcement eventhandler
    @EventHandler
    public void onBlockDamage(final BlockDamageEvent event) {
        // get if a player damaged the block
        if (event.getPlayer() == null) {
            return;
        }
        final Block block = event.getBlock();
        final ItemStack itemInHand = event.getItemInHand();

        if (block.getType() == Material.AIR)
            return;
        // check if the block is solid
        if (!block.getType().isSolid()) {
            event.getPlayer().sendMessage("§cYou can only reinforce solid blocks!");
            return;
        }
        // If the item is either iron, gold or diamond
        switch (itemInHand.getType()) {
            case IRON_INGOT:
                ReinforceBlock(event, config.getInt("reinforcement.IRON_INGOT"), block, itemInHand);
                return;
            case GOLD_INGOT:
                ReinforceBlock(event, config.getInt("reinforcement.GOLD_INGOT"), block, itemInHand);
                return;
            case DIAMOND:
                ReinforceBlock(event, config.getInt("reinforcement.DIAMOND"), block, itemInHand);
                return;
            default:
                return;
        }
    }

    public void ReinforceBlock(final BlockDamageEvent event, final int health, final Block block,
            final ItemStack itemInHand) {

        // The player has punched a block and wants to reinforce it.
        Player p = event.getPlayer();
        Location blockLocation = block.getLocation();
        String block_id = blockLocation.getBlockX() + "," + blockLocation.getBlockY()
                + "," + blockLocation.getBlockZ();
        // First, get information about the player and if he is in a nation async from
        // the database
        String getPlayerInfoSQL = "SELECT uid, nation FROM player WHERE uid = ?";
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = sqlHelper.query(getPlayerInfoSQL, p.getUniqueId().toString());
                    if (rs.next()) {
                        // Check if block is already reinforced
                        String getReinforcementSQL = "SELECT * FROM block_reinforcement WHERE block_id = ? AND world = ?;";
                        ResultSet rsReinforcement = sqlHelper.query(getReinforcementSQL, block_id,
                                blockLocation.getWorld().getName());
                        if (rsReinforcement.next()) {
                            // If the block is already reinforced, tell the player that
                            p.sendMessage("§cThis block is already reinforced!");
                            return;
                        }

                        String nation = rs.getString("nation");
                        String uid = rs.getString("uid");

                        // Insert the information to the database async
                        String insertReinforcementSQL = "INSERT INTO block_reinforcement (block_id, world, health, nation, player_id, block_type, reinforcement_type) VALUES (?, ?, ?, ?, ?, ?, ?);";

                        sqlHelper.update(insertReinforcementSQL, block_id, event.getPlayer().getWorld().getName(),
                                health, nation, uid,
                                block.getType().toString(),
                                itemInHand.getType().name());

                        p.sendMessage("§aSuccessfully reinforced block!");
                        block.getWorld().spawnParticle(org.bukkit.Particle.DRAGON_BREATH,
                                block.getLocation(), 2, 0, 0, 0);
                        p.getInventory().getItemInMainHand()
                                .setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                        event.setCancelled(true);
                        return;
                    } else
                        throw new SQLException("Player could not be found in the database.");

                } catch (SQLException e) {
                    p.sendMessage(
                            "§cThere was an error finding your player info in the database, block is NOT reinforced");
                    p.sendMessage(e.getMessage());
                    e.printStackTrace();
                    return;
                }
            }
        });

    }

    @EventHandler
    public void onExplosionPrimeEvent(EntityExplodeEvent event) {
        plugin.getLogger().info(event.getEntity().toString());
        // loop through all the impacted block in the event.blockList and check if they
        // are reinforced
        for (Block block : event.blockList()) {
            String block_id = block.getX() + "," + block.getY() + "," + block.getZ();
            String getReinforcementSQL = "SELECT * FROM block_reinforcement WHERE block_id = ? AND world = ?;";
            try (ResultSet rs = sqlHelper.query(getReinforcementSQL, block_id,
                    block.getWorld().getName())) {
                if (rs.next()) {

                    // Remove 5 health from the block reinforcement depending on the hardness of the
                    // block, if the health is 0, remove the
                    // block
                    // reinforcement from the database
                    int newHealth = rs.getInt("health") - (int) Math.ceil((10 / block.getType().getHardness()));
                    plugin.getLogger().info("block " + block.getType().name() + " has new health " + newHealth + " "
                            + block.getType().getHardness());
                    if (newHealth <= 0) {
                        String removeReinforcementSQL = "DELETE FROM block_reinforcement WHERE block_id = ? AND world = ?;";
                        sqlHelper.updateAsync(removeReinforcementSQL, () -> {
                        }, block_id,
                                block.getWorld().getName());
                    } else {
                        // Update the health of the block reinforcement async
                        String updateReinforcementHealthSQL = "UPDATE block_reinforcement SET health = ? WHERE block_id = ? AND world = ?;";
                        // Remove the block from the event.blockList, so it doesn't get destroyed
                        event.blockList().remove(block);
                        sqlHelper.updateAsync(updateReinforcementHealthSQL, new SQLHelper.UpdateCallback() {
                            @Override
                            public void onQueryDone() throws SQLException {
                                // If the reinforced blocks health is dividable with 5, tell the player that
                                // are on the way to break the block
                                if (newHealth % 5 == 0) {
                                    event.getEntity()
                                            .sendMessage("§aYou are on the way to break the block! It has " + newHealth
                                                    + " health left!");
                                }

                            }
                        }, newHealth, block_id,
                                block.getWorld().getName());

                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
