package com.riftproject.riftclaims.command;

import com.riftproject.riftclaims.RiftClaims;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClaimAdminTabCompleter implements TabCompleter {

    private final RiftClaims plugin;
    private static final List<String> COMMANDS = Arrays.asList("reload");

    public ClaimAdminTabCompleter(RiftClaims plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!plugin.getPermissionManager().hasPermission(sender, "admin")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], COMMANDS, new ArrayList<>());
        }
        
        return new ArrayList<>();
    }
}