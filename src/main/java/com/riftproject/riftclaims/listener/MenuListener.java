package com.riftproject.riftclaims.listener;

import com.riftproject.riftclaims.RiftClaims;
import com.riftproject.riftclaims.manager.ClaimSessionManager.ClaimSession;
import com.riftproject.riftclaims.menu.CreationMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {

    private final RiftClaims plugin;

    public MenuListener(RiftClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || !(e.getWhoClicked() instanceof Player)) {
            return;
        }

        if (e.getInventory().getHolder() instanceof CreationMenu) {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            ClaimSession session = plugin.getSessionManager().getSession(player.getUniqueId());

            if (session == null) {
                player.closeInventory();
                return;
            }

            switch (e.getSlot()) {
                case 11: // Кнопка "Стартовый домик"
                    session.toggleStarterHouse();
                    new CreationMenu(plugin).open(player);
                    break;

                case 13: // Кнопка "Подтвердить"
                    player.closeInventory();
                    player.getInventory().setItemInMainHand(null);
                    
                    plugin.getClaimManager().createClaim(player, session.getRegion());
                    plugin.getLocaleManager().sendMessage(player, "claim.creation-success");

                    if (session.isStarterHouseEnabled()) {
                        plugin.getLocaleManager().sendMessage(player, "claim.house-placing");
                        // TODO: Здесь будет вызов метода для вставки схематика.
                        // Например: plugin.getSchematicManager().pasteStarterHouse(session.getCenter());
                    }

                    plugin.getSessionManager().endSession(player.getUniqueId());
                    plugin.getHighlighter().stopHighlighting(player);
                    break;

                case 15: // Кнопка "Отклонить"
                    player.closeInventory();
                    plugin.getSessionManager().cancelSession(player);
                    break;
            }
        }
    }
}