package com.riftproject.riftclaims;

import com.riftproject.riftclaims.command.ClaimAdminCommand;
import com.riftproject.riftclaims.command.ClaimAdminTabCompleter;
import com.riftproject.riftclaims.command.DomCommand;
import com.riftproject.riftclaims.command.DomDeleteCommand;
import com.riftproject.riftclaims.highlighter.Highlighter;
import com.riftproject.riftclaims.listener.ClaimToolListener;
import com.riftproject.riftclaims.listener.MenuListener;
import com.riftproject.riftclaims.listener.ProtectionListener;
import com.riftproject.riftclaims.listener.ToolProtectionListener;
import com.riftproject.riftclaims.locale.LocaleManager;
import com.riftproject.riftclaims.manager.*;
import com.riftproject.riftclaims.storage.StorageManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RiftClaims extends JavaPlugin {

    private LocaleManager localeManager;
    private StorageManager storageManager;
    private PermissionManager permissionManager;
    private ProtectionManager protectionManager; // Добавлено
    private ClaimManager claimManager;
    private ClaimToolManager claimToolManager;
    private ClaimSessionManager sessionManager;
    private Highlighter highlighter;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        this.localeManager = new LocaleManager(this);
        this.permissionManager = new PermissionManager(this);
        this.protectionManager = new ProtectionManager(this); // Инициализировано
        this.claimToolManager = new ClaimToolManager(this);
        this.storageManager = new StorageManager(this);
        this.claimManager = new ClaimManager(this);
        this.sessionManager = new ClaimSessionManager(this);
        this.highlighter = new Highlighter(this);

        getServer().getPluginManager().registerEvents(new ClaimToolListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new ToolProtectionListener(this), this);

        getCommand("dom").setExecutor(new DomCommand(this));
        getCommand("domdelete").setExecutor(new DomDeleteCommand(this));
        getCommand("claimadmin").setExecutor(new ClaimAdminCommand(this));
        getCommand("claimadmin").setTabCompleter(new ClaimAdminTabCompleter(this));

        getLogger().info("RiftClaims has been enabled!");
    }

    @Override
    public void onDisable() {
        if (claimManager != null) {
            claimManager.saveClaims();
        }
        if (highlighter != null) {
            highlighter.stopAllHighlighting();
        }
        getLogger().info("RiftClaims has been disabled!");
    }

    public LocaleManager getLocaleManager() { return localeManager; }
    public StorageManager getStorageManager() { return storageManager; }
    public PermissionManager getPermissionManager() { return permissionManager; }
    public ProtectionManager getProtectionManager() { return protectionManager; } // Добавлено
    public ClaimManager getClaimManager() { return claimManager; }
    public ClaimToolManager getClaimToolManager() { return claimToolManager; }
    public ClaimSessionManager getSessionManager() { return sessionManager; }
    public Highlighter getHighlighter() { return highlighter; }
}