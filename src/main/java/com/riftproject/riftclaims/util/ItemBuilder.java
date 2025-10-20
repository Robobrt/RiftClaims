package com.riftproject.riftclaims.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {
    private final ItemStack itemStack;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder setName(String name) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            // ИСПРАВЛЕНО: преобразуем строку с '&' кодами в современный компонент
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            // ИСПРАВЛЕНО: преобразуем каждую строку в компонент
            List<Component> componentLore = lore.stream()
                    .map(line -> LegacyComponentSerializer.legacyAmpersand().deserialize(line))
                    .collect(Collectors.toList());
            meta.lore(componentLore);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }
}