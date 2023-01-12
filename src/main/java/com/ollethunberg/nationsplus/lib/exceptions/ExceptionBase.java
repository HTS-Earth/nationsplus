package com.ollethunberg.nationsplus.lib.exceptions;

import org.bukkit.entity.Player;

public abstract class ExceptionBase extends Exception {
    public ExceptionBase(Player player, String errorName, String errorMessage) {
        super(errorMessage);
        player.sendMessage(
                "§r[§4§l" + errorName.toUpperCase()
                        + "§r]§c " + errorMessage);
    }
}
