package com.ollethunberg.nationsplus.commands.nation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

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
                return Arrays.asList(keywords);
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
                    case "create":
                        if (args.length == 2) {
                            return new ArrayList<String>() {
                                {
                                    add("<name>");
                                }
                            };
                        } else if (args.length == 3) {
                            return new ArrayList<String>() {
                                {
                                    add("<prefix>");
                                }
                            };
                        }
                        break;
                    case "donate":
                        if (args.length == 2) {
                            return new ArrayList<String>() {
                                {
                                    add("<amount>");
                                }
                            };
                        }
                        break;
                    case "withdraw":
                        if (args.length == 2) {
                            return new ArrayList<String>() {
                                {
                                    add("<amount>");
                                }
                            };
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
