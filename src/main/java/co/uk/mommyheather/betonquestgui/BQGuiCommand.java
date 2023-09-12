package co.uk.mommyheather.betonquestgui;

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
                sender.sendMessage("You must send one argument - true or false!");
                return false;
            }

            switch (args[0]) {
                case "true" : {
                    BetonQuestGui.INSTANCE.players.put(sender.getName(), true);
                    return true;
                }
                case "false" : {
                    BetonQuestGui.INSTANCE.players.put(sender.getName(), false);
                    return true;
                }
                case "login" : {
                    BetonQuestGui.INSTANCE.players.putIfAbsent(sender.getName(), true); //login will be ran by the mod itself
                    return true;
                }
                default : return false;

            }
        }
        return false;
    }
}