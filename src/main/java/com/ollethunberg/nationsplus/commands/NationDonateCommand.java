package com.ollethunberg.nationsplus.commands;

import org.bukkit.entity.Player;

import com.ollethunberg.nationsplus.NationsPlus;
import com.ollethunberg.nationsplus.lib.helpers.NationHelper;
import com.ollethunberg.nationsplus.lib.helpers.PlayerHelper;
import com.ollethunberg.nationsplus.lib.helpers.WalletBalanceHelper;
import com.ollethunberg.nationsplus.lib.models.db.DBPlayer;

public class NationDonateCommand {
    PlayerHelper playerHelper = new PlayerHelper();
    NationHelper nationHelper = new NationHelper();
    WalletBalanceHelper walletBalanceHelper = new WalletBalanceHelper();

    public void execute(Player player, Integer amount) throws Exception {
        if (amount < 0)
            throw new Exception("Amount must be positive");

        DBPlayer p = playerHelper.getPlayer(player.getUniqueId().toString());
        if (p == null)
            throw new Exception("Player not found in database");
        if (p.nation == null) {
            throw new Exception("You are not in a nation");
        }
        walletBalanceHelper.addBalancePlayer(p.uid, -amount);
        nationHelper.addMoney(p.nation, amount);
        player.sendMessage("§aDonated " + NationsPlus.dollarFormat.format(amount) + " to your nation");
    }

}
