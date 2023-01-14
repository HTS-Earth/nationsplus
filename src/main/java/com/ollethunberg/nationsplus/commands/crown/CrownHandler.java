package com.ollethunberg.nationsplus.commands.crown;

import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ollethunberg.nationsplus.NationsPlus;
import com.ollethunberg.nationsplus.lib.exceptions.ExceptionBase;

public class CrownHandler implements CommandExecutor {
    Crown crown = new Crown();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            /* Player sent the command */
            String cmd = command.getName().toLowerCase();
            Player player = (Player) sender;
            if (args.length == 0) {
                player.sendMessage("§r[§4§lERROR§r]§c You must specify an action!");
                return true;
            }
            String action = args[0].toLowerCase();

            try {

                if (cmd.equals("crown")) {
                    switch (action) {
                        case "pass": {
                            if (args.length < 2) {
                                throw new IllegalArgumentException("You must specify a player to pass the crown to!");
                            }
                            crown.pass(player, args[1]);
                            break;
                        }

                    }
                }

            } catch (SQLException e) {
                player.sendMessage(
                        "§r[§4§lDATABASE-ERROR§r]§c A database error occured. Please contact an administrator.");
                NationsPlus.LOGGER.warning(action + " " + e.getMessage());
                return true;
            } catch (ExceptionBase e) {
                NationsPlus.LOGGER.warning(action + " " + e.getMessage());
                return true;
            }
        } else {
            /* Console sent the command */
            sender.sendMessage("You must be a player to use this command!");
        }
        return true;
    }

}
