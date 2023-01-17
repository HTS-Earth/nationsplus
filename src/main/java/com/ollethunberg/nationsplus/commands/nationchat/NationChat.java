package com.ollethunberg.nationsplus.commands.nationchat;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.ollethunberg.nationsplus.lib.SQLHelper;
import com.ollethunberg.nationsplus.lib.exceptions.PlayerNotFoundException;
import com.ollethunberg.nationsplus.lib.helpers.PlayerHelper;
import com.ollethunberg.nationsplus.lib.models.db.DBPlayer;

public class NationChat extends SQLHelper {
    PlayerHelper playerHelper = new PlayerHelper();

    public void newChat(Player player, String... messageArguments) throws SQLException, PlayerNotFoundException {
        String message = String.join(" ", messageArguments);
        DBPlayer dbPlayer = playerHelper.getPlayer(player.getUniqueId().toString());
        if (dbPlayer.nation == null) {
            player.sendMessage("§r[§4§lNATION-ERROR§r]§c You are not in a nation.");
            return;
        }
        List<DBPlayer> playersInNation = playerHelper.getPlayersInNation(dbPlayer.nation);
        String nationChat = "§r[§b§lNATION§r]§b " + player.getDisplayName() + "§r: " + message;
        for (DBPlayer dbPlayerInNation : playersInNation) {
            Player playerInNation = Bukkit.getPlayer(UUID.fromString(dbPlayerInNation.uid));
            if (playerInNation != null) {
                playerInNation.sendMessage(nationChat);
            }
        }
    }
}
