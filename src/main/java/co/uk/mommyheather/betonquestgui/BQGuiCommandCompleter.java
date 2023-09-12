package co.uk.mommyheather.betonquestgui;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class BQGuiCommandCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        if (arg3.length <= 1) {
            return Arrays.asList(new String[]{"true", "false"});
        }
        return null;
    }
    
}
