package com.riftproject.riftclaims.claim;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class Region {

    private final World world;
    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;

    public Region(Location pos1, Location pos2) {
        this.world = pos1.getWorld();
        this.minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        this.minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        this.maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
    }

    public boolean contains(Location location) {
        if (!location.getWorld().equals(world)) {
            return false;
        }
        return location.getBlockX() >= minX && location.getBlockX() <= maxX
                && location.getBlockY() >= minY && location.getBlockY() <= maxY
                && location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ;
    }

    /**
     * Проверяет, пересекается ли этот регион с другим.
     * @param other Другой регион для проверки.
     * @return true, если регионы пересекаются, иначе false.
     */
    public boolean intersects(Region other) {
        if (!this.world.equals(other.world)) {
            return false;
        }
        return this.minX <= other.maxX && this.maxX >= other.minX &&
               this.minY <= other.maxY && this.maxY >= other.minY &&
               this.minZ <= other.maxZ && this.maxZ >= other.minZ;
    }

    public List<Location> getCornerLocations() {
        List<Location> corners = new ArrayList<>();
        corners.add(new Location(world, minX, minY, minZ));
        corners.add(new Location(world, maxX, minY, minZ));
        corners.add(new Location(world, minX, minY, maxZ));
        corners.add(new Location(world, maxX, minY, maxZ));
        corners.add(new Location(world, minX, maxY, minZ));
        corners.add(new Location(world, maxX, maxY, minZ));
        corners.add(new Location(world, minX, maxY, maxZ));
        corners.add(new Location(world, maxX, maxY, maxZ));
        return corners;
    }
    
    public World getWorld() { return world; }
    public int getMinX() { return minX; }
    public int getMinY() { return minY; }
    public int getMinZ() { return minZ; }
    public int getMaxX() { return maxX; }
    public int getMaxY() { return maxY; }
    public int getMaxZ() { return maxZ; }
}