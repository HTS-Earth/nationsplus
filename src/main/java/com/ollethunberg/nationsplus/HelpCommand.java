package com.ollethunberg.nationsplus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

public class HelpCommand {
    public static final Map<String, String> commands = new HashMap<String, String>() {
        {
            put("/nation create <name> <prefix>", "Creates a new nation");
            put("/nation tax <percentage>", "Sets the tax of the nation");
            put("/nation war <nation name>", "Declares wars on another nation");
            put("/nation neutral <nation name>", "Declares neutrality to another nation");
            put("/nation peace <nation name>", "Declares peace to another nation");
            put("/nation list", "List the nation stats");
            put("/nation info <nation name>", "Displays the info of a nation");
            put("/nation join <nation name>", "Join a nation! (You can't leave)");
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
