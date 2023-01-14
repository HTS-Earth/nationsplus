package com.ollethunberg.nationsplus.commands.crown;

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
import com.ollethunberg.nationsplus.lib.exceptions.PlayerNotFoundException;
import com.ollethunberg.nationsplus.lib.helpers.PlayerHelper;
import com.ollethunberg.nationsplus.lib.models.db.DBPlayer;

public class Crown extends SQLHelper {
    PlayerHelper playerHelper = new PlayerHelper();

    public static final ItemStack crown(String nation) {
        ItemStack crown = new ItemStack(Material.CHAINMAIL_HELMET, 1);
        crown.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        crown.addUnsafeEnchantment(Enchantment.OXYGEN, 2);
        crown.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
        ItemMeta crownMeta = crown.getItemMeta();
        crownMeta.setUnbreakable(true);
        crownMeta.setDisplayName("§6§lCrown of " + nation);
        List<String> lore = new ArrayList<String>(); // create a List<String> for the lore
        lore.add("§a/crown pass ");
        lore.add("§8crown");
        // Add the nation name to the lore
        lore.add(nation);

        crownMeta.setLore(lore); // set the ItemMeta's lore to the List<String>
        crown.setItemMeta(crownMeta); // set the ItemStack's ItemMeta to the ItemMeta
        return crown;
    }

    public void pass(Player oldKing, String newKingName)
            throws SQLException, PlayerNotFoundException, IllegalArgumentException {
        // check if the oldking has a helmet that is a crown
        if (oldKing.getPlayer().getName().equals(newKingName)) {
            throw new IllegalArgumentException(oldKing, "You can't pass the crown to yourself!");
        }
        if (!isCrownItem(oldKing.getInventory().getHelmet())) {
            throw new IllegalArgumentException(oldKing, "You are not wearing a crown!");
        }
        // check if they are in the same nation
        DBPlayer dbOldKing = playerHelper.getPlayer(oldKing.getUniqueId().toString());
        DBPlayer dbNewKing = playerHelper.getPlayerByName(newKingName);

        if (!dbOldKing.nation.equals(dbNewKing.nation)) {
            throw new IllegalArgumentException(oldKing, "You are not in the same nation as the new king!");
        }
        // crown the new king
        String claimCrownSQLString = "UPDATE nation SET king_id = ? WHERE name = ?";
        SQLHelper.update(claimCrownSQLString, dbNewKing.uid, dbOldKing.nation);
        // remove the crown from the old king
        oldKing.getInventory().setHelmet(null);
        // give the crown to the new king
        Player newKing = Bukkit.getPlayer(newKingName);
        newKing.getInventory().setHelmet(crown(dbOldKing.nation));

        // Message the old king that they have passed the crown
        oldKing.sendMessage("§aYou have passed the crown to " + newKingName);
        // Message the new king that they have been crowned
        newKing.sendMessage("§aYou have been crowned as the king of " + dbOldKing.nation);

        // announce to the server that the crown has been passed
        Bukkit.broadcastMessage("§l§6[NEW KING§6]§r [§2§l" + dbOldKing.nation + "§r] §4" + oldKing.getName()
                + "§a has passed the crown to §6§l" + newKingName + "§a!");

    }

    // Helper method to check if an item has "§8crown" in its lore
    public static boolean isCrownItem(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            return meta.hasLore() && meta.getLore().contains("§8crown");
        }
        return false;
    }

}
