package com.ollethunberg.nationsplus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

public class NationAutoComplete implements TabCompleter {
    private static final String[] keywords = { "create", "tax", "war", "peace", "ally", "neutral", "enemy", "list",
            "join", "status", "info", "donate", "withdraw" };

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (args.length == 1) {

                // Check if command is nation
                final List<String> completions = new ArrayList<String>(Arrays.asList(keywords));
                final List<String> keyWordAsArrayList = new ArrayList<String>(Arrays.asList(keywords));

                StringUtil.copyPartialMatches(args[0], keyWordAsArrayList, completions);
                Collections.sort(completions);
                return completions;
            } else
                return null;
        } catch (Exception e) {
            return null;
        }
    }
}
