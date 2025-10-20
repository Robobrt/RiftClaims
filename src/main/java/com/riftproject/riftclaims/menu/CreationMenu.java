package com.riftproject.riftclaims.menu;

import com.riftproject.riftclaims.RiftClaims;
import com.riftproject.riftclaims.manager.ClaimSessionManager.ClaimSession;
import com.riftproject.riftclaims.util.ItemBuilder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CreationMenu implements InventoryHolder {

    private final RiftClaims plugin;
    private Inventory inventory;

    public CreationMenu(RiftClaims plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        ClaimSession session = plugin.getSessionManager().getSession(player.getUniqueId());
        if (session == null) return;

        String title = plugin.getLocaleManager().getMessage("menu.creation.title");
        this.inventory = Bukkit.createInventory(this, 27, LegacyComponentSerializer.legacyAmpersand().deserialize(title));

        // Кнопка "Стартовый домик"
        boolean houseEnabled = session.isStarterHouseEnabled();
        inventory.setItem(11, new ItemBuilder(houseEnabled ? Material.LIME_WOOL : Material.RED_WOOL)
                .setName(plugin.getLocaleManager().getMessage("menu.creation.house-item.name"))
                .setLore(
                        plugin.getLocaleManager().getMessage("menu.creation.house-item.lore-line1"),
                        houseEnabled ? plugin.getLocaleManager().getMessage("menu.creation.house-item.lore-enabled")
                                     : plugin.getLocaleManager().getMessage("menu.creation.house-item.lore-disabled")
                )
                .build());

        // Кнопка "Подтвердить"
        inventory.setItem(13, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
                .setName(plugin.getLocaleManager().getMessage("menu.creation.confirm-item.name"))
                .build());

        // Кнопка "Отклонить"
        inventory.setItem(15, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setName(plugin.getLocaleManager().getMessage("menu.creation.cancel-item.name"))
                .build());
        
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}