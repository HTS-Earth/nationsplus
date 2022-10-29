package com.ollethunberg.nationsplus.commands;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.ollethunberg.nationsplus.NationsPlus;
import com.ollethunberg.nationsplus.lib.helpers.NationHelper;
import com.ollethunberg.nationsplus.lib.helpers.PlayerHelper;
import com.ollethunberg.nationsplus.lib.helpers.WalletBalanceHelper;
import com.ollethunberg.nationsplus.lib.models.Nation;
import com.ollethunberg.nationsplus.lib.models.db.DBPlayer;

public class NationMoneyCommands {
    PlayerHelper playerHelper = new PlayerHelper();
    NationHelper nationHelper = new NationHelper();
    WalletBalanceHelper walletBalanceHelper = new WalletBalanceHelper();

    public void notifyNationMembers(String nationName, String message) throws SQLException {
        for (DBPlayer player : playerHelper.getPlayersInNation(nationName)) {
            Player p = Bukkit.getServer().getPlayer(UUID.fromString(player.uid));
            if (p != null) {
                p.sendMessage(message);
            }
        }
    }

    public void donate(Player player, Integer amount) throws Exception {
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
        player.sendMessage("§aYou donated " + NationsPlus.dollarFormat.format(amount) + " to your nation");
        notifyNationMembers(p.nation, "§2" + player.getName() + "§7 donated §a"
                + NationsPlus.dollarFormat.format(amount) + "§7 to the nation!");
    }

    public void withdraw(Player player, Integer amount, String receiverPlayerName) throws Exception {
        DBPlayer potentialNationOwner = playerHelper.getPlayer(player.getUniqueId().toString());
        if (potentialNationOwner == null)
            throw new Exception("Player not found in database");
        if (potentialNationOwner.nation == null) {
            throw new Exception("You are not in a nation");
        }
        Nation nation = nationHelper.getNation(potentialNationOwner.nation);
        if (!nation.king_id.equals(potentialNationOwner.uid)) {
            throw new Exception("You are not the owner of the nation");
        }
        DBPlayer receiver = playerHelper.getPlayerByName(receiverPlayerName);
        if (receiver == null)
            throw new Exception("Player not found in database");
        if (!receiver.nation.equals(potentialNationOwner.nation)) {
            throw new Exception("Player is not in your nation");
        }
        if (amount < 0)
            throw new Exception("Amount must be positive");
        if (amount > nation.balance)
            throw new Exception("Nation does not have enough money");

        nationHelper.addMoney(potentialNationOwner.nation, -amount);
        walletBalanceHelper.addBalancePlayer(receiver.uid, amount);
        player.sendMessage("§aYou withdrew " + NationsPlus.dollarFormat.format(amount) + " from your nation");
        notifyNationMembers(potentialNationOwner.nation, "§2" + player.getName() + "§7 withdrew §a"
                + NationsPlus.dollarFormat.format(amount) + "§7 from the nation to §2" + receiverPlayerName + "§7!");
    }
}
