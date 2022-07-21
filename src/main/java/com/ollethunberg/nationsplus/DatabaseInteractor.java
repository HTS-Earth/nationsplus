package com.ollethunberg.nationsplus;

import java.sql.Connection;

import org.bukkit.plugin.Plugin;

public class DatabaseInteractor {
    public SQLHelper sqlHelper;

    public Plugin plugin = NationsPlus.getPlugin(NationsPlus.class);

    public DatabaseInteractor(Connection connection) {
        sqlHelper = new SQLHelper(connection);
    }
}
