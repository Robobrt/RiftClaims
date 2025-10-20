package com.riftproject.riftclaims.command;

import com.riftproject.riftclaims.RiftClaims;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ClaimAdminCommand implements CommandExecutor {

    private final RiftClaims plugin;

    public ClaimAdminCommand(RiftClaims plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getPermissionManager().hasPermission(sender, "admin")) {
            // Сообщение об ошибке будет отправлено сервером автоматически из plugin.yml
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.getLocaleManager().loadMessages();
            plugin.getPermissionManager().loadPermissions();
            plugin.getClaimToolManager().loadToolConfig();
            plugin.getProtectionManager().loadConfig(); // Добавлено
            sender.sendMessage("§aRiftClaims configurations reloaded.");
            return true;
        }

        sender.sendMessage("§cUsage: /claimadmin reload");
        return true;
    }
}