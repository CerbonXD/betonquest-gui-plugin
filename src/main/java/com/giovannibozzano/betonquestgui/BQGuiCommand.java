package com.giovannibozzano.betonquestgui;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BQGuiCommand implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length != 1) {
                sender.sendMessage("You must send one argument - register or unregister!");
                return false;
            }

            switch (args[0]) {
                case "register" : {
                    BetonQuestGui.INSTANCE.players.add(sender.getName());
                    return true;
                }
                case "unregister" : {
                    BetonQuestGui.INSTANCE.players.remove(sender.getName());
                    return true;
                }
                default : return false;

            }
        }
        return false;
    }
}