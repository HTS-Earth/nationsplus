package com.ollethunberg.nationsplus;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    Connection conn;
    private Plugin plugin = NationsPlus.getPlugin(NationsPlus.class);

    public DatabaseManager(Connection _connection) {
        conn = _connection;
    }

    public void createDatabase() throws FileNotFoundException, SQLException {
        // Check if the database exists.
        String checkIfExistsSQL = "SELECT EXISTS ( SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename  = 'nation' );";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(checkIfExistsSQL);
        rs.next();
        Boolean doesStructureExist = rs.getBoolean("exists");
        if (!doesStructureExist) {
            ScriptRunner sr = new ScriptRunner(conn);

            plugin.getLogger().info(plugin.getResource("db.sql").toString());

            Reader reader = new BufferedReader(new InputStreamReader(plugin.getResource("db.sql")));
            sr.runScript(reader);
            plugin.getLogger().info("Created new database structure");
        } else {
            plugin.getLogger().info(("Database structure already in place. No need to import new SQL data. "));
        }
    }

}
