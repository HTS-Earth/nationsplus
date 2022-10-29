package com.ollethunberg.nationsplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.ollethunberg.nationsplus.commands.CreateNationCommand;
import com.ollethunberg.nationsplus.commands.CrownClaimCommand;
import com.ollethunberg.nationsplus.commands.HelpCommand;
import com.ollethunberg.nationsplus.commands.InfoNationCommand;
import com.ollethunberg.nationsplus.commands.JoinNationCommand;
import com.ollethunberg.nationsplus.commands.ListNationCommand;
import com.ollethunberg.nationsplus.commands.NationMoneyCommands;
import com.ollethunberg.nationsplus.commands.NationRelationshipCommands;
import com.ollethunberg.nationsplus.commands.ReinforceCommand;
import com.ollethunberg.nationsplus.commands.TaxCommand;

public class CommandHandler implements CommandExecutor {

    // This method is called, when somebody uses our command
    Plugin plugin = NationsPlus.getPlugin(NationsPlus.class);
    CreateNationCommand createNationCommand = new CreateNationCommand();
    ListNationCommand listNationCommand = new ListNationCommand();
    InfoNationCommand infoNationCommand = new InfoNationCommand();
    JoinNationCommand joinNationCommand = new JoinNationCommand();
    NationRelationshipCommands nationRelationshipCommands = new NationRelationshipCommands();
    HelpCommand helpCommand = new HelpCommand();
    TaxCommand taxCommand = new TaxCommand();
    CrownClaimCommand crownClaimCommand = new CrownClaimCommand();
    ReinforceCommand reinforceCommand = new ReinforceCommand();
    NationMoneyCommands nationMoneyCommands = new NationMoneyCommands();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if it is the help commmand, if it is, send the help message
        if (command.getName().equalsIgnoreCase("nationsplus")) {
            helpCommand.execute(sender, args);
            return true;
        }
        if (sender instanceof Player) {
            String cmd = command.getName();
            Player executor = (Player) sender;
            try {

                // Player only commands
                if (cmd.equalsIgnoreCase("nation")) {
                    // Check what arguments there are
                    // if there are any arguments
                    if (args.length == 0) {
                        helpCommand.execute(executor, args);
                        return true;
                    }

                    String subCommand = args[0];
                    switch (subCommand) {
                        // Check for "create" argument
                        case "create": {
                            if (!executor.hasPermission("nationsplus.create"))
                                return true;
                            createNationCommand.execute(args[1], args[2], executor);
                            return true;
                        }
                        case "list": {
                            listNationCommand.execute(executor);
                            return true;
                        }
                        case "info": {
                            infoNationCommand.execute(executor, args[1]);
                            return true;
                        }
                        case "join": {
                            joinNationCommand.execute(executor, args[1]);
                            return true;
                        }
                        case "status": {
                            nationRelationshipCommands.executeStatus(executor);
                            return true;
                        }
                        case "tax": {
                            taxCommand.execute(executor, args[1]);
                            return true;
                        }
                        case "donate": {
                            nationMoneyCommands.donate(executor, Integer.parseInt(args[1]));
                            return true;
                        }
                        case "withdraw": {
                            nationMoneyCommands.withdraw(executor, Integer.parseInt(args[1]), args[2]);
                            return true;
                        }
                    }
                    if (NationRelationshipCommands.isStatusValid(subCommand)) {
                        nationRelationshipCommands.execute(executor, args[1], args[0]);
                        return true;
                    }

                } else if (cmd.equalsIgnoreCase("crown")) {
                    if (args[0].equalsIgnoreCase("claim")) {
                        crownClaimCommand.execute(executor);
                        return true;
                    }
                } else if (cmd.equalsIgnoreCase("reinforce")) {
                    String reinforceTarget = args.length > 0 ? args[0] : null;
                    reinforceCommand.execute(executor, reinforceTarget);
                    return true;
                }
            } catch (Exception e) {
                executor.sendMessage("§r[§4§lERROR§r]§c " + e.getMessage());
                e.printStackTrace();
            }
        }
        return true;
    }
}
