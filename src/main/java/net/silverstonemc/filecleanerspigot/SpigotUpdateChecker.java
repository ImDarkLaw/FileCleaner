package net.silverstonemc.filecleanerspigot;

import net.silverstonemc.filecleaner.VersionChecker;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SpigotUpdateChecker implements Listener {
    public SpigotUpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        String pluginName = plugin.getDescription().getName();

        if (event.getPlayer().hasPermission(pluginName.toLowerCase() + ".updatenotifs"))
            // Check for updates asynchronously
            new BukkitRunnable() {
                @Override
                public void run() {
                    String latest = new VersionChecker().getLatestVersion();
                    String current = plugin.getDescription().getVersion().replace("s", "");

                    if (latest == null) return;
                    if (!current.equals(latest)) event.getPlayer()
                        .sendMessage(ChatColor.YELLOW + "An update is available for " + pluginName + "! " + ChatColor.GOLD + "(" + current + " → " + latest + ")\n" + ChatColor.DARK_AQUA + "https://github.com/SilverstoneMC/" + pluginName + "/releases/latest");
                }
            }.runTaskAsynchronously(plugin);
    }

    public void logUpdate(String current, String latest) {
        String pluginName = plugin.getDescription().getName();

        plugin.getLogger()
            .warning("An update is available for " + pluginName + "! (" + current + " → " + latest + ")");
        plugin.getLogger().warning("https://github.com/SilverstoneMC/" + pluginName + "/releases/latest");
    }
}
