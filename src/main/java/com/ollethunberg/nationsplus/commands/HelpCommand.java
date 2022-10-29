package com.ollethunberg.nationsplus.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

public class HelpCommand {
    public static final Map<String, String> commands = new HashMap<String, String>() {
        {
            put("/n create <name> <prefix>", "§aCreates a new nation");
            put("/n tax <percentage>", "§aSets the tax of the nation");
            put("/n war <nation name>", "§aDeclares wars on another nation");
            put("/n neutral <nation name>", "§aDeclares neutrality to another nation");
            put("/n peace <nation name>", "§aDeclares peace to another nation");
            put("/n ally <nation name>", "§aDeclares ally to another nation");
            put("/n enemy <nation name>", "§aDeclares enemeies to another nation");
            put("/n list", "§aList the nation stats");
            put("/n info <nation name>", "§aDisplays the info of a nation");
            put("/n join <nation name>", "§aJoin a nation! (You can't leave)");
            put("/n donate <amount>", "§aDonates the set amount to your nation's bank");
            put("/n withdraw <amount> <receiver player name>",
                    "§aWithdraw the amount from your nation's bank to the receiver");
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
