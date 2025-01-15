package fr.acth2.guilds.command;

import fr.acth2.guilds.References;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class GuildCommand implements CommandExecutor {

    private final References references;

    public GuildCommand(References references) {
        this.references = references;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§7[GUILD] §cOnly players can use this command!");
            return true;
        }
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage("§7[GUILD] §cUsage: /guild create <guildName>");
                    return true;
                }
                String guildName = args[1];

                if (references.isPlayerInDB(uuid)) {
                    player.sendMessage("§7[GUILD] §cYou seem to already be in a guild (or in DB)!");
                    return true;
                }

                boolean created = references.insertPlayer(uuid, guildName, 1, 0, 0);
                if (created) {
                    player.sendMessage("§7[GUILD] §2Guild §f" + guildName + "§2 created! You are the admin.");
                } else {
                    player.sendMessage("§7[GUILD] §cFailed to create the guild. Check console for errors.");
                }
                return true;

            case "invite":
                if (args.length < 2) {
                    player.sendMessage("§7[GUILD] §cUsage: /guild invite <playerName>");
                    return true;
                }
                String targetName = args[1];
                Player target = Bukkit.getPlayer(targetName);
                if (target == null) {
                    player.sendMessage("§7[GUILD] §cPlayer not found or offline.");
                    return true;
                }

                if (!references.isAdmin(uuid)) {
                    player.sendMessage("§7[GUILD] §cYou are not an admin of any guild!");
                    return true;
                }

                String adminGuild = references.getId(uuid);
                if (adminGuild == null || adminGuild.isEmpty()) {
                    player.sendMessage("§7[GUILD] §cYou don't have a guild to invite people to!");
                    return true;
                }

                String targetUUID = target.getUniqueId().toString();
                if (references.isPlayerInDB(targetUUID)) {
                    player.sendMessage("§7[GUILD] §cThat player is already in a guild (or in DB)!");
                    return true;
                }

                boolean invited = references.insertPlayer(targetUUID, adminGuild, 0, 0, 0);
                if (invited) {
                    player.sendMessage("§7[GUILD] §2You invited §f" + targetName + " §2to your guild!");
                    target.sendMessage("§7[GUILD] §2You have been invited to guild: §f" + adminGuild);
                } else {
                    player.sendMessage("§7[GUILD] §cFailed to invite " + targetName + ". Check console for errors.");
                }
                return true;

            case "chat":
                if (args.length > 2) {
                    player.sendMessage("§7[GUILD] §cUsage: '/guild chat' manage the guild-chat canal");
                    return true;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                String chatMessage = sb.toString().trim();

                String guildId = references.getId(uuid);
                if (guildId == null || guildId.isEmpty()) {
                    player.sendMessage("§7[GUILD] §cYou are not in a guild.");
                    return true;
                }

                List<String> guildUuids = references.getAllPlayersInGuild(guildId);
                if (guildUuids.isEmpty()) {
                    player.sendMessage("§7[GUILD] §cNo one else is in your guild to chat with.");
                    return true;
                }

                String chatterID = player.getUniqueId().toString();
                if (!references.isGuildChat(chatterID)) {
                    String registeredId = references.getId(chatterID);
                    references.deletePlayer(chatterID);

                    int isAdmin = references.isAdmin(chatterID) ? 1 : 0;

                    boolean reInserted = references.insertPlayer(
                            chatterID,
                            registeredId,
                            isAdmin,
                            (references.isPublic(registeredId) ? 1 : 0),
                            1
                    );

                    if (reInserted) {
                        player.sendMessage("§7[GUILD] §2You are now in the guild-canal!");
                    } else {
                        player.sendMessage("§7[GUILD] §cFailed to go in the guild-canal. Check console for errors.");
                    }
                } else {
                    String registeredId = references.getId(chatterID);
                    references.deletePlayer(chatterID);
                    int isAdmin = references.isAdmin(chatterID) ? 1 : 0;

                    boolean reInserted = references.insertPlayer(
                            chatterID,
                            registeredId,
                            isAdmin,
                            (references.isPublic(registeredId) ? 1 : 0),
                            0
                    );

                    if (reInserted) {
                        player.sendMessage("§7[GUILD] §2You are not in the guild-canal anymore!");
                    } else {
                        player.sendMessage("§7[GUILD] §cFailed to exit the guild-canal. Check console for errors.");
                    }
                }
                return true;

            case "promote":
                if (args.length < 2) {
                    player.sendMessage("§7[GUILD] §cUsage: /guild promote <playerName>");
                    return true;
                }
                String promoteName = args[1];
                Player promoteTarget = Bukkit.getPlayer(promoteName);
                if (promoteTarget == null) {
                    player.sendMessage("§7[GUILD] §cThat player is offline or doesn't exist.");
                    return true;
                }

                if (!references.isAdmin(uuid)) {
                    player.sendMessage("§7[GUILD] §cYou are not an admin, cannot promote others!");
                    return true;
                }

                String promoterGuild = references.getId(uuid);
                if (promoterGuild == null || promoterGuild.isEmpty()) {
                    player.sendMessage("§7[GUILD] §cYou have no guild to promote in!");
                    return true;
                }

                String promoteTargetUUID = promoteTarget.getUniqueId().toString();
                if (!references.isPlayerInDB(promoteTargetUUID)) {
                    player.sendMessage("§7[GUILD] §cThat player is not in a guild!");
                    return true;
                }
                String targetGuild = references.getId(promoteTargetUUID);
                if (targetGuild == null || !targetGuild.equalsIgnoreCase(promoterGuild)) {
                    player.sendMessage("§7[GUILD] §cThat player is not in your guild!");
                    return true;
                }

                boolean targetIsPublic = references.isPublic(promoteTargetUUID);
                references.deletePlayer(promoteTargetUUID);
                boolean reInserted = references.insertPlayer(
                        promoteTargetUUID,
                        promoterGuild,
                        1,
                        (targetIsPublic ? 1 : 0),
                        references.isGuildChatting(promoteTargetUUID) ? 1 : 0
                );

                if (reInserted) {
                    player.sendMessage("§7[GUILD] §2You promoted §f" + promoteName + "§2 to admin!");
                    promoteTarget.sendMessage("§7[GUILD] §2You have been promoted to admin in guild: §f" + promoterGuild);
                } else {
                    player.sendMessage("§7[GUILD] §cFailed to promote the player. Check console for errors.");
                }
                return true;

            case "ban":
                if (args.length < 2) {
                    player.sendMessage("§7[GUILD] §cUsage: /guild ban <playerName>");
                    return true;
                }
                String banName = args[1];
                Player banTarget = Bukkit.getPlayer(banName);
                if (banTarget == null) {
                    player.sendMessage("§7[GUILD] §cThat player is offline or doesn't exist.");
                    return true;
                }

                if (!references.isAdmin(uuid)) {
                    player.sendMessage("§7[GUILD] §cYou are not an admin, can't ban players!");
                    return true;
                }

                String bannerGuild = references.getId(uuid);
                if (bannerGuild == null || bannerGuild.isEmpty()) {
                    player.sendMessage("§7[GUILD] §cYou have no guild to ban from!");
                    return true;
                }

                String banTargetUUID = banTarget.getUniqueId().toString();
                if (!references.isPlayerInDB(banTargetUUID)) {
                    player.sendMessage("§7[GUILD] §cThat player is not in a guild.");
                    return true;
                }
                String banGuild = references.getId(banTargetUUID);
                if (!bannerGuild.equalsIgnoreCase(banGuild)) {
                    player.sendMessage("§7[GUILD] §cThat player is not in your guild!");
                    return true;
                }

                boolean banned = references.deletePlayer(banTargetUUID);
                if (banned) {
                    player.sendMessage("§7[GUILD] §2You banned §f" + banName + "§2 from the guild!");
                    banTarget.sendMessage("§7[GUILD] §cYou have been banned from the guild: §f" + banGuild);
                } else {
                    player.sendMessage("§7[GUILD] §cFailed to ban the player. Check console for errors.");
                }
                return true;

            case "private":
                if (args.length < 2) {
                    player.sendMessage("§7[GUILD] §cUsage: /guild private <0|1>");
                    return true;
                }
                if (!references.isAdmin(uuid)) {
                    player.sendMessage("§7[GUILD] §cYou are not an admin, can't set private/public!");
                    return true;
                }

                String privateGuild = references.getId(uuid);
                if (privateGuild == null || privateGuild.isEmpty()) {
                    player.sendMessage("§7[GUILD] §cYou have no guild to set public/private!");
                    return true;
                }

                int newPublicVal;
                try {
                    newPublicVal = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§7[GUILD] §cPlease specify 0 or 1.");
                    return true;
                }

                references.deletePlayer(uuid);
                boolean reInsertedPrivate = references.insertPlayer(uuid, privateGuild, 1, newPublicVal, references.isGuildChat(uuid) ? 1 : 0);
                if (reInsertedPrivate) {
                    if (newPublicVal == 1) {
                        player.sendMessage("§7[GUILD] §2Your guild is now PUBLIC!");
                    } else {
                        player.sendMessage("§7[GUILD] §2Your guild is now PRIVATE!");
                    }
                } else {
                    player.sendMessage("§7[GUILD] §cFailed to update guild privacy. Check console for errors.");
                }
                return true;

            case "quit":
                if (!references.isPlayerInDB(uuid)) {
                    player.sendMessage("§7[GUILD] §cYou are not in any guild to quit!");
                    return true;
                }
                boolean quit = references.deletePlayer(uuid);
                if (quit) {
                    player.sendMessage("§7[GUILD] §2You have quit your guild!");
                } else {
                    player.sendMessage("§7[GUILD] §cFailed to quit. Check console for errors.");
                }
                return true;

            case "remove":
                if (args.length < 2) {
                    player.sendMessage("§7[GUILD] §cUsage: /guild remove <playerName>");
                    return true;
                }

                if (!references.isAdmin(uuid)) {
                    player.sendMessage("§7[GUILD] §cYou are not an admin, cannot remove players!");
                    return true;
                }

                String removeName = args[1];
                Player removeTarget = Bukkit.getPlayer(removeName);
                if (removeTarget == null) {
                    player.sendMessage("§7[GUILD] §cPlayer is offline or doesn't exist.");
                    return true;
                }

                String removerGuild = references.getId(uuid);
                if (removerGuild == null || removerGuild.isEmpty()) {
                    player.sendMessage("§7[GUILD] §cYou have no guild from which to remove players!");
                    return true;
                }

                String removeUUID = removeTarget.getUniqueId().toString();
                if (!references.isPlayerInDB(removeUUID)) {
                    player.sendMessage("§7[GUILD] §cThat player is not in a guild!");
                    return true;
                }

                String removedGuild = references.getId(removeUUID);
                if (!removerGuild.equalsIgnoreCase(removedGuild)) {
                    player.sendMessage("§7[GUILD] §cThat player is not in your guild!");
                    return true;
                }

                boolean removed = references.deletePlayer(removeUUID);
                if (removed) {
                    player.sendMessage("§7[GUILD] §7You removed §f" + removeName + " §7from your guild.");
                    removeTarget.sendMessage("§7[GUILD] §cYou have been removed from the guild: §f" + removedGuild);
                } else {
                    player.sendMessage("§7[GUILD] §cFailed to remove player. Check console for errors.");
                }
                return true;

            case "list":
                String listGuild = references.getId(uuid);
                if (listGuild == null || listGuild.isEmpty()) {
                    player.sendMessage("§7[GUILD] §cYou are not in a guild!");
                    return true;
                }

                List<String> memberUuids = references.getAllPlayersInGuild(listGuild);
                if (memberUuids.isEmpty()) {
                    player.sendMessage("§7[GUILD] §cThere are no members in your guild (DB is empty?).");
                    return true;
                }

                StringBuilder listBuilder = new StringBuilder();
                for (String memUuid : memberUuids) {
                    try {
                        UUID mUUID = UUID.fromString(memUuid);
                        Player memPlayer = Bukkit.getPlayer(mUUID);
                        if (memPlayer != null && memPlayer.isOnline()) {
                            listBuilder.append("§2").append(memPlayer.getName()).append("§7, ");
                        }
                    } catch (IllegalArgumentException ignored) { }
                }

                String membersList = listBuilder.toString().trim();
                if (membersList.endsWith(",")) {
                    membersList = membersList.substring(0, membersList.length() - 1);
                }

                if (membersList.isEmpty()) {
                    player.sendMessage("§7[GUILD] §cAll your guild members are offline or invalid.");
                } else {
                    player.sendMessage("§7[GUILD] §2Members of your guild: " + membersList);
                }
                return true;

            default:
                sendUsage(player);
                return true;
        }
    }
    private void sendUsage(Player player) {
        player.sendMessage("§7[GUILD] §2---- Guild Commands ----");
        player.sendMessage("§7[GUILD] §2/guild create <name> §7- Create a new guild.");
        player.sendMessage("§7[GUILD] §2/guild invite <player> §7- Invite a player to your guild.");
        player.sendMessage("§7[GUILD] §2/guild promote <player> §7- Promote a member to admin.");
        player.sendMessage("§7[GUILD] §2/guild ban <player> §7- Ban a player from the guild.");
        player.sendMessage("§7[GUILD] §2/guild private <0|1> §7- Toggle your guild's privacy.");
        player.sendMessage("§7[GUILD] §2/guild chat <message> §7- Speak in guild chat.");
        player.sendMessage("§7[GUILD] §2/guild quit §7- Leave your current guild.");
        player.sendMessage("§7[GUILD] §2/guild remove <player> §7- Remove a member (non-ban).");
        player.sendMessage("§7[GUILD] §2/guild list §7- List all online members in your guild.");
    }
}
