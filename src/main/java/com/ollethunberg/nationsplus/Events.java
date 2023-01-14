package com.ollethunberg.nationsplus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.ollethunberg.nationsplus.commands.crown.Crown;
import com.ollethunberg.nationsplus.lib.SQLHelper;
import com.ollethunberg.nationsplus.lib.exceptions.ExceptionBase;
import com.ollethunberg.nationsplus.lib.helpers.PlayerHelper;
import com.ollethunberg.nationsplus.lib.models.db.DBPlayer;

public class Events implements Listener {

    private Plugin plugin = NationsPlus.getPlugin(NationsPlus.class);
    private Configuration config = plugin.getConfig();
    public static HashMap<String, String> nationPrefixCache = new HashMap<String, String>();
    public static HashMap<String, String> rankPrefixCache = new HashMap<String, String>();
    private PlayerHelper playerHelper = new PlayerHelper();

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {

        try {

            // Check if they are in the database.
            String isPlayerInDatabaseSQL = "SELECT EXISTS ( SELECT FROM player WHERE uid = ? );";
            String playerId = event.getPlayer().getUniqueId().toString();
            ResultSet rs = SQLHelper.query(isPlayerInDatabaseSQL, playerId);

            rs.next();

            if (!rs.getBoolean("exists")) {
                NationsPlus.LOGGER.info("New player joined!");
                // Insert into database
                String insertNewPlayerSQL = "INSERT INTO player(uid, last_login, player_name, kills, deaths) VALUES (?, CURRENT_TIMESTAMP, ?, 0,0);";
                SQLHelper.update(insertNewPlayerSQL, playerId,
                        event.getPlayer().getName());
                // Teleport the player to the spawn
            } else {
                NationsPlus.LOGGER.info("Player does exist in the database!");
                // Check if the player has a ban on them on the player_bans table
                String playerBannedUntil = "SELECT banned_date + (banned_minutes * interval '1 minute') as banned_until, player_id FROM player_bans WHERE player_id = ? order by banned_date DESC;";
                ResultSet rsPlayerBannedUntil = SQLHelper.query(playerBannedUntil,
                        playerId);
                rsPlayerBannedUntil.next();

                String updatePlayerLastLoginSQL = "UPDATE player SET last_login=CURRENT_TIMESTAMP, player_name=? where uid = ?";
                SQLHelper.update(updatePlayerLastLoginSQL, event.getPlayer().getDisplayName(),
                        playerId);
                // Update our prefix cache with the prefix of the nation that the player is in.
                String getPlayerNationSQL = "SELECT n.prefix, p.rank, n.king_id FROM player as p inner join nation as n on p.nation=n.name WHERE p.uid = ?;";
                ResultSet rsPlayerNation = SQLHelper.query(getPlayerNationSQL,
                        playerId);
                if (rsPlayerNation.next()) {

                    if (rsPlayerNation.getString("rank").equals("vip")) {
                        rankPrefixCache.put(playerId, "§6[VIP§6]");
                    }
                    // Check if the player is the king of the nation
                    if (rsPlayerNation.getString("king_id").equals(playerId)) {
                        rankPrefixCache.put(playerId,
                                "§6[King§6]");
                    }
                    nationPrefixCache.put(playerId,
                            rsPlayerNation.getString("prefix"));
                } else {
                    NationsPlus.LOGGER.info("Player is not in a nation!");
                    event.getPlayer().sendMessage("§aPlease join a nation!");
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /* Event handler inventory move event */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Cancel the event if the clicked item has "§8crown" in its lore
        event.setCancelled(Crown.isCrownItem(event.getCurrentItem()));
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        // Cancel the event if the dropped item has "§8crown" in its lore
        event.setCancelled(Crown.isCrownItem(event.getItemDrop().getItemStack()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Get the player who died
        Player victim = event.getEntity();

        try {
            // Check if the player was wearing a helmet with "§8crown" in its lore
            if (Crown.isCrownItem(victim.getInventory().getHelmet())) {
                // check if the player is dead by a player
                if (victim.getKiller() instanceof Player) {
                    // Get the player who killed the player
                    Player killer = victim.getKiller();

                    // check which nation the killer belongs to
                    DBPlayer killerDB = playerHelper.getPlayer(killer.getUniqueId().toString());
                    DBPlayer victimDB = playerHelper.getPlayer(victim.getUniqueId().toString());
                    /* Logic if killer and victim is in the same nation */
                    if (killerDB.nation.equals(victimDB.nation)) {
                        // remove the crown from the victim's head
                        // copy the crown to the killer's head
                        ItemStack crown = victim.getInventory().getHelmet();
                        ItemStack killersOldHelmet = killer.getInventory().getHelmet();
                        if (killersOldHelmet != null) {
                            // check if killers inventory is full
                            if (killer.getInventory().firstEmpty() == -1) {
                                // drop the old helmet on the ground
                                killer.getWorld().dropItemNaturally(killer.getLocation(), killersOldHelmet);
                            } else {
                                // put the old helmet in the killer's inventory
                                killer.getInventory().addItem(killersOldHelmet);
                            }
                        }
                        killer.getInventory().setHelmet(crown);
                        victim.getInventory().setHelmet(null);
                        // check if the killer is the same nation as the victim
                        // if that is the case, make the killer the new king of the nation
                        String updateNationKingSQL = "UPDATE nation SET king=? WHERE name=?;";
                        SQLHelper.update(updateNationKingSQL, killerDB.uid,
                                killerDB.nation);

                        return;
                    } else {
                        /* Victim and killer is not in the same nation. */
                    }

                }
            }
        } catch (SQLException e) {
            // log error
            NationsPlus.LOGGER.info("Error: " + e.getMessage());
        } catch (ExceptionBase e) {
            // log error
            NationsPlus.LOGGER.info("Error: " + e.getMessage());
        }

    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        // remove player from cache
        nationPrefixCache.remove(event.getPlayer().getUniqueId().toString());
        rankPrefixCache.remove(event.getPlayer().getUniqueId().toString());
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        // Get preifix from the cache.
        String nationPrefix = nationPrefixCache.get(e.getPlayer().getUniqueId().toString());
        String rankPrefix = rankPrefixCache.get(e.getPlayer().getUniqueId().toString());
        if (nationPrefix != null && rankPrefix == null) {
            e.setFormat("§e[§r" + nationPrefix + "§e]§r %s : %s");
        } else if (nationPrefix != null && rankPrefix != null) {
            e.setFormat("§e[§r" + nationPrefix + "§e]§r " + rankPrefix + "§r %s : %s");
        }
    }

    // Block reinforcement eventhandler
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.AIR)
            return;

        // Check if there is a reinforcement on that block
        String getReinforcementSQL = "SELECT * FROM block_reinforcement WHERE block_id = ? AND world = ?;";
        String block_id = event.getBlock().getLocation().getBlockX() + ","
                + event.getBlock().getLocation().getBlockY()
                + "," + event.getBlock().getLocation().getBlockZ();
        try (ResultSet rs = SQLHelper.query(getReinforcementSQL, block_id,
                event.getBlock().getLocation().getWorld().getName())) {
            if (rs.next()) {
                String nationOfClaim = rs.getString("nation");
                DBPlayer player = playerHelper.getPlayer(event.getPlayer().getUniqueId().toString());

                Boolean isPlayerOwnerOfClaim = rs.getString("player_id")
                        .equals(player.uid);
                String reinforcementMode = rs.getString("reinforcement_mode").equals("PRIVATE") ? "private"
                        : "nation";
                // Check if the player is the owner of the reinforcement
                if ((isPlayerOwnerOfClaim
                        && reinforcementMode.equals("private"))
                        || (nationOfClaim.equals(player.nation)
                                && reinforcementMode.equals("nation"))) {
                    // Remove the reinforcement
                    String removeReinforcementSQL = "DELETE FROM block_reinforcement WHERE block_id = ? AND world = ?;";
                    SQLHelper.update(removeReinforcementSQL, block_id,
                            event.getBlock().getLocation().getWorld().getName());
                    return;

                }
                int newHealth = rs.getInt("health") - 1;
                if (newHealth > 0) {
                    event.setCancelled(true);
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                        @Override
                        public void run() {
                            // Instanstiate particles at the blocks position
                            event.getBlock().getWorld().spawnParticle(org.bukkit.Particle.HEART,
                                    event.getBlock().getLocation(), 3, 0, 0, 0);
                            // Update the health of the block reinforcement async
                            String updateReinforcementHealthSQL = "UPDATE block_reinforcement SET health = health - 1 WHERE block_id = ? AND world = ?;";
                            try {
                                SQLHelper.update(updateReinforcementHealthSQL, block_id,
                                        event.getBlock().getLocation().getWorld().getName());
                            } catch (SQLException e) {
                                event.getPlayer()
                                        .sendMessage("§cSomething went wrong, please contact an admin!");
                                e.printStackTrace();
                            }
                            if (newHealth % 5 == 0) {
                                event.getPlayer()
                                        .sendMessage(
                                                "§aYou are on the way to break the block! It has " + newHealth
                                                        + " health left!");
                            }
                        }
                    });

                } else {
                    // If the block is broken, remove the reinforcement from the database
                    String removeReinforcementSQL = "DELETE FROM block_reinforcement WHERE block_id = ? AND world = ?;";
                    SQLHelper.update(removeReinforcementSQL, block_id,
                            event.getBlock().getLocation().getWorld().getName());
                }

            }
        } catch (SQLException e) {
            NationsPlus.LOGGER.warning("Error: " + e.getMessage());
        } catch (ExceptionBase e) {
            NationsPlus.LOGGER.warning(e.getMessage());
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

        if (block.getType() == Material.AIR || !block.getType().isSolid())
            return;
        // check if the hardness of the block is 0
        if (block.getType().getHardness() == 0) {
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
            case STICK:
                // If the item is a stick, check health and the owner of the reinforcement
                String getReinforcementSQL = "SELECT r.health,p.player_name, r.nation, r.reinforcement_mode  FROM block_reinforcement as r left join player as p on r.player_id = p.uid WHERE r.block_id = ? AND r.world = ?;";
                String block_id = block.getLocation().getBlockX() + "," + block.getLocation().getBlockY() + ","
                        + block.getLocation().getBlockZ();
                try (ResultSet rs = SQLHelper.query(getReinforcementSQL, block_id,
                        block.getLocation().getWorld().getName())) {
                    if (rs.next()) {
                        boolean isReinforcedByNation = (rs.getString("reinforcement_mode")).equals("NATION");
                        String owner = isReinforcedByNation ? ("§6[§r" + rs.getString("nation") + "§6]§r (§cnation§r)")
                                : rs.getString("player_name");

                        event.getPlayer().sendMessage(
                                "§aThis block is reinforced by "
                                        + owner + " with " + rs.getInt("health") + " health!");

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }

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
        String getPlayerInfoSQL = "SELECT uid, nation, reinforcement_mode FROM player WHERE uid = ?";
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = SQLHelper.query(getPlayerInfoSQL, p.getUniqueId().toString());
                    if (rs.next()) {
                        // Check if block is already reinforced
                        String getReinforcementSQL = "SELECT * FROM block_reinforcement WHERE block_id = ? AND world = ?;";
                        ResultSet rsReinforcement = SQLHelper.query(getReinforcementSQL, block_id,
                                blockLocation.getWorld().getName());
                        if (rsReinforcement.next()) {
                            // If the block is already reinforced, tell the player that
                            p.sendMessage("§cThis block is already reinforced!");
                            return;
                        }

                        String nation = rs.getString("nation");
                        String uid = rs.getString("uid");
                        String reinforcement_mode = rs.getString("reinforcement_mode");

                        // Insert the information to the database async
                        String insertReinforcementSQL = "INSERT INTO block_reinforcement (block_id, world, health, nation, player_id, block_type, reinforcement_type, reinforcement_mode) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

                        SQLHelper.update(insertReinforcementSQL, block_id, event.getPlayer().getWorld().getName(),
                                health, nation, uid,
                                block.getType().toString(),
                                itemInHand.getType().name(), reinforcement_mode);

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
        NationsPlus.LOGGER.info(event.getEntity().toString());
        // loop through all the impacted block in the event.blockList and check if they
        // are reinforced
        for (Block block : event.blockList()) {
            String block_id = block.getX() + "," + block.getY() + "," + block.getZ();
            String getReinforcementSQL = "SELECT * FROM block_reinforcement WHERE block_id = ? AND world = ?;";
            try (ResultSet rs = SQLHelper.query(getReinforcementSQL, block_id,
                    block.getWorld().getName())) {
                if (rs.next()) {

                    // Remove 5 health from the block reinforcement depending on the hardness of the
                    // block, if the health is 0, remove the
                    // block
                    // reinforcement from the database
                    int newHealth = rs.getInt("health") - (int) Math.ceil((10 / block.getType().getHardness()));
                    NationsPlus.LOGGER.info("block " + block.getType().name() + " has new health " + newHealth + " "
                            + block.getType().getHardness());
                    if (newHealth <= 0) {
                        String removeReinforcementSQL = "DELETE FROM block_reinforcement WHERE block_id = ? AND world = ?;";
                        SQLHelper.update(removeReinforcementSQL, block_id,
                                block.getWorld().getName());
                    } else {
                        // Update the health of the block reinforcement async
                        String updateReinforcementHealthSQL = "UPDATE block_reinforcement SET health = ? WHERE block_id = ? AND world = ?;";
                        // Remove the block from the event.blockList, so it doesn't get destroyed
                        event.blockList().remove(block);
                        SQLHelper.update(updateReinforcementHealthSQL, newHealth, block_id,
                                block.getWorld().getName());
                        if (newHealth % 5 == 0) {
                            event.getEntity()
                                    .sendMessage("§aYou are on the way to break the block! It has " + newHealth
                                            + " health left!");
                        }

                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
