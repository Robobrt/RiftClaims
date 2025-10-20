package com.riftproject.riftclaims.manager;

import com.riftproject.riftclaims.RiftClaims;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class PermissionManager {

    private final RiftClaims plugin;
    private final Map<String, String> permissions = new HashMap<>();

    public PermissionManager(RiftClaims plugin) {
        this.plugin = plugin;
        loadPermissions();
    }

    public void loadPermissions() {
        permissions.clear();
        // Загружаем каждое разрешение из конфига. Если его нет, используем значение по умолчанию.
        permissions.put("admin", plugin.getConfig().getString("permissions.admin", "riftclaims.admin"));
        permissions.put("dom-command", plugin.getConfig().getString("permissions.dom-command", "riftclaims.command.dom"));
        permissions.put("domdelete-command", plugin.getConfig().getString("permissions.domdelete-command", "riftclaims.command.domdelete"));
        permissions.put("size-prefix", plugin.getConfig().getString("permissions.size-prefix", "riftclaims.size."));
    }

    /**
     * Получает строку разрешения по ключу.
     * @param key Ключ из конфига (например, "admin").
     * @return Строка разрешения (например, "riftclaims.admin").
     */
    public String getPermission(String key) {
        return permissions.getOrDefault(key, "riftclaims.invalid.permission");
    }

    /**
     * Проверяет, есть ли у игрока разрешение.
     * @param sender Игрок или консоль.
     * @param key Ключ разрешения из конфига.
     * @return true, если разрешение есть.
     */
    public boolean hasPermission(CommandSender sender, String key) {
        return sender.hasPermission(getPermission(key));
    }
}