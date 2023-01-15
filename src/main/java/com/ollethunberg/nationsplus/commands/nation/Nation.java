package com.ollethunberg.nationsplus.commands.nation;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.ollethunberg.nationsplus.Events;
import com.ollethunberg.nationsplus.NationsPlus;
import com.ollethunberg.nationsplus.commands.crown.Crown;
import com.ollethunberg.nationsplus.commands.nation.commands.help.Help;
import com.ollethunberg.nationsplus.commands.nation.commands.nationrelationship.NationRelationship;
import com.ollethunberg.nationsplus.commands.nation.commands.tax.Tax;
import com.ollethunberg.nationsplus.lib.SQLHelper;
import com.ollethunberg.nationsplus.lib.exceptions.BadPermissionException;
import com.ollethunberg.nationsplus.lib.exceptions.IllegalArgumentException;
import com.ollethunberg.nationsplus.lib.exceptions.NationException;
import com.ollethunberg.nationsplus.lib.exceptions.NationNotFoundException;
import com.ollethunberg.nationsplus.lib.exceptions.PermissionException;
import com.ollethunberg.nationsplus.lib.exceptions.PlayerNotFoundException;
import com.ollethunberg.nationsplus.lib.helpers.NationHelper;
import com.ollethunberg.nationsplus.lib.helpers.PlayerHelper;
import com.ollethunberg.nationsplus.lib.helpers.WalletBalanceHelper;
import com.ollethunberg.nationsplus.lib.models.db.DBPlayer;
import com.ollethunberg.nationsplus.misc.Discord;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;

public class Nation extends WalletBalanceHelper {
    NationHelper nationHelper = new NationHelper();
    NationGUI nationGUI = new NationGUI();
    Help help = new Help();
    Tax tax = new Tax();
    PlayerHelper playerHelper = new PlayerHelper();
    NationRelationship nationRelationship = new NationRelationship();
    LuckPerms lpApi;

