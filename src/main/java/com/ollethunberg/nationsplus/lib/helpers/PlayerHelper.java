package com.ollethunberg.nationsplus.lib.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ollethunberg.nationsplus.lib.SQLHelper;
import com.ollethunberg.nationsplus.lib.models.db.DBPlayer;

public class PlayerHelper extends SQLHelper {
    public DBPlayer serializeDBPlayer(ResultSet rs) throws SQLException {
        DBPlayer player = new DBPlayer();
        player.uid = rs.getString("uid");
        player.player_name = rs.getString("player_name");
        player.balance = rs.getFloat("balance");
        player.nation = rs.getString("nation");
        player.kills = rs.getInt("kills");
        player.deaths = rs.getInt("deaths");
        return player;
    }

    public List<DBPlayer> serializeDBPlayers(ResultSet rs) throws SQLException {
        List<DBPlayer> players = new ArrayList<DBPlayer>();
        while (rs.next()) {
            players.add(serializeDBPlayer(rs));

        }
        return players;
    }

    public DBPlayer getPlayer(String uid) throws SQLException {
        ResultSet rs = query("SELECT * from player where uid=?", uid);
        if (!rs.next()) {
            throw new Error("No player found");
        }
        return serializeDBPlayer(rs);
    }

    public List<DBPlayer> getPlayersInNation(String nation) throws SQLException {
        ResultSet rs = query("SELECT * from player where nation=?", nation);
        return serializeDBPlayers(rs);
    }

    public DBPlayer getPlayerByName(String name) throws SQLException {
        ResultSet rs = query("SELECT * from player where LOWER(player_name)=LOWER(?)", name);
        if (!rs.next()) {
            throw new Error("No player found");
        }
        return serializeDBPlayer(rs);
    }

}