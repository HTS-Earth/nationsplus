package com.ollethunberg.nationsplus.commands.crown;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ollethunberg.nationsplus.lib.SQLHelper;
import com.ollethunberg.nationsplus.lib.exceptions.IllegalArgumentException;
import com.ollethunberg.nationsplus.lib.exceptions.NationException;

public class Crown extends SQLHelper {
    public static final ItemStack crown(String nation) {
        ItemStack crown = new ItemStack(Material.CHAINMAIL_HELMET, 1);
        crown.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        crown.addUnsafeEnchantment(Enchantment.OXYGEN, 2);
        crown.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
        ItemMeta crownMeta = crown.getItemMeta();
        crownMeta.setUnbreakable(true);
        crownMeta.setDisplayName("§6§lCrown of " + nation);
        List<String> lore = new ArrayList<String>(); // create a List<String> for the lore
        lore.add("§eUse the crown to claim the throne");
        lore.add("§a/crown claim");
        lore.add("§8crown");
        // Add the nation name to the lore
        lore.add(nation);

        crownMeta.setLore(lore); // set the ItemMeta's lore to the List<String>
        crown.setItemMeta(crownMeta); // set the ItemStack's ItemMeta to the ItemMeta
        return crown;
    }

    public void unclaim(Player p) throws SQLException {
        // Check which player is the king of the nation
        String getKingSQLString = "SELECT n.king_id, n.name FROM player as p inner join nation as n on n.name = p.nation WHERE p.uid = ?";
        try {
            ResultSet rs = SQLHelper.query(getKingSQLString, p.getUniqueId().toString());
            rs.next();
            // If the player is the king, unclaim the crown
            if (rs.getString("king_id") != null && rs.getString("king_id").equals(p.getUniqueId().toString())) {
                String unclaimCrownSQLString = "UPDATE nation SET king_id = null WHERE king_id = ?";
                SQLHelper.update(unclaimCrownSQLString, p.getUniqueId().toString());
                // Give a golden helmet item to the player

                p.getInventory().addItem(Crown.crown(rs.getString("nation"))); // add the ItemStack to the
                p.sendMessage("§aYou have unclaimed the crown!");
            } else {
                p.sendMessage("§cYou are not the king of your nation! Therefore, you can't unclaim the crown");
            }
            rs.close();
        } catch (SQLException e) {
            // Message the user that something went wrong claiming the crown
            p.sendMessage("§cSomething went wrong unclaiming the crown.");
        }

    }

    public void claim(Player p) throws SQLException, NationException, IllegalArgumentException {
        // Check if the player is holding an item with the tag "crown"

        if (p.getInventory().getItemInMainHand().hasItemMeta()
                && p.getInventory().getItemInMainHand().getItemMeta().hasLore()
                && p.getInventory().getItemInMainHand().getItemMeta().getLore().contains("§8crown")) {
            // Get the last line of the lore
            String nationName = p.getInventory().getItemInMainHand().getItemMeta().getLore()
                    .get(p.getInventory().getItemInMainHand().getItemMeta().getLore().size() - 1);

            // Check if the player is in a nation
            String isInNationSQLString = "SELECT nation FROM player WHERE uid = ?";
            ResultSet rs = SQLHelper.query(isInNationSQLString, p.getUniqueId().toString());
            // Check if the player is in the nation
            rs.next();
            // if the Player is part of the nation, the claim the crown and set the king_id
            // to the players uid
            if (rs.getString("nation") != null && rs.getString("nation").equals(nationName)) {
                String claimCrownSQLString = "UPDATE nation SET king_id = ? WHERE name = ?";
                SQLHelper.update(claimCrownSQLString, p.getUniqueId().toString(), rs.getString("nation"));

                p.sendMessage("§aYou have claimed the crown!");
            } else {
                // See if there is a nation with the nation name
                String getNationSQLString = "SELECT name FROM nation WHERE name = ?";
                ResultSet nationRs = SQLHelper.query(getNationSQLString, nationName);
                if (nationRs.next()) {

                    String nation = nationRs.getString("name");
                    // Set the nation king_id to be null
                    String unclaimCrownSQLString = "UPDATE nation SET king_id = null WHERE name = ?";
                    SQLHelper.update(unclaimCrownSQLString, nation);
                    // Announce that the nation has fallen

                    // Make all the players that belonged to the nation nationless
                    String makeNationlessSQLString = "UPDATE player SET nation = null WHERE nation = ?";
                    SQLHelper.update(makeNationlessSQLString, nation);
                    // Remove the relationships of the nation
                    String removeRelationshipsSQLString = "DELETE FROM nation_relations WHERE nation_one = ? or nation_second = ?;";
                    SQLHelper.update(removeRelationshipsSQLString, nation, nation);
                    // Remove the nation
                    String removeNationSQLString = "DELETE FROM nation WHERE name = ?";
                    SQLHelper.update(removeNationSQLString, nation);
                    // Remove the crown from the players main hand
                    p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

                    Bukkit.getServer()
                            .broadcastMessage("§c§lThe nation " + nation + " has fallen!");
                    Bukkit.getServer().broadcastMessage("§aAll its members are not nationless");
                } else {
                    throw new NationException(p, "There is no nation that belongs to this crown!");
                }

            }

        } else {
            throw new IllegalArgumentException(p, "You are not holding a crown!");
        }
    }
}
