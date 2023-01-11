package com.ollethunberg.nationsplus.commands.nation.commands.tax;

import java.sql.SQLException;

import org.bukkit.entity.Player;

import com.ollethunberg.nationsplus.lib.helpers.NationHelper;
import com.ollethunberg.nationsplus.lib.helpers.PlayerHelper;
import com.ollethunberg.nationsplus.lib.models.Nation;
import com.ollethunberg.nationsplus.lib.models.db.DBPlayer;

public class Tax {
    PlayerHelper playerHelper = new PlayerHelper();
    NationHelper nationHelper = new NationHelper();

    public void setTax(Player player, String taxType, String tax) throws IllegalArgumentException, SQLException {
        float taxFloat = Float.parseFloat(tax);
        if (taxFloat < 0 || taxFloat > 100)
            throw new IllegalArgumentException("Tax must be between 0 and 100");
        DBPlayer p = playerHelper.getPlayer(player.getUniqueId().toString());
        if (p.nation == null)
            throw new IllegalArgumentException("You are not in a nation");

        Nation nation = nationHelper.getNation(p.nation);
        if (!nation.king_id.equals(p.uid))
            throw new IllegalArgumentException("You are not the king of your nation");

        nationHelper.setTax(p.nation, taxType, taxFloat);

        player.sendMessage("§aTax set to " + taxFloat + "%");

    }
}