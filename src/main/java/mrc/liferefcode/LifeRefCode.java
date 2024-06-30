package mrc.liferefcode;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class LifeRefCode extends JavaPlugin implements Listener {

    private HashMap<UUID, String> playerIPs = new HashMap<>(); // хранит айпи игроков
    private HashSet<String> usedIPs = new HashSet<>(); // хранит использованные IP

    @Override
    public void onEnable() {
        this.getLogger().warning("Сделано для LifeTime");
        this.getLogger().warning("Разработчик: mrcDEV");
        // Регистрация событий
        getServer().getPluginManager().registerEvents(this, this);
        // Загрузка конфигурации
        saveDefaultConfig();

        // Регистрация команды из конфигурационного файла
        registerCommandFromConfig();
    }

    @Override
    public void onDisable() {
        // Сохранение данных перед отключением
        saveConfig();
    }

    private void registerCommandFromConfig() {
        FileConfiguration config = getConfig();
        String commandName = config.getString("command.name", "usecode");
        String commandDescription = config.getString("command.description", "Use a referral code");
        String commandUsage = config.getString("command.usage", "/usecode <code>");
        String commandPermission = config.getString("command.permission", "referralsystem.usecode");

        BukkitCommand command = new BukkitCommand(commandName) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (args.length == 1) {
                        String code = args[0];
                        useReferralCode(player, code);
                        return true;
                    } else {
                        player.sendMessage(config.getString("messages.usage", commandUsage));
                        return false;
                    }
                }
                return false;
            }
        };

        command.setDescription(commandDescription);
        command.setUsage(commandUsage);
        command.setPermission(commandPermission);

        getCommandMap().register(getDescription().getName(), command);
    }

    private org.bukkit.command.CommandMap getCommandMap() {
        try {
            java.lang.reflect.Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            return (org.bukkit.command.CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean useReferralCode(Player player, String code) {
        FileConfiguration config = getConfig();
        String playerIP = player.getAddress().getAddress().getHostAddress();

        if (config.contains("refcodes." + code)) {
            if (usedIPs.contains(playerIP)) {
                player.sendMessage(config.getString("messages.ipAlreadyUsed", "Этот IP уже использовал реферальный код."));
                return false;
            }

            usedIPs.add(playerIP);
            playerIPs.put(player.getUniqueId(), playerIP);

            String command = config.getString("refcodes." + code);
            if (command != null) {
                command = command.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                player.sendMessage(config.getString("messages.codeUsed", "Вы использовали реферальный код: %code%").replace("%code%", code));
                return true;
            } else {
                player.sendMessage(config.getString("messages.commandNotFound", "Ошибка: команда для этого реферального кода не найдена."));
                return false;
            }
        } else {
            player.sendMessage(config.getString("messages.invalidCode", "Реферальный код недействителен."));
            return false;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerIP = player.getAddress().getAddress().getHostAddress();
        playerIPs.put(player.getUniqueId(), playerIP);
    }
}
