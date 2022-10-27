package com.ollethunberg.nationsplus;

import java.sql.Connection;

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
import com.ollethunberg.nationsplus.commands.NationRelationshipCommands;
import com.ollethunberg.nationsplus.commands.ReinforceCommand;
import com.ollethunberg.nationsplus.commands.TaxCommand;

public class CommandHandler implements CommandExecutor {

    // This method is called, when somebody uses our command
    Connection conn;
    Plugin plugin = NationsPlus.getPlugin(NationsPlus.class);

    public CommandHandler(Connection _connection) {
        conn = _connection;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if it is the help commmand, if it is, send the help message
        if (command.getName().equalsIgnoreCase("nationsplus")) {
            HelpCommand helpCommand = new HelpCommand();
            helpCommand.execute(sender, args);
            return true;
        }
        if (sender instanceof Player) {
            String cmd = command.getName();
            Player executor = (Player) sender;
            plugin.getLogger().info(cmd);
            // Player only commands
            if (cmd.equalsIgnoreCase("nation")) {
                // Check what arguments there are
                // if there are any arguments
                if (args.length == 0) {
                    // Send help message using the HelpCommand class
                    HelpCommand help = new HelpCommand();
                    help.execute(executor, args);
                    return true;
                }

                String subCommand = args[0];
                switch (subCommand) {
                    // Check for "create" argument
                    case "create": {
                        if (!executor.hasPermission("nationsplus.create"))
                            return true;
                        // Create the nation
                        plugin.getLogger().info("Creating nation!");
                        // Execute the create nation command
                        CreateNationCommand createNationCommand = new CreateNationCommand(conn);
                        createNationCommand.execute(args[1], args[2], executor);
                        return true;
                    }
                    case "list": {
                        // Execute the list nation command
                        ListNationCommand listNationCommand = new ListNationCommand(conn);
                        listNationCommand.execute(executor);
                        return true;
                    }
                    case "info": {
                        // Execute the info nation command
                        InfoNationCommand infoNationCommand = new InfoNationCommand(conn);
                        infoNationCommand.execute(executor, args[1]);
                        return true;
                    }
                    case "join": {
                        // Execute the join nation command
                        JoinNationCommand joinNationCommand = new JoinNationCommand();
                        joinNationCommand.execute(executor, args[1]);
                        return true;
                    }
                    case "status": {
                        NationRelationshipCommands nationRelationshipCommands = new NationRelationshipCommands();
                        nationRelationshipCommands.executeStatus(executor);
                        return true;
                    }
                    case "tax": {
                        // Execute the tax nation command
                        TaxCommand taxCommand = new TaxCommand();
                        taxCommand.execute(executor, args[1]);
                        return true;
                    }
                }
                if (NationRelationshipCommands.isStatusValid(subCommand)) {
                    // Execute relationship command
                    NationRelationshipCommands nationRelationshipCommands = new NationRelationshipCommands();
                    nationRelationshipCommands.execute(executor, args[1], args[0]);
                    return true;
                }

            } else if (cmd.equalsIgnoreCase("crown")) {
                if (args[0].equalsIgnoreCase("claim")) {
                    CrownClaimCommand crownClaimCommand = new CrownClaimCommand();
                    crownClaimCommand.execute(executor);
                    return true;
                }
            } else if (cmd.equalsIgnoreCase("reinforce")) {
                String reinforceTarget = args.length > 0 ? args[0] : null;

                ReinforceCommand reinforceCommand = new ReinforceCommand();
                reinforceCommand.execute(executor, reinforceTarget);
                return true;
            }
        }
        return true;
    }
}
