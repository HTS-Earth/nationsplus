package com.ollethunberg.nationsplus.commands.nation;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ollethunberg.nationsplus.Events;
import com.ollethunberg.nationsplus.NationsPlus;
import com.ollethunberg.nationsplus.commands.crown.Crown;
import com.ollethunberg.nationsplus.commands.nation.commands.help.Help;
import com.ollethunberg.nationsplus.commands.nation.commands.nationrelationship.NationRelationship;
import com.ollethunberg.nationsplus.commands.nation.commands.tax.Tax;
import com.ollethunberg.nationsplus.lib.SQLHelper;
import com.ollethunberg.nationsplus.lib.helpers.NationHelper;
import com.ollethunberg.nationsplus.lib.helpers.PlayerHelper;
import com.ollethunberg.nationsplus.lib.helpers.WalletBalanceHelper;
import com.ollethunberg.nationsplus.lib.models.db.DBPlayer;

public class Nation extends WalletBalanceHelper {
    NationHelper nationHelper = new NationHelper();
    NationGUI nationGUI = new NationGUI();
    Help help = new Help();
    Tax tax = new Tax();
    PlayerHelper playerHelper = new PlayerHelper();
    NationRelationship nationRelationship = new NationRelationship();

    void create(String nationName, String prefix, Player king) throws SQLException {
        String insertNewNationSQL = "INSERT INTO nation(name, prefix, king_id, created_date, kills, balance) VALUES (?, ?, ?, CURRENT_TIMESTAMP, 0,0);";
        SQLHelper.update(insertNewNationSQL, nationName, prefix, king.getUniqueId().toString());

        // Give a crown with the antion to the king
        ItemStack crown = Crown.crown(nationName);
        king.getInventory().setHelmet(crown);

        // Message the king that the nation was created

        king.sendMessage("§2Your nation was successfully created!");
    }

    void list(Player player) throws SQLException {
        List<com.ollethunberg.nationsplus.lib.models.Nation> nations = nationHelper.getNations();
        nationGUI.openNationsGUI(nations, player);
    }

    void info(Player player, String nationName) throws SQLException {
        com.ollethunberg.nationsplus.lib.models.Nation nation = nationHelper.getNation(nationName);
        nationGUI.openNationUI(nation, player);
    }

    void join(Player player, String nationName) throws SQLException {
        // get nation
        com.ollethunberg.nationsplus.lib.models.Nation nation = nationHelper.getNation(nationName);
        String updatePlayerNationIdSQL = "UPDATE player SET nation = ? WHERE uid = ?";
        SQLHelper.update(updatePlayerNationIdSQL, nationName, player.getUniqueId().toString());
        Events.nationPrefixCache.remove(player.getUniqueId().toString());
        Events.nationPrefixCache.put(player.getUniqueId().toString(), nation.prefix);
        Location nationLocation = new Location(Bukkit.getWorld("world"), nation.x,
                nation.y, nation.z);

        player.teleport(nationLocation);
        player.sendMessage("§aYou have successfully joined the nation!");

    }

    public void withdraw(Player player, Integer amount, String receiverPlayerName) throws Exception {
        DBPlayer potentialNationOwner = playerHelper.getPlayer(player.getUniqueId().toString());
        if (potentialNationOwner == null)
            throw new Exception("Player not found in database");
        if (potentialNationOwner.nation == null) {
            throw new Exception("You are not in a nation");
        }
        com.ollethunberg.nationsplus.lib.models.Nation nation = nationHelper.getNation(potentialNationOwner.nation);
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
        addBalancePlayer(receiver.uid, amount);
        player.sendMessage("§aYou withdrew " + NationsPlus.dollarFormat.format(amount) + " from your nation");
        nationHelper.notifyNationMembers(potentialNationOwner.nation, "§2" + player.getName() + "§7 withdrew §a"
                + NationsPlus.dollarFormat.format(amount) + "§7 from the nation to §2" + receiverPlayerName + "§7!");
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
        addBalancePlayer(p.uid, -amount);
        nationHelper.addMoney(p.nation, amount);
        player.sendMessage("§aYou donated " + NationsPlus.dollarFormat.format(amount) + " to your nation");
        nationHelper.notifyNationMembers(p.nation, "§2" + player.getName() + "§7 donated §a"
                + NationsPlus.dollarFormat.format(amount) + "§7 to the nation!");
    }

    public void help(Player player, String... args) {
        help.help(player, args);
    }

    public void tax(Player player, String taxType, String taxIn) throws Exception {
        tax.setTax(player, taxType, taxIn);
    }

    public void getStatus(Player player) throws Exception {
        nationRelationship.listStatus(player);
    }
}
