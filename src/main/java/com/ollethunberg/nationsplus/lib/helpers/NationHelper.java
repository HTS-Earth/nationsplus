package com.ollethunberg.nationsplus.lib.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.ollethunberg.nationsplus.lib.SQLHelper;
import com.ollethunberg.nationsplus.lib.models.Nation;
import com.ollethunberg.nationsplus.lib.models.db.DBNation;
import com.ollethunberg.nationsplus.lib.models.db.DBPlayer;
import com.ollethunberg.nationsplus.lib.exceptions.IllegalArgumentException;

public class NationHelper extends SQLHelper {
    PlayerHelper playerHelper = new PlayerHelper();

    public DBNation serializeDBNation(ResultSet rs) throws SQLException {
        DBNation nation = new Nation();

        nation.name = rs.getString("name");
        nation.king_id = rs.getString("king_id");
        nation.successor_id = rs.getString("successor_id");
        nation.balance = rs.getFloat("balance");
        nation.kills = rs.getInt("kills");
        nation.x = rs.getInt("x");
        nation.y = rs.getInt("y");
        nation.z = rs.getInt("z");

        return nation;
    }

    public Nation serializeNation(ResultSet rs) throws SQLException {
        Nation nation = (Nation) serializeDBNation(rs);
        System.out.println(nation);
        nation.king_name = rs.getString("king_name");
        nation.membersCount = rs.getInt("membersCount");

        return nation;
    }

    public Nation getNation(String nationName) throws SQLException {

        ResultSet rs = query(
                "SELECT n.*, p.player_name as king_name, (select count(p.*)from player as p where p.nation = n.name) as \"membersCount\" from nation as n inner join player as p on p.uid=n.king_id where n.name=?",
                nationName);
        if (!rs.next()) {
            throw new Error("No nation found");
        }
        return serializeNation(rs);

    }

    public List<Nation> getNations() throws SQLException {
        List<Nation> nations = new ArrayList<Nation>();
        ResultSet rs = query(
                "SELECT n.*, p.player_name as king_name, (select count(p.*)from player as p where p.nation = n.name) as \"membersCount\" from nation as n inner join player as p on p.uid=n.king_id");
        while (rs.next()) {
            nations.add(serializeNation(rs));
        }
        return nations;
    }

    public void setTax(Player player, String nationName, String taxType, float tax)
            throws SQLException, IllegalArgumentException {
        switch (taxType) {
            case "income":
                update("UPDATE nation SET income_tax=? WHERE name=?", tax, nationName);
                break;
            case "transfer":
                update("UPDATE nation SET transfer_tax=? WHERE name=?", tax, nationName);
                break;
            case "market":
                update("UPDATE nation SET market_tax=? WHERE name=?", tax, nationName);
                break;
            case "vat":
                update("UPDATE nation SET vat_tax=? WHERE name=?", tax, nationName);
                break;
            default:
                throw new IllegalArgumentException(player, "Valid tax types are income, transfer, market and vat");
        }

    }

    public void addMoney(String nationName, float amount) throws SQLException {
        update("UPDATE nation SET balance=balance+? WHERE name=?", amount, nationName);
    }

    public void notifyNationMembers(String nationName, String message) throws SQLException {
        for (DBPlayer player : playerHelper.getPlayersInNation(nationName)) {
            Player p = Bukkit.getServer().getPlayer(UUID.fromString(player.uid));
            if (p != null) {
                p.sendMessage(message);
            }
        }
    }

}
