package mrc.liferefcode.commands;

import mrc.liferefcode.LifeRefCode;
import mrc.liferefcode.menu.ReferralMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReferralMenuCommand implements CommandExecutor {

    private final LifeRefCode plugin;

    public ReferralMenuCommand(LifeRefCode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ReferralMenu menu = new ReferralMenu(plugin);
            menu.openMenu(player);
            return true;
        } else {
            sender.sendMessage("12312321321");
            return true;
        }
    }
}
