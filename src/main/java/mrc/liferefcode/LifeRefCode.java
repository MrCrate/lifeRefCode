package mrc.liferefcode;

import mrc.liferefcode.admin.AdminCommands;
import mrc.liferefcode.commands.ReferralCommand;
import mrc.liferefcode.commands.ReferralMenuCommand;
import mrc.liferefcode.menu.ReferralMenu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class LifeRefCode extends JavaPlugin {

    HashMap<UUID, String> playerIPs = new HashMap<>();
    public HashMap<String, Integer> referralCount = new HashMap<>();
    HashMap<UUID, Long> playerPlayTime = new HashMap<>();
    HashSet<String> usedIPs = new HashSet<>();
    HashMap<String, UUID> referralCodes = new HashMap<>();
    private FileConfiguration menuConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveMenuConfig();

        getServer().getPluginManager().registerEvents(new PlayTimeTracker(this), this);
        getServer().getPluginManager().registerEvents(new ReferralMenu(this), this);

        getCommand("ref").setExecutor(new ReferralCommand(this));
        getCommand("refadmin").setExecutor(new AdminCommands(this));
        getCommand("refmenu").setExecutor(new ReferralMenuCommand(this));
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    public FileConfiguration getMenuConfig() {
        return menuConfig;
    }

    public void saveMenuConfig() {
        File menuFile = new File(getDataFolder(), "menu.yml");
        if (!menuFile.exists()) {
            saveResource("menu.yml", false);
        }
        menuConfig = YamlConfiguration.loadConfiguration(menuFile);
    }

    public boolean useReferralCode(Player player, String referrerName) {
        FileConfiguration config = getConfig();
        UUID playerUUID = player.getUniqueId();
        String playerIP = player.getAddress().getAddress().getHostAddress();

        if (playerPlayTime.getOrDefault(playerUUID, 0L) < 7200) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.insufficientPlayTime")));
            return false;
        }

        if (usedIPs.contains(playerIP)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.ipAlreadyUsed")));
            return false;
        }

        if (referralCodes.containsKey(referrerName)) {
            UUID referrerUUID = referralCodes.get(referrerName);
            Player referrer = getServer().getPlayer(referrerUUID);

            if (referrer != null && !referrerUUID.equals(playerUUID)) {
                usedIPs.add(playerIP);
                referralCount.put(referrerName, referralCount.getOrDefault(referrerName, 0) + 1);
                int referrals = referralCount.get(referrerName);

                int reward = getRewardForReferrals(referrals);
                giveReward(player, reward);
                giveReward(referrer, reward);

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.codeUsed")));
                referrer.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.referrerReward")));

                logReferral(player, referrerName);
                return true;
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.invalidReferrer")));
                return false;
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.invalidCode")));
            return false;
        }
    }

    public int getReferralCount(String playerName) {
        return referralCount.getOrDefault(playerName, 0);
    }

    public int getReferralLevel(String playerName) {
        int count = getReferralCount(playerName);
        if (count >= 30) return 3;
        if (count >= 10) return 2;
        return 1;
    }

    public int getTotalRewards(String playerName) {
        int count = getReferralCount(playerName);
        if (count >= 30) return count * 650;
        if (count >= 10) return count * 400;
        return count * 200;
    }

    private int getRewardForReferrals(int referrals) {
        if (referrals >= 30) return 650;
        if (referrals >= 10) return 400;
        return 200;
    }

    private void giveReward(Player player, int reward) {
        player.sendMessage("Вы получили " + reward + " рубинов!");
    }

    private void logReferral(Player player, String referrerName) {
        getLogger().info("Игрок " + player.getName() + " 1323123 " + referrerName);
    }
}
