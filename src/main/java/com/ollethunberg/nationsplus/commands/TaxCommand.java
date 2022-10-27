package com.ollethunberg.nationsplus.commands;

import org.bukkit.entity.Player;

import com.ollethunberg.nationsplus.lib.helpers.NationHelper;
import com.ollethunberg.nationsplus.lib.helpers.PlayerHelper;
import com.ollethunberg.nationsplus.lib.models.Nation;
import com.ollethunberg.nationsplus.lib.models.db.DBPlayer;

public class TaxCommand {
    PlayerHelper playerHelper = new PlayerHelper();
    NationHelper nationHelper = new NationHelper();

    public void execute(Player player, String tax) throws Exception {
        float taxFloat = Float.parseFloat(tax);
        if (taxFloat < 0 || taxFloat > 100)
            throw new Exception("Tax must be between 0 and 100");
        DBPlayer p = playerHelper.getPlayer(player.getUniqueId().toString());
        if (p.nation == null)
            throw new Exception("You are not in a nation");

        Nation nation = nationHelper.getNation(p.nation);
        if (nation.king_id != p.uid)
            throw new Exception("You are not the king of your nation");

        nationHelper.setTax(p.nation, taxFloat);

    }
}