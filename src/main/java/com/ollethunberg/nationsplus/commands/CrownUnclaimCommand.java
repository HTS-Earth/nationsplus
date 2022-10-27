package com.ollethunberg.nationsplus.commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import com.ollethunberg.nationsplus.lib.SQLHelper;

public class CrownUnclaimCommand {

    public void execute(Player p) {
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

                p.getInventory().addItem(CrownClaimCommand.crown(rs.getString("nation"))); // add the ItemStack to the
                p.sendMessage("§aYou have unclaimed the crown!");
            } else {
                p.sendMessage("§cYou are not the king of your nation! Therefore, you can't unclaim the crown");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Message the user that something went wrong claiming the crown
            p.sendMessage("§cSomething went wrong unclaiming the crown.");
        }
    }
}
