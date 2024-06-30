package mrc.liferefcode.admin;

import mrc.liferefcode.LifeRefCode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminCommands implements CommandExecutor {

    private final LifeRefCode plugin;

    public AdminCommands(LifeRefCode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("referralsystem.admin")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                plugin.referralCount.forEach((referrer, count) -> sender.sendMessage(referrer + ": " + count + " рефералов"));
                return true;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("check")) {
                String playerName = args[1];
                int count = plugin.referralCount.getOrDefault(playerName, 0);
                sender.sendMessage(playerName + " пригласил " + count + " игроков.");
                return true;
            }
        }
        return false;
    }
}
