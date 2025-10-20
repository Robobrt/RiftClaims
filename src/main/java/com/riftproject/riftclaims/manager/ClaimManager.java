package com.riftproject.riftclaims.manager;

import com.riftproject.riftclaims.RiftClaims;
import com.riftproject.riftclaims.claim.Claim;
import com.riftproject.riftclaims.claim.Region;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClaimManager {

    private final RiftClaims plugin;
    private final List<Claim> claims;

    public ClaimManager(RiftClaims plugin) {
        this.plugin = plugin;
        this.claims = new ArrayList<>();
        loadClaims();
    }

    public void loadClaims() {
        claims.clear();
        claims.addAll(plugin.getStorageManager().loadClaims());
        plugin.getLogger().info("Loaded " + claims.size() + " claims.");
    }

    public void saveClaims() {
        plugin.getStorageManager().saveClaims(claims);
        plugin.getLogger().info("Saved " + claims.size() + " claims.");
    }

    public void createClaim(Player player, Region region) {
        if (getClaimByOwner(player.getUniqueId()) != null) {
            plugin.getLocaleManager().sendMessage(player, "error.already-have-claim");
            return;
        }
        Claim claim = new Claim(player.getUniqueId(), region);
        claims.add(claim);
    }
    
    public void deleteClaim(Player player) {
        Claim claim = getClaimByOwner(player.getUniqueId());
        if (claim != null) {
            if (plugin.getClaimToolManager().giveClaimToolSafely(player)) {
                claims.remove(claim);
                plugin.getLocaleManager().sendMessage(player, "claim.deleted");
                plugin.getLocaleManager().sendMessage(player, "claim.tool-returned-in-hotbar");
            } else {
                plugin.getLocaleManager().sendMessage(player, "error.cannot-delete-hotbar-full");
            }
        } else {
            plugin.getLocaleManager().sendMessage(player, "error.no-claim-to-delete");
        }
    }

    /**
     * Проверяет, пересекается ли предложенный регион с уже существующими приватами.
     * @param newRegion Регион для проверки.
     * @return true, если есть пересечение, иначе false.
     */
    public boolean isRegionOverlapping(Region newRegion) {
        for (Claim existingClaim : claims) {
            if (existingClaim.getRegion().intersects(newRegion)) {
                return true;
            }
        }
        return false;
    }

    public Claim getClaimByOwner(UUID ownerUUID) {
        return claims.stream()
                .filter(claim -> claim.getOwner().equals(ownerUUID))
                .findFirst()
                .orElse(null);
    }

    public Claim getClaimAt(Location location) {
        return claims.stream()
                .filter(claim -> claim.getRegion().contains(location))
                .findFirst()
                .orElse(null);
    }
    
    public List<Claim> getAllClaims() {
        return new ArrayList<>(claims);
    }
}