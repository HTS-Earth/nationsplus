package com.ollethunberg.nationsplus;

import java.sql.Connection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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
                }

                // Check for "create" argument
                else if (args[0].equalsIgnoreCase("create") && executor.hasPermission("nationsplus.create")) {
                    // Create the nation
                    plugin.getLogger().info("Creating nation!");
                    // Execute the create nation command
                    CreateNationCommand createNationCommand = new CreateNationCommand(conn);
                    createNationCommand.execute(args[1], args[2], executor);
                    return true;
                } else if (args[0].equalsIgnoreCase("list")) {
                    // Execute the list nation command
                    ListNationCommand listNationCommand = new ListNationCommand(conn);
                    listNationCommand.execute(executor);
                    return true;
                } else if (args[0].equalsIgnoreCase("info")) {
                    // Execute the info nation command
                    InfoNationCommand infoNationCommand = new InfoNationCommand(conn);
                    infoNationCommand.execute(executor, args[1]);
                    return true;
                } else if (args[0].equalsIgnoreCase("join")) {
                    // Execute the join nation command
                    JoinNationCommand joinNationCommand = new JoinNationCommand(conn);
                    joinNationCommand.execute(executor, args[1]);
                    return true;
                } else if (args[0].equalsIgnoreCase("status")) {
                    NationRelationshipCommands nationRelationshipCommands = new NationRelationshipCommands(conn);
                    nationRelationshipCommands.executeStatus(executor);
                    return true;
                } else if (NationRelationshipCommands.isStatusValid(args[0])) {
                    // Execute relationship command
                    NationRelationshipCommands nationRelationshipCommands = new NationRelationshipCommands(conn);
                    nationRelationshipCommands.execute(executor, args[1], args[0]);
                    return true;
                }

            }
        }
        return true;
    }
}
