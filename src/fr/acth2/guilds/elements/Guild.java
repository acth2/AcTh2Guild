package fr.acth2.guilds.elements;

import fr.acth2.guilds.enums.GuildRank;

import java.util.HashMap;
import java.util.Map;

public class Guild {

    private final String name;
    private String ownerUuid;
    private final Map<String, GuildRank> members;
    private boolean isPrivate;

    public Guild(String name, String ownerUuid) {
        this.name = name;
        this.ownerUuid = ownerUuid;
        this.members = new HashMap<>();
        this.isPrivate = false;

        members.put(ownerUuid, GuildRank.OWNER);
    }

    public String getName() {
        return name;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isOwner(String uuid) {
        return ownerUuid.equals(uuid);
    }

    public void setRank(String playerUuid, GuildRank rank) {
        if (members.containsKey(playerUuid)) {
            members.put(playerUuid, rank);
        }
    }

    public void addMember(String playerUuid, GuildRank rank) {
        members.put(playerUuid, rank);
    }

    public boolean removeMember(String playerUuid) {
        return (members.remove(playerUuid) != null);
    }

    public boolean isMember(String playerUuid) {
        return members.containsKey(playerUuid);
    }

    public boolean canBan(String playerUuid) {
        GuildRank rank = members.get(playerUuid);
        return (rank == GuildRank.OFFICER || rank == GuildRank.OWNER);
    }
}
