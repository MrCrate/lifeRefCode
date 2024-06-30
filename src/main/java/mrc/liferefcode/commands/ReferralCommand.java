package mrc.liferefcode.commands;

import mrc.liferefcode.LifeRefCode;
import mrc.liferefcode.menu.ReferralMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReferralCommand implements CommandExecutor {

    private final LifeRefCode plugin;
    private final ReferralMenu referralMenu;

    public ReferralCommand(LifeRefCode plugin) {
        this.plugin = plugin;
        this.referralMenu = new ReferralMenu(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            referralMenu.openMenu(player);
            return true;
        }

        if (args.length == 1) {
            String referrerName = args[0];
            if (plugin.useReferralCode(player, referrerName)) {
                return true;
            } else {
                player.sendMessage(plugin.getConfig().getString("messages.invalidCode"));
                return false;
            }
        }

        player.sendMessage(plugin.getConfig().getString("command.usage"));
        return false;
    }
}
