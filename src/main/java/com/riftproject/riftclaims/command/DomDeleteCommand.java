package com.riftproject.riftclaims.command;

import com.riftproject.riftclaims.RiftClaims;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DomDeleteCommand implements CommandExecutor {
    private final RiftClaims plugin;

    public DomDeleteCommand(RiftClaims plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        Player player = (Player) sender;
        plugin.getClaimManager().deleteClaim(player);
        return true;
    }
}