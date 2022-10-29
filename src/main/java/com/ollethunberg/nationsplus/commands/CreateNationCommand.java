package com.ollethunberg.nationsplus.commands;

import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.ollethunberg.nationsplus.NationsPlus;
import com.ollethunberg.nationsplus.lib.SQLHelper;

public class CreateNationCommand {

    Plugin plugin = NationsPlus.getPlugin(NationsPlus.class);

    public void execute(String nationName, String prefix, Player king) {
        try {
            String insertNewNationSQL = "INSERT INTO nation(name, prefix, king_id, created_date, kills, balance) VALUES (?, ?, ?, CURRENT_TIMESTAMP, 0,0);";
            SQLHelper.update(insertNewNationSQL, nationName, prefix, king.getUniqueId().toString());

            // Give a crown with the antion to the king
            ItemStack crown = CrownClaimCommand.crown(nationName);
            king.getInventory().addItem(crown);

            // Message the king that the nation was created

            king.sendMessage("§2Your nation was successfully created!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
