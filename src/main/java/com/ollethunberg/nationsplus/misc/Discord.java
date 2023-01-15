package com.ollethunberg.nationsplus.misc;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Discord {
    public static String getInviteLink() {
        return "https://discord.gg/DND5dGK";
    }

    public static BaseComponent[] getInviteLinkComponent() {
        return new ComponentBuilder("§2§l>> §aClick here to join the discord server")
                .color(ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        getInviteLink()))
                .create();
    }
}
