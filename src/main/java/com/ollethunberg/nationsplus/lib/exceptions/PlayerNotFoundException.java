package com.ollethunberg.nationsplus.lib.exceptions;

import org.bukkit.entity.Player;

public class PlayerNotFoundException extends ExceptionBase {
    public PlayerNotFoundException(Player player, String playerName) {
        super(player, "input-error", "Player \"" + playerName + "\" not found");
    }
}
