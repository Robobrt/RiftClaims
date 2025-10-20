package com.riftproject.riftclaims.locale;

import com.riftproject.riftclaims.RiftClaims;
import net.md_5.bungee.api.ChatColor; // ИСПРАВЛЕНО: используем современный импорт
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LocaleManager {

    private final RiftClaims plugin;
    private FileConfiguration messagesConfig;

    public LocaleManager(RiftClaims plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages_ru.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages_ru.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String path) {
        String message = messagesConfig.getString(path, "&cMessage not found: " + path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void sendMessage(CommandSender sender, String path) {
        sender.sendMessage(getMessage(path));
    }
}