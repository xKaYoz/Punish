package net.akarian.punish.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

/**
 * Created by KaYoz on 8/7/2017.
 * Subscribe to me on Youtube:
 * http://www.youtube.com/c/KaYozMC/
 */

public class ItemBuilder {
    public static ItemStack build(Material material, int amount, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Chat.formatList(lore));
        meta.setDisplayName(Chat.format(name));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getPlayerHead(UUID uuid) {

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta smeta = (SkullMeta) skull.getItemMeta();

        smeta.setOwner(NameManager.getName(uuid));
        smeta.setDisplayName(Chat.format("&e" + NameManager.getName(uuid)));
        skull.setItemMeta(smeta);

        return skull;
    }

}
