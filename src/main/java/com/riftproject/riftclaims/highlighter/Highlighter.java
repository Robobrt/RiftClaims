package com.riftproject.riftclaims.highlighter;

import com.riftproject.riftclaims.RiftClaims;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Highlighter {

    private final RiftClaims plugin;
    private final Map<UUID, List<BlockDisplay>> activeDisplays = new HashMap<>();

    public Highlighter(RiftClaims plugin) {
        this.plugin = plugin;
    }

    public void startHighlighting(Player player, List<Location> corners) {
        stopHighlighting(player);
        if (corners == null || corners.size() != 8) return;

        List<BlockDisplay> playerDisplays = new ArrayList<>();
        activeDisplays.put(player.getUniqueId(), playerDisplays);

        // Получаем материал из конфига
        Material cornerMaterial = Material.matchMaterial(plugin.getConfig().getString("highlighter.corner-material", "SEA_LANTERN"));
        if (cornerMaterial == null || !cornerMaterial.isBlock()) {
            cornerMaterial = Material.SEA_LANTERN;
        }

        // Проходим по 8 углам региона и в каждом создаем светящийся блок
        for (Location corner : corners) {
            BlockDisplay display = spawnGlowingCornerBlock(player, corner, cornerMaterial);
            if (display != null) {
                playerDisplays.add(display);
            }
        }
    }

    public void stopHighlighting(Player player) {
        List<BlockDisplay> displays = activeDisplays.remove(player.getUniqueId());
        if (displays != null) {
            displays.forEach(Entity::remove);
        }
    }

    public void stopAllHighlighting() {
        activeDisplays.values().forEach(list -> list.forEach(Entity::remove));
        activeDisplays.clear();
    }

    /**
     * Создает одну сущность BlockDisplay, которая выглядит как блок и светится.
     */
    private BlockDisplay spawnGlowingCornerBlock(Player player, Location location, Material material) {
        // Мы спавним сущность в мире, чтобы она существовала на сервере
        return location.getWorld().spawn(location, BlockDisplay.class, display -> {
            // Внешний вид
            display.setBlock(material.createBlockData());
            display.setBrightness(new Display.Brightness(15, 15)); // Максимальная яркость

            // Поведение
            display.setGravity(false);
            display.setPersistent(false);

            // --- КЛЮЧЕВАЯ ЛОГИКА ---
            // Мы напрямую включаем свечение для этой сущности.
            // Это аналог команды /effect give ... glowing
            display.setGlowing(true); 
            // Устанавливаем цвет свечения (это API Paper/Purpur)
            display.setGlowColorOverride(Color.AQUA); 
            
            // Центрируем модель блока, чтобы он выглядел как обычный блок
            Transformation transformation = display.getTransformation();
            transformation.getTranslation().set(-0.5f, -0.5f, -0.5f);
            display.setTransformation(transformation);

            // Чтобы сущность не мешала, делаем её "маркером"
            // Это не обязательно для BlockDisplay, но является хорошей практикой
            // display.setMarker(true); // В API 1.20+ это может быть недоступно для BlockDisplay
        });
    }
}