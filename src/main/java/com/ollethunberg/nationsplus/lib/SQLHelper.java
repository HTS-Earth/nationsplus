package com.ollethunberg.nationsplus.lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

import com.ollethunberg.nationsplus.NationsPlus;

public class SQLHelper {
    public static Connection conn;
    public Plugin plugin = NationsPlus.getPlugin(NationsPlus.class);

    public static Connection getConnection() {
        return conn;
    }

    public static void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet query(String query, Object... args) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        for (int i = 0; i < args.length; i++) {
            preparedStatement.setObject(i + 1, args[i]);
        }
        preparedStatement.executeQuery();
        return preparedStatement.getResultSet();
    }

    public static void update(String query, Object... args) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        for (int i = 0; i < args.length; i++) {
            preparedStatement.setObject(i + 1, args[i]);
        }
        preparedStatement.executeUpdate();
    }

}
