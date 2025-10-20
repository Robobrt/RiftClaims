package com.riftproject.riftclaims.listener;

import com.riftproject.riftclaims.RiftClaims;
import com.riftproject.riftclaims.claim.Region;
import com.riftproject.riftclaims.menu.CreationMenu;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class ClaimToolListener implements Listener {

    private final RiftClaims plugin;

    public ClaimToolListener(RiftClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!plugin.getClaimToolManager().isClaimTool(itemInHand)) return;
        if (player.isSneaking() && event.getAction().name().startsWith("RIGHT_CLICK")) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (plugin.getSessionManager().hasSession(player.getUniqueId())) {
                new CreationMenu(plugin).open(player);
            } else {
                if (plugin.getClaimManager().getClaimByOwner(player.getUniqueId()) != null) {
                    plugin.getLocaleManager().sendMessage(player, "error.already-have-claim");
                    event.setCancelled(true);
                    return;
                }
                startClaimProcess(player, event.getClickedBlock().getLocation());
            }
            event.setCancelled(true);
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (plugin.getSessionManager().hasSession(player.getUniqueId())) {
                new CreationMenu(plugin).open(player);
            }
        } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (plugin.getSessionManager().hasSession(player.getUniqueId())) {
                plugin.getSessionManager().cancelSession(player);
            }
        }
    }

    @EventHandler
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack previousItem = player.getInventory().getItem(event.getPreviousSlot());
        if (previousItem != null && plugin.getClaimToolManager().isClaimTool(previousItem)) {
            if (plugin.getSessionManager().hasSession(player.getUniqueId())) {
                plugin.getSessionManager().cancelSession(player);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (plugin.getSessionManager().hasSession(event.getPlayer().getUniqueId())) {
            plugin.getSessionManager().cancelSession(event.getPlayer());
        }
    }

    private void startClaimProcess(Player player, Location center) {
        // --- НОВАЯ ЛОГИКА СОЗДАНИЯ РЕГИОНА "АКВАРИУМ" ---
        
        // Получаем радиус по X и Z из прав игрока.
        int radiusXZ = plugin.getClaimToolManager().getClaimRadiusForPlayer(player);
        // Радиус по Y теперь фиксированный, 10 блоков вверх и вниз.
        int radiusY = 10; 

        // Вычисляем два противоположных угла куба
        Location pos1 = center.clone().subtract(radiusXZ, radiusY, radiusXZ);
        Location pos2 = center.clone().add(radiusXZ, radiusY, radiusXZ);
        
        // Создаем единый регион для всего
        Region claimRegion = new Region(pos1, pos2);

        // Проверяем на пересечение
        if (plugin.getClaimManager().isRegionOverlapping(claimRegion)) {
            plugin.getLocaleManager().sendMessage(player, "error.claim-is-overlapping");
            return;
        }
        
        // --- ЗАПУСКАЕМ ПРОЦЕСС С ЕДИНЫМ РЕГИОНОМ ---
        // Сохраняем "аквариум" в сессию
        plugin.getSessionManager().startSession(player, center, claimRegion); 
        // И его же отправляем на отрисовку
        plugin.getHighlighter().startHighlighting(player, claimRegion.getCornerLocations()); 
        
        plugin.getClaimToolManager().activateTool(player.getInventory().getItemInMainHand());
        plugin.getLocaleManager().sendMessage(player, "claim.confirm-prompt");
    }
}