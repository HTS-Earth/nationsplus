package com.ollethunberg.nationsplus.lib.exceptions;

import org.bukkit.entity.Player;

public class IllegalArgumentException extends ExceptionBase {
    public IllegalArgumentException(Player player, String message) {
        super(player, "input-error", message);
    }
}
