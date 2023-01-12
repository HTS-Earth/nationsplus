package com.ollethunberg.nationsplus.lib.exceptions;

import org.bukkit.entity.Player;

public class PermissionException extends ExceptionBase {
    public PermissionException(Player player, String errorMessage) {
        super(player, "permission-error", errorMessage);
    }
}