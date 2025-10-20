package com.riftproject.riftclaims.manager;

import com.riftproject.riftclaims.RiftClaims;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ProtectionManager {

    private final RiftClaims plugin;
    private FileConfiguration protectionConfig;

    // --- Правила защиты ---
    public boolean BLOCK_BREAK;
    public boolean BLOCK_PLACE;
    public boolean PROTECT_ALL_INTERACTABLE;
    public Set<Material> PROTECTED_BLOCKS;
    public boolean DAMAGE_ANIMALS;
    public boolean DAMAGE_VILLAGERS;
    public boolean DAMAGE_GOLEMS;
    public boolean ARMOR_STAND;
    public boolean ITEM_FRAME;
    public boolean PAINTING;
    public boolean VEHICLE;
    public boolean BLOCK_EXPLOSION;
    public boolean BUCKET_USAGE;
    public boolean FLINT_AND_STEEL;
    public boolean FIRE_SPREAD;
    public boolean BLOCK_BURN;
    public boolean PISTON_MOVEMENT;

    public ProtectionManager(RiftClaims plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        File protectionFile = new File(plugin.getDataFolder(), "protection.yml");
        if (!protectionFile.exists()) {
            plugin.saveResource("protection.yml", false);
        }
        protectionConfig = YamlConfiguration.loadConfiguration(protectionFile);
        loadRules();
    }

    private void loadRules() {
        BLOCK_BREAK = protectionConfig.getBoolean("protection.block-break", true);
        BLOCK_PLACE = protectionConfig.getBoolean("protection.block-place", true);
        
        PROTECT_ALL_INTERACTABLE = protectionConfig.getBoolean("interaction.protect-all-interactable", true);
        PROTECTED_BLOCKS = protectionConfig.getStringList("interaction.protect-specific-blocks").stream()
                .map(Material::matchMaterial)
                .collect(Collectors.toSet());
        if (PROTECTED_BLOCKS == null) PROTECTED_BLOCKS = new HashSet<>();
        
        DAMAGE_ANIMALS = protectionConfig.getBoolean("entities.damage-animals", true);
        DAMAGE_VILLAGERS = protectionConfig.getBoolean("entities.damage-villagers", true);
        DAMAGE_GOLEMS = protectionConfig.getBoolean("entities.damage-golems", true);
        ARMOR_STAND = protectionConfig.getBoolean("entities.armor-stand", true);
        ITEM_FRAME = protectionConfig.getBoolean("entities.item-frame", true);
        PAINTING = protectionConfig.getBoolean("entities.painting", true);
        VEHICLE = protectionConfig.getBoolean("entities.vehicle", true);
        
        BLOCK_EXPLOSION = protectionConfig.getBoolean("environment.block-explosion", true);
        BUCKET_USAGE = protectionConfig.getBoolean("environment.bucket-usage", true);
        FLINT_AND_STEEL = protectionConfig.getBoolean("environment.flint-and-steel", true);
        FIRE_SPREAD = protectionConfig.getBoolean("environment.fire-spread", true);
        BLOCK_BURN = protectionConfig.getBoolean("environment.block-burn", true);
        PISTON_MOVEMENT = protectionConfig.getBoolean("environment.piston-movement", true);
    }
}