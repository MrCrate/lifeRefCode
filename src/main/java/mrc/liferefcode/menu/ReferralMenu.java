package mrc.liferefcode.menu;

import mrc.liferefcode.LifeRefCode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class ReferralMenu implements Listener {

    private final LifeRefCode plugin;
    private final FileConfiguration menuConfig;

    public ReferralMenu(LifeRefCode plugin) {
        this.plugin = plugin;
        this.menuConfig = plugin.getMenuConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openMenu(Player player) {
        String title = ChatColor.translateAlternateColorCodes('&', menuConfig.getString("menu.title"));
        int size = menuConfig.getInt("menu.size");
        Inventory menu = Bukkit.createInventory(null, size, title);

        // Книга с объяснением
        setMenuItem(menu, menuConfig.getConfigurationSection("menu.info"), null, null);

        // Головы с уровнями реферальных кодов
        setMenuItem(menu, menuConfig.getConfigurationSection("menu.levels.level1"), "1", null);
        setMenuItem(menu, menuConfig.getConfigurationSection("menu.levels.level2"), "2", null);
        setMenuItem(menu, menuConfig.getConfigurationSection("menu.levels.level3"), "3", null);

        // Голова игрока со статистикой
        setMenuItem(menu, menuConfig.getConfigurationSection("menu.playerStats"), null, player);

        player.openInventory(menu);
    }

    private void setMenuItem(Inventory menu, ConfigurationSection section, String level, Player player) {
        if (section == null) return;

        int slot = section.getInt("slot");
        String materialName = section.getString("material");
        String displayName = ChatColor.translateAlternateColorCodes('&', section.getString("name"));
        List<String> lore = section.getStringList("lore");

        ItemStack item = new ItemStack(Material.valueOf(materialName));
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(displayName);
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
        }

        if (player != null && meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwningPlayer(player);

            int referrals = plugin.getReferralCount(player.getName());
            int playerLevel = plugin.getReferralLevel(player.getName());
            int rewards = plugin.getTotalRewards(player.getName());

            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, lore.get(i)
                        .replace("%referrals%", String.valueOf(referrals))
                        .replace("%level%", String.valueOf(playerLevel))
                        .replace("%rewards%", String.valueOf(rewards)));
            }
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        menu.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', menuConfig.getString("menu.title")))) {
            event.setCancelled(true);
        }
    }
}
