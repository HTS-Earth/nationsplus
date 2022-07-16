package com.ollethunberg.nationsplus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CreateNationCommand {
    Connection conn;
    Plugin plugin = NationsPlus.getPlugin(NationsPlus.class);

    public CreateNationCommand(Connection _connection) {
        conn = _connection;
    }

    public void execute(String nationName, String prefix, Player king) {
        try {
            String insertNewNationSQL = "INSERT INTO nation(name, prefix, king_id, created_date, kills, balance) VALUES (?, ?, ?, CURRENT_TIMESTAMP, 0,0);";
            PreparedStatement prepareInsertStatement = conn.prepareStatement(insertNewNationSQL);
            prepareInsertStatement.setString(1, nationName);
            prepareInsertStatement.setString(2, prefix);
            prepareInsertStatement.setString(3, king.getUniqueId().toString());
            prepareInsertStatement.executeUpdate();
            // Message the king that the nation was created

            king.sendMessage("§2Your nation was successfully created!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
