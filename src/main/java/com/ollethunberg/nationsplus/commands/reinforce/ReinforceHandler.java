package com.ollethunberg.nationsplus.commands.reinforce;

import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ollethunberg.nationsplus.NationsPlus;

public class ReinforceHandler implements CommandExecutor {

    Reinforce reinforce = new Reinforce();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            /* Player sent the command */
            Player player = (Player) sender;
            String action = args[0].toUpperCase();
            try {
                reinforce.reinforce(player, action);
            } catch (SQLException e) {
                player.sendMessage("§cThere was an error while executing the command!");
                e.printStackTrace();
                NationsPlus.LOGGER.warning(action + " " + e.getMessage());
                return true;
            } catch (Error | Exception e) {
                player.sendMessage("§r[§4§lERROR§r]§c " + e.getMessage());
                return true;
            }
        } else {
            /* Console sent the command */
            sender.sendMessage("You must be a player to use this command!");
        }
        return true;
    }
}
