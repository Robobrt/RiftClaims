package com.riftproject.riftclaims.manager;

import com.riftproject.riftclaims.RiftClaims;
import com.riftproject.riftclaims.claim.Region;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClaimSessionManager {

    private final RiftClaims plugin;
    private final Map<UUID, ClaimSession> activeSessions = new HashMap<>();

    public ClaimSessionManager(RiftClaims plugin) {
        this.plugin = plugin;
    }
    
    public static class ClaimSession {
        private final Location center;
        private final Region region;
        private boolean starterHouseEnabled = true;

        public ClaimSession(Location center, Region region) {
            this.center = center;
            this.region = region;
        }
        public Location getCenter() { return center; }
        public Region getRegion() { return region; }
        public boolean isStarterHouseEnabled() { return starterHouseEnabled; }
        public void toggleStarterHouse() { this.starterHouseEnabled = !this.starterHouseEnabled; }
    }

    public void startSession(Player player, Location center, Region region) {
        activeSessions.put(player.getUniqueId(), new ClaimSession(center, region));
    }

    public void endSession(UUID playerUUID) {
        activeSessions.remove(playerUUID);
    }

    public ClaimSession getSession(UUID playerUUID) {
        return activeSessions.get(playerUUID);
    }

    public boolean hasSession(UUID playerUUID) {
        return activeSessions.containsKey(playerUUID);
    }
    
    // Централизованный метод отмены, чтобы избежать дублирования кода
    public void cancelSession(Player player) {
        if (!hasSession(player.getUniqueId())) {
            return;
        }
        endSession(player.getUniqueId());
        plugin.getHighlighter().stopHighlighting(player);
        plugin.getClaimToolManager().deactivateTool(player.getInventory().getItemInMainHand());
        plugin.getLocaleManager().sendMessage(player, "claim.creation-cancelled");
    }
}