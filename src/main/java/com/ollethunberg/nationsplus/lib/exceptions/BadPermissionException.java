package com.ollethunberg.nationsplus.lib.exceptions;

import org.bukkit.entity.Player;

public class BadPermissionException extends ExceptionBase {
    public BadPermissionException(Player player, String message) {
        super(player, "permission-error", message);
    }
}
