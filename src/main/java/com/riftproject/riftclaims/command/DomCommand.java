package com.riftproject.riftclaims.command;

import com.riftproject.riftclaims.RiftClaims;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DomCommand implements CommandExecutor {
    private final RiftClaims plugin;

    public DomCommand(RiftClaims plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        Player player = (Player) sender;
        if (plugin.getClaimManager().getClaimByOwner(player.getUniqueId()) != null) {
            plugin.getLocaleManager().sendMessage(player, "error.already-have-claim");
            return true;
        }
        
        for (ItemStack item : player.getInventory().getContents()) {
            if (plugin.getClaimToolManager().isClaimTool(item)) {
                plugin.getLocaleManager().sendMessage(player, "error.already-have-tool");
                return true;
            }
        }

        if (plugin.getClaimToolManager().giveClaimToolSafely(player)) {
            plugin.getLocaleManager().sendMessage(player, "claim.tool-received");
        } else {
            plugin.getLocaleManager().sendMessage(player, "error.hotbar-full");
        }
        return true;
    }
}