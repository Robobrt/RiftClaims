package com.riftproject.riftclaims.manager;

import com.riftproject.riftclaims.RiftClaims;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ClaimToolManager {

    private final RiftClaims plugin;
    private final NamespacedKey claimToolKey;
    private String toolName;
    private List<String> toolLore;
    private Material toolMaterial;

    public ClaimToolManager(RiftClaims plugin) {
        this.plugin = plugin;
        this.claimToolKey = new NamespacedKey(plugin, "claim_tool");
        loadToolConfig();
    }

    public void loadToolConfig() {
        toolMaterial = Material.valueOf(plugin.getConfig().getString("claim-tool.material", "GOLDEN_HOE"));
        toolName = plugin.getLocaleManager().getMessage("claim-tool.name");
        toolLore = plugin.getConfig().getStringList("claim-tool.lore").stream()
                .map(line -> plugin.getLocaleManager().getMessage(line))
                .collect(Collectors.toList());
    }

    public ItemStack getClaimTool() {
        ItemStack tool = new ItemStack(toolMaterial);
        ItemMeta meta = tool.getItemMeta();
        
        meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(toolName));
        List<Component> componentLore = toolLore.stream()
                .map(line -> LegacyComponentSerializer.legacyAmpersand().deserialize(line))
                .collect(Collectors.toList());
        meta.lore(componentLore);
        
        meta.getPersistentDataContainer().set(claimToolKey, PersistentDataType.BYTE, (byte) 1);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        tool.setItemMeta(meta);
        return tool;
    }

    /**
     * Безопасно выдает инструмент игроку только в хотбар.
     * @param player Игрок, которому нужно выдать инструмент.
     * @return true, если инструмент был успешно выдан, иначе false.
     */
    public boolean giveClaimToolSafely(Player player) {
        // Хотбар - это слоты с 0 по 8
        for (int i = 0; i <= 8; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                player.getInventory().setItem(i, getClaimTool());
                return true; // Успешно выдали
            }
        }
        return false; // В хотбаре нет места
    }
    
    public boolean isClaimTool(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(claimToolKey, PersistentDataType.BYTE);
    }
    
    public void activateTool(ItemStack item) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }
    
    public void deactivateTool(ItemStack item) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        meta.removeEnchant(Enchantment.LURE);
        item.setItemMeta(meta);
    }
    
    public int getClaimRadiusForPlayer(Player player) {
        ConfigurationSection sizeSection = plugin.getConfig().getConfigurationSection("claim-sizes");
        if (sizeSection == null) return plugin.getConfig().getInt("claim-sizes.default", 10);

        int radius = plugin.getConfig().getInt("claim-sizes.default", 10);
        
        String sizePrefix = plugin.getPermissionManager().getPermission("size-prefix");

        List<String> sortedKeys = new ArrayList<>(sizeSection.getKeys(false));
        Collections.reverse(sortedKeys);

        for (String key : sortedKeys) {
            if (!key.equals("default") && player.hasPermission(sizePrefix + key)) {
                radius = sizeSection.getInt(key);
                break;
            }
        }
        return radius;
    }
}