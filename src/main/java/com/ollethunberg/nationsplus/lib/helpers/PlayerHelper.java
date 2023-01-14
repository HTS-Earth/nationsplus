package com.ollethunberg.nationsplus.lib.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.ollethunberg.nationsplus.NationsPlus;
import com.ollethunberg.nationsplus.lib.SQLHelper;
import com.ollethunberg.nationsplus.lib.exceptions.PlayerNotFoundException;
import com.ollethunberg.nationsplus.lib.models.db.DBPlayer;

public class PlayerHelper extends SQLHelper {
    public DBPlayer serializeDBPlayer(ResultSet rs) throws SQLException {
        DBPlayer player = new DBPlayer();
        NationsPlus.LOGGER.info("serializeDBPlayer");
        System.out.println(rs);
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

    public DBPlayer getPlayer(String uid) throws SQLException, PlayerNotFoundException {
        ResultSet rs = query("SELECT * from player where uid=?", uid);
        if (!rs.next()) {
            throw new PlayerNotFoundException(Bukkit.getPlayer(UUID.fromString(uid)), uid);
        }
        DBPlayer player = serializeDBPlayer(rs);
        rs.close();
        return player;
    }

    public List<DBPlayer> getPlayersInNation(String nation) throws SQLException {
        ResultSet rs = query("SELECT * from player where nation=?", nation);
        List<DBPlayer> players = serializeDBPlayers(rs);
        rs.close();
        return players;
    }

    public DBPlayer getPlayerByName(String name) throws SQLException {
        ResultSet rs = query("SELECT * from player where LOWER(player_name)=LOWER(?)", name);
        if (!rs.next()) {
            throw new java.lang.IllegalArgumentException("Player not found");
        }
        DBPlayer player = serializeDBPlayer(rs);
        rs.close();
        return player;
    }

}