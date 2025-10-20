package com.riftproject.riftclaims.listener;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import com.riftproject.riftclaims.RiftClaims;

public class ToolProtectionListener implements Listener {

    private final RiftClaims plugin;

    public ToolProtectionListener(RiftClaims plugin) {
        this.plugin = plugin;
    }

    // Запрещает использовать инструмент как мотыгу
    @EventHandler
    public void onHoeUsage(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && plugin.getClaimToolManager().isClaimTool(event.getItem())) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType().toString().contains("DIRT")) {
                event.setCancelled(true);
            }
        }
    }
    
    // Блокирует ЛЮБЫЕ клики по предмету в инвентаре (перемещение, shift-клик, цифры и т.д.)
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        boolean isToolInvolved = plugin.getClaimToolManager().isClaimTool(event.getCurrentItem()) ||
                                 plugin.getClaimToolManager().isClaimTool(event.getCursor());

        if (isToolInvolved) {
            event.setCancelled(true);
            plugin.getLocaleManager().sendMessage((Player) event.getWhoClicked(), "tool-protection.cannot-move");
        }
    }

    // Блокирует перетаскивание предметов на ячейку с инструментом
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        for (int slot : event.getRawSlots()) {
            ItemStack item = event.getView().getItem(slot);
            if (plugin.getClaimToolManager().isClaimTool(item)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    // Блокирует выбрасывание предмета (клавиша Q)
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onToolDrop(PlayerDropItemEvent event) {
        if (plugin.getClaimToolManager().isClaimTool(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            plugin.getLocaleManager().sendMessage(event.getPlayer(), "tool-protection.cannot-drop");
        }
    }

    // Блокирует перемещение предмета в левую руку (клавиша F)
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        if (plugin.getClaimToolManager().isClaimTool(event.getMainHandItem()) ||
            plugin.getClaimToolManager().isClaimTool(event.getOffHandItem())) {
            event.setCancelled(true);
            plugin.getLocaleManager().sendMessage(event.getPlayer(), "tool-protection.cannot-move");
        }
    }
    
    // Сохраняет предмет при смерти, удаляя его из списка выпадающих вещей
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<ItemStack> drops = event.getDrops();
        ItemStack toolToKeep = null;
        
        Iterator<ItemStack> iterator = drops.iterator();
        while (iterator.hasNext()) {
            ItemStack drop = iterator.next();
            if (plugin.getClaimToolManager().isClaimTool(drop)) {
                toolToKeep = drop.clone(); // Сохраняем копию
                iterator.remove();       // Удаляем из списка дропа
                break;
            }
        }

        if (toolToKeep != null) {
            // Paper API позволяет добавить предмет в список сохраняемых
            event.getItemsToKeep().add(toolToKeep);
            plugin.getLogger().info("Claim tool for " + event.getEntity().getName() + " was saved from dropping on death.");
        }
    }
}