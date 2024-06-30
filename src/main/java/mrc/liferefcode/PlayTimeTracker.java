package mrc.liferefcode;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayTimeTracker implements Listener {

    private final LifeRefCode plugin;

    public PlayTimeTracker(LifeRefCode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        plugin.playerPlayTime.putIfAbsent(playerUUID, System.currentTimeMillis() / 1000L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        long playTime = System.currentTimeMillis() / 1000L - plugin.playerPlayTime.getOrDefault(playerUUID, 0L);
        plugin.playerPlayTime.put(playerUUID, playTime);
    }
}
