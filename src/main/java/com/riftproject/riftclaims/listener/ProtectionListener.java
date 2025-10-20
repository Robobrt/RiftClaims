package com.riftproject.riftclaims.listener;

import com.riftproject.riftclaims.RiftClaims;
import com.riftproject.riftclaims.claim.Claim;
import com.riftproject.riftclaims.manager.ProtectionManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Iterator;

public class ProtectionListener implements Listener {

    private final RiftClaims plugin;
    private final ProtectionManager prot;

    public ProtectionListener(RiftClaims plugin) {
        this.plugin = plugin;
        this.prot = plugin.getProtectionManager();
    }

    private boolean canPerformAction(Player player, Claim claim) {
        if (claim == null) return true;
        return claim.getOwner().equals(player.getUniqueId());
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!prot.BLOCK_BREAK) return;
        Claim claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
        if (!canPerformAction(event.getPlayer(), claim)) {
            event.setCancelled(true);
            plugin.getLocaleManager().sendMessage(event.getPlayer(), "protection.cannot-build");
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!prot.BLOCK_PLACE) return;
        Claim claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
        if (!canPerformAction(event.getPlayer(), claim)) {
            event.setCancelled(true);
            plugin.getLocaleManager().sendMessage(event.getPlayer(), "protection.cannot-build");
        }
    }
    
    // ИСПРАВЛЕНО: Добавлена аннотация для скрытия предупреждения об устаревшем методе
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) return;
        
        Material type = event.getClickedBlock().getType();
        
        boolean shouldProtect;
        if (prot.PROTECT_ALL_INTERACTABLE) {
            // ИСПРАВЛЕНО: Возвращаем старый, но рабочий метод Material.isInteractable()
            shouldProtect = type.isInteractable() && !type.name().contains("SLAB") && !type.name().contains("STAIRS") && !type.name().contains("WALL");
        } else {
            shouldProtect = prot.PROTECTED_BLOCKS.contains(type);
        }
        
        if (shouldProtect) {
             Claim claim = plugin.getClaimManager().getClaimAt(event.getClickedBlock().getLocation());
             if (!canPerformAction(event.getPlayer(), claim)) {
                 event.setCancelled(true);
                 plugin.getLocaleManager().sendMessage(event.getPlayer(), "protection.cannot-interact");
             }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Claim claim = plugin.getClaimManager().getClaimAt(event.getEntity().getLocation());
        if (claim == null) return;
        
        Player attacker = getAttacker(event.getDamager());
        
        if (isProtectedEntityType(event.getEntity())) {
            if (attacker == null || !canPerformAction(attacker, claim)) {
                event.setCancelled(true);
                if (attacker != null) plugin.getLocaleManager().sendMessage(attacker, "protection.cannot-harm-entities");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame && !prot.ITEM_FRAME) return;
        if (event.getEntity() instanceof Painting && !prot.PAINTING) return;

        Claim claim = plugin.getClaimManager().getClaimAt(event.getEntity().getLocation());
        Player remover = getAttacker(event.getRemover());
        if (claim != null && (remover == null || !canPerformAction(remover, claim))) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (isProtectedEntityType(event.getRightClicked())) {
            Claim claim = plugin.getClaimManager().getClaimAt(event.getRightClicked().getLocation());
            if (!canPerformAction(event.getPlayer(), claim)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!prot.BUCKET_USAGE) return;
        Claim claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
        if (!canPerformAction(event.getPlayer(), claim)) {
            event.setCancelled(true);
            plugin.getLocaleManager().sendMessage(event.getPlayer(), "protection.cannot-use-buckets");
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        Claim claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
        if (claim == null) return;
        
        if (event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            if (!prot.FLINT_AND_STEEL) return;
            if (event.getPlayer() != null && !canPerformAction(event.getPlayer(), claim)) {
                event.setCancelled(true);
                plugin.getLocaleManager().sendMessage(event.getPlayer(), "protection.cannot-ignite");
            }
        } else if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            if (prot.FIRE_SPREAD) event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        if (!prot.BLOCK_BURN) return;
        if (plugin.getClaimManager().getClaimAt(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!prot.BLOCK_EXPLOSION) return;
        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            if (plugin.getClaimManager().getClaimAt(iterator.next().getLocation()) != null) {
                iterator.remove();
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!prot.PISTON_MOVEMENT) return;
        Claim sourceClaim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
        for (Block block : event.getBlocks()) {
            Claim targetClaim = plugin.getClaimManager().getClaimAt(block.getRelative(event.getDirection()).getLocation());
            if (sourceClaim != targetClaim) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (!prot.PISTON_MOVEMENT) return;
        Claim sourceClaim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
        for (Block block : event.getBlocks()) {
            Claim targetClaim = plugin.getClaimManager().getClaimAt(block.getLocation());
            if (sourceClaim != targetClaim) {
                event.setCancelled(true);
                return;
            }
        }
    }
    
    private boolean isProtectedEntityType(Entity entity) {
        if (prot.DAMAGE_ANIMALS && entity instanceof Animals) return true;
        if (prot.DAMAGE_VILLAGERS && (entity instanceof Villager || entity instanceof WanderingTrader)) return true;
        if (prot.DAMAGE_GOLEMS && entity instanceof Golem) return true;
        if (prot.ARMOR_STAND && entity instanceof ArmorStand) return true;
        if (prot.ITEM_FRAME && (entity instanceof ItemFrame || entity instanceof GlowItemFrame)) return true;
        if (prot.PAINTING && entity instanceof Painting) return true;
        if (prot.VEHICLE && entity instanceof Vehicle) return true;
        return false;
    }
    
    private Player getAttacker(Entity damager) {
        if (damager instanceof Player) return (Player) damager;
        if (damager instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (shooter instanceof Player) return (Player) shooter;
        }
        return null;
    }
}