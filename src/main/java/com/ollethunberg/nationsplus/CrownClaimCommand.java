package com.ollethunberg.nationsplus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrownClaimCommand extends DatabaseInteractor {
    // Use databaseInteractor to get the connection to the database.
    public CrownClaimCommand(Connection _connection) {
        super(_connection);
    }

    public static final ItemStack crown(String nation) {
        ItemStack crown = new ItemStack(Material.CHAINMAIL_HELMET, 1);
        crown.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        crown.addUnsafeEnchantment(Enchantment.OXYGEN, 2);
        crown.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
        ItemMeta crownMeta = crown.getItemMeta();
        crownMeta.setUnbreakable(true);
        crownMeta.setDisplayName("§6§lCrown");
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

    public void execute(Player p) {
        // Check if the player is holding an item with the tag "crown"

        if (p.getInventory().getItemInMainHand().hasItemMeta()
                && p.getInventory().getItemInMainHand().getItemMeta().hasLore()
                && p.getInventory().getItemInMainHand().getItemMeta().getLore().contains("§8crown")) {
            // Get the last line of the lore
            String nationName = p.getInventory().getItemInMainHand().getItemMeta().getLore()
                    .get(p.getInventory().getItemInMainHand().getItemMeta().getLore().size() - 1);

            // Check if the player is in a nation
            String isInNationSQLString = "SELECT nation FROM player WHERE uid = ? and nation = ?";
            try {
                ResultSet rs = sqlHelper.query(isInNationSQLString, p.getUniqueId().toString(), nationName);
                rs.next();
                // if the Player is part of the nation, the claim the crown and set the king_id
                // to the players uid
                if (rs.getString("nation") != null) {
                    String claimCrownSQLString = "UPDATE nation SET king_id = ? WHERE name = ?";
                    sqlHelper.update(claimCrownSQLString, p.getUniqueId().toString(), rs.getString("nation"));

                    p.sendMessage("§aYou have claimed the crown!");

                } else {
                    p.sendMessage("§cYou are not in the nation! Therefore, you can't claim the crown");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Message the user that something went wrong claiming the crown
                p.sendMessage("§cSomething went wrong claiming the crown. Make sure you are in a nation.");
            }

        } else {
            p.sendMessage("§cYou are not holding a crown!");
        }
    }

}
