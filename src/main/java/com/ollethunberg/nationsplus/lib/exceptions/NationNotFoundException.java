package com.ollethunberg.nationsplus.lib.exceptions;

import org.bukkit.entity.Player;

public class NationNotFoundException extends ExceptionBase {
    public NationNotFoundException(Player player, String nationName) {
        super(player, "nation-error", "Nation \"" + nationName + "\" not found");
    }
}
