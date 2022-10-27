package com.ollethunberg.nationsplus.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

public class HelpCommand {
    public static final Map<String, String> commands = new HashMap<String, String>() {
        {
            put("/n create <name> <prefix>", "Creates a new nation");
            put("/n tax <percentage>", "Sets the tax of the nation");
            put("/n war <nation name>", "Declares wars on another nation");
            put("/n neutral <nation name>", "Declares neutrality to another nation");
            put("/n peace <nation name>", "Declares peace to another nation");
            put("/n ally <nation name>", "Declares ally to another nation");
            put("/n enemy <nation name>", "Declares enemeies to another nation");
            put("/n list", "List the nation stats");
            put("/n info <nation name>", "Displays the info of a nation");
            put("/n join <nation name>", "Join a nation! (You can't leave)");
        }
    };

    public void execute(CommandSender sender, String[] args) {
        try {
            if (args.length == 0) {
                for (String command : commands.keySet()) {
                    sender.sendMessage(command);
                }
            } else if (args.length == 1) {
                for (String key : commands.keySet()) {
                    if (args[0].split("\\s+")[0].contains(key)) { // Only get the keyword and see if it matches.
                        sender.sendMessage(commands.get(args[0]));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
