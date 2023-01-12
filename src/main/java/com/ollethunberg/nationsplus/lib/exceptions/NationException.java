package com.ollethunberg.nationsplus.lib.exceptions;

import org.bukkit.entity.Player;

public class NationException extends ExceptionBase {
    public NationException(Player player, String message) {
        super(player, "nation-error", message);
    }
}
