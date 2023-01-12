package com.ollethunberg.nationsplus.commands.nation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import com.ollethunberg.nationsplus.NationsPlus;
import com.ollethunberg.nationsplus.lib.helpers.NationHelper;

public class NationAutoComplete implements TabCompleter {
    NationHelper nationHelper;
    public static ArrayList<String> nations = new ArrayList<String>();

    private static final String[] keywords = { "create", "tax", "war", "peace", "ally", "neutral", "enemy", "list",
            "join", "status", "info", "donate", "withdraw" };

    public NationAutoComplete() {
        NationsPlus.LOGGER.warning("Nation auto complete ran");

        nationHelper = new NationHelper();
        try {
            nationHelper.getNations().forEach(nation -> {
                nations.add(nation.name);
            });
        } catch (SQLException e) {
            NationsPlus.LOGGER.warning("Failed to load nations for autocomplete");
        }

    }

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
            } else {
                switch (args[0]) {
                    case "tax":
                        if (args.length == 2) {
                            return new ArrayList<String>() {
                                {
                                    add("income");
                                    add("transfer");
                                    add("market");
                                    add("vat");
                                }
                            };
                        } else if (args.length == 3) {
                            return new ArrayList<String>() {
                                {
                                    add("10");
                                    add("20");
                                    add("30");
                                }
                            };
                        }
                        break;
                    case "info", "join", "enemy", "ally", "neutral", "peace", "war":
                        // get all nations
                        if (args.length == 2) {
                            return nations;
                        }
                        break;

                }
                return null;

            }
        } catch (Exception e) {
            return null;
        }
    }
}