    public Nation() {
        super();
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            lpApi = provider.getProvider();
        }
    }

    void create(String nationName, String prefix, Player king) throws SQLException {
        String insertNewNationSQL = "INSERT INTO nation(name, prefix, king_id, created_date, kills, balance) VALUES (?, ?, ?, CURRENT_TIMESTAMP, 0,0);";
        SQLHelper.update(insertNewNationSQL, nationName, prefix, king.getUniqueId().toString());

        // Give a crown with the antion to the king
        ItemStack crown = Crown.crown(nationName);
        king.getInventory().setHelmet(crown);

        // Message the king that the nation was created
        king.sendMessage("§2Your nation was successfully created!");

        NationAutoComplete.nations.add(nationName);
        // execute commands:
        // nte group add [nationName]
        // nte group valtara permission nte.[nationName]
        // nte group valtara prefix '&a[(nationName)]&r'
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "nte group add " + nationName);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "nte group " + nationName + " permission nte."
                + nationName);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "nte group " + nationName + " prefix '&a["
                + nationName + "]&r '");

    }

    void list(Player player) throws SQLException {
        List<com.ollethunberg.nationsplus.lib.models.Nation> nations = nationHelper.getNations();
        nationGUI.openNationsGUI(nations, player);
    }

    void info(Player player, String nationName) throws SQLException, NationNotFoundException {
        com.ollethunberg.nationsplus.lib.models.Nation nation = nationHelper.getNation(nationName);
        nationGUI.openNationUI(nation, player);
    }

    void join(Player player, String nationName)
            throws SQLException, NationNotFoundException, PlayerNotFoundException, BadPermissionException {
        // get player
        DBPlayer dbPlayer = playerHelper.getPlayer(player.getUniqueId().toString());
        if (dbPlayer.nation != null) {
            throw new BadPermissionException(player, "You are already in a nation!");
        }
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
        // create random 5 digit code (letters)
        String code = "";
        for (int i = 0; i < 5; i++) {
            int random = (int) (Math.random() * 26 + 97);
            code += (char) random;
        }
        // save code to db
        String insertCodeSQL = "UPDATE player set discord_code = ? WHERE uid = ?";
        SQLHelper.update(insertCodeSQL, code, player.getUniqueId().toString());
        // send code to player
        player.spigot().sendMessage(Discord.getInviteLinkComponent());
        player.sendMessage("§aEnter your nation discord role by typing §e/code " + code
                + "§r§a in the main §l§9DISCORD§r channel");

        // add the permission nte.[nationName] to the player
        User user = lpApi.getUserManager().getUser(player.getUniqueId());

        user.data().add(Node.builder("nte." + nationName.toLowerCase()).build());
        lpApi.getUserManager().saveUser(user);
    }

    public void withdraw(Player player, Integer amount, String receiverPlayerName)
            throws PlayerNotFoundException, SQLException, PermissionException, NationException,
            IllegalArgumentException, NationNotFoundException {
        DBPlayer potentialNationOwner = playerHelper.getPlayer(player.getUniqueId().toString());
        if (potentialNationOwner == null)
            throw new PlayerNotFoundException(player, player.getName());
        if (potentialNationOwner.nation == null) {
            throw new NationException(player, "You are not in a nation");
        }
        com.ollethunberg.nationsplus.lib.models.Nation nation = nationHelper.getNation(
                potentialNationOwner.nation);
        if (!nation.king_id.equals(potentialNationOwner.uid)) {
            throw new PermissionException(player, "You are not the owner of the nation");
        }
        DBPlayer receiver = playerHelper.getPlayerByName(receiverPlayerName);
        if (receiver == null)
            throw new PlayerNotFoundException(player, receiverPlayerName);
        if (!receiver.nation.equals(potentialNationOwner.nation)) {
            throw new NationException(player, "Player is not in your nation");
        }
        if (amount < 0)
            throw new IllegalArgumentException(player, "Amount must be positive");
        if (amount > nation.balance)
            throw new IllegalArgumentException(player, "Nation does not have enough money");

        nationHelper.addMoney(potentialNationOwner.nation, -amount);
        addBalancePlayer(receiver.uid, amount);
        player.sendMessage("§aYou withdrew " + NationsPlus.dollarFormat.format(amount) + " from your nation");
        nationHelper.notifyNationMembers(potentialNationOwner.nation, "§2" + player.getName() + "§7 withdrew §a"
                + NationsPlus.dollarFormat.format(amount) + "§7 from the nation to §2" + receiverPlayerName + "§7!");
    }

    public void donate(Player player, Integer amount)
            throws PlayerNotFoundException, SQLException, PermissionException, NationNotFoundException,
            IllegalArgumentException {
        if (amount < 0)
            throw new IllegalArgumentException(player, "Amount must be positive");

        DBPlayer p = playerHelper.getPlayer(player.getUniqueId().toString());
        if (p == null)
            throw new PlayerNotFoundException(player, player.getName());
        if (p.nation == null) {
            throw new NationNotFoundException(player, "(your nation)");
        }
        try {
            addBalancePlayer(p.uid, -amount);
        } catch (java.lang.IllegalArgumentException e) {
            throw new IllegalArgumentException(player, "You do not have enough money");
        }

        nationHelper.addMoney(p.nation, amount);
        player.sendMessage("§aYou donated " + NationsPlus.dollarFormat.format(amount) + " to your nation");
        nationHelper.notifyNationMembers(p.nation, "§2" + player.getName() + "§7 donated §a"
                + NationsPlus.dollarFormat.format(amount) + "§7 to the nation!");
    }

    public void help(Player player, String... args) {
        help.help(player, args);
    }

    public void tax(Player player, String taxType, String taxIn)
            throws SQLException, IllegalArgumentException, NationNotFoundException, PlayerNotFoundException {
        tax.setTax(player, taxType, taxIn);
    }

    public void getStatus(Player player) throws SQLException {
        nationRelationship.listStatus(player);
    }
}
