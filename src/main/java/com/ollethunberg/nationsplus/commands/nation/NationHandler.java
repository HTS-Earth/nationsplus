package com.ollethunberg.nationsplus.commands.nation;

import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ollethunberg.nationsplus.NationsPlus;
import com.ollethunberg.nationsplus.commands.nation.commands.nationrelationship.NationRelationship;
import com.ollethunberg.nationsplus.lib.exceptions.ExceptionBase;
import com.ollethunberg.nationsplus.lib.exceptions.IllegalArgumentException;
import com.ollethunberg.nationsplus.lib.exceptions.PermissionException;

public class NationHandler implements CommandExecutor {

    Nation nation;
    NationRelationship nationRelationship;

    public NationHandler() {

        nation = new Nation();
        nationRelationship = new NationRelationship();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            /* Player sent the command */
            String cmd = command.getName().toLowerCase();
            Player player = (Player) sender;

            String action = args.length >= 1 ? args[0].toLowerCase() : "";
            try {

                if (cmd.equals("nation") && !action.equals("")) {
                    switch (action) {
                        case "create": {
                            if (!player.hasPermission("nationsplus.create")) {
                                throw new PermissionException(player, "You do not have permission to create a nation!");
                            }
                            if (args.length < 3)
                                throw new IllegalArgumentException(player, "You must specify a name and a tag!");
                            nation.create(args[1], args[2], player);
                            break;
                        }
                        case "list": {
                            nation.list(player);
                            break;
                        }
                        case "info": {
                            if (args.length < 2)
                                throw new IllegalArgumentException(player, "You must specify a nation!");
                            nation.info(player, args[1]);
                            break;
                        }
                        case "join": {
                            if (args.length < 2)
                                throw new IllegalArgumentException(player, "You must specify a nation!");
                            nation.join(player, args[1]);
                            break;
                        }
                        case "status": {
                            nation.getStatus(player);
                            return true;
                        }
                        case "tax": {
                            if (args.length < 3)
                                throw new IllegalArgumentException(player, "You must specify a tax type and amount!");
                            nation.tax(player, args[1], args[2]);
                            break;
                        }
                        case "donate": {
                            if (args.length < 2)
                                throw new IllegalArgumentException(player, "You must specify an amount!");
                            nation.donate(player, Integer.parseInt(args[1]));
                            break;
                        }
                        case "withdraw": {
                            if (args.length < 3)
                                throw new IllegalArgumentException(player, "You must specify an amount and a player!");
                            nation.withdraw(player, Integer.parseInt(args[1]), args[2]);
                            break;
                        }
                    }
                    if (NationRelationship.isStatusValid(action)) {
                        nationRelationship.status(player, args[1], args[0]);
                        return true;
                    }
                } else
                    nation.help(player);
            } catch (SQLException e) {
                player.sendMessage(
                        "§r[§4§lDATABASE-ERROR§r]§c A database error occured. Please contact an administrator.");
                NationsPlus.LOGGER.warning(action + " " + e.getMessage());
                return true;
            } catch (ExceptionBase e) {
                return true;
            }

        } else {
            /* Console sent the command */
            sender.sendMessage("You must be a player to use this command!");
        }
        return true;
    }
}
