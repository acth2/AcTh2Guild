package fr.acth2.guilds;

import fr.acth2.guilds.command.GuildCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class Main extends JavaPlugin implements Listener {

    private static final References mainReference = new References();

    @Override
    public void onEnable() {
        References references = new References();
        getCommand("guild").setExecutor(new GuildCommand(references));
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Logger.getAnonymousLogger().info("Plugin started..");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String chatterUUID = event.getPlayer().getUniqueId().toString();
        if (mainReference.isGuildChat(chatterUUID)) {
            event.setCancelled(true);

            String guildId = mainReference.getId(chatterUUID);
            List<String> guildUuids = mainReference.getAllPlayersInGuild(guildId);
            for (String gmUuid : guildUuids) {
                try {
                    UUID memberUUID = UUID.fromString(gmUuid);
                    Player guildMember = Bukkit.getPlayer(memberUUID);
                    if (guildMember != null && guildMember.isOnline()) {
                        guildMember.sendMessage("§7[GUILD] §2"
                                + event.getPlayer().getName() + "§7: §r" + event.getMessage());
                    }
                } catch (IllegalArgumentException ignored) { }
            }
        }
    }
}
