package com.ollethunberg.nationsplus.lib.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ollethunberg.nationsplus.lib.SQLHelper;
import com.ollethunberg.nationsplus.lib.models.Nation;
import com.ollethunberg.nationsplus.lib.models.db.DBNation;

public class NationHelper extends SQLHelper {
    public DBNation serializeDBNation(ResultSet rs) throws SQLException {
        DBNation nation = new Nation();

        nation.name = rs.getString("name");
        nation.king_id = rs.getString("king_id");
        nation.successor_id = rs.getString("successor_id");
        nation.balance = rs.getFloat("balance");
        nation.kills = rs.getInt("kills");

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

    public void setTax(String nationName, float tax) throws SQLException {
        update("UPDATE nation SET tax=? WHERE name=?", tax, nationName);
    }

    public void addMoney(String nationName, float amount) throws SQLException {
        update("UPDATE nation SET balance=balance+? WHERE name=?", amount, nationName);
    }
}
