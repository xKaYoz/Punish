package net.akarian.punish.punishment.guis;

import net.akarian.punish.punishment.PunishmentHandler;
import net.akarian.punish.utils.Chat;
import net.akarian.punish.utils.ItemBuilder;
import net.akarian.punish.utils.PunishmentGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class HistoryMenuGUI implements PunishmentGUI {

    OfflinePlayer op;
    String uuid;
    String name;


    public HistoryMenuGUI(OfflinePlayer op) {
        this.op = op;
        this.uuid = op.getUniqueId().toString();
        this.name = op.getName();
    }

    public HistoryMenuGUI() {

    }

    @Override
    public void onGUIClick(Player p, int slot, ItemStack item, ClickType click, Inventory inv) {

        OfflinePlayer op = Bukkit.getOfflinePlayer(ChatColor.stripColor(inv.getItem(4).getItemMeta().getDisplayName()));

        if (item.getType() == Material.WOOL) {

            if (item.getItemMeta().getDisplayName().equalsIgnoreCase(Chat.format("&cWarnings"))) {
                p.openInventory(new HistoryGUI(op.getUniqueId().toString(), "WARN", 1, 1).getInventory());
            } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(Chat.format("&cBans"))) {
                p.openInventory(new HistoryGUI(op.getUniqueId().toString(), "BAN",1,1).getInventory());
            } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(Chat.format("&cMutes"))) {
                p.openInventory(new HistoryGUI(op.getUniqueId().toString(), "MUTE",1,1).getInventory());
            } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(Chat.format("&cKicks"))) {
                p.openInventory(new HistoryGUI(op.getUniqueId().toString(), "KICK",1,1).getInventory());
            }

        }

    }

    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, 36, Chat.format("&c&l" + name + "'s Punishments"));

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta smeta = (SkullMeta) skull.getItemMeta();

        smeta.setOwner(op.getName());
        smeta.setDisplayName(Chat.format("&e" + op.getName()));
        skull.setItemMeta(smeta);

        inv.setItem(4, skull);

        inv.setItem(19, ItemBuilder.build(Material.WOOL, 1, 14, "&cWarnings", Arrays.asList("&7See all warnings for " + name, "&7Total: &e" + PunishmentHandler.getNumWarn(uuid))));
        inv.setItem(21, ItemBuilder.build(Material.WOOL, 1, 15, "&cBans", Arrays.asList("&7See all bans for " + name, "&7Total: &e" + PunishmentHandler.getNumBan(uuid))));
        inv.setItem(23, ItemBuilder.build(Material.WOOL, 1, 8, "&cMutes", Arrays.asList("&7See all mutes for " + name, "&7Total: &e" + PunishmentHandler.getNumMute(uuid))));
        inv.setItem(25, ItemBuilder.build(Material.WOOL, 1, 4, "&cKicks", Arrays.asList("&7See all kicks for " + name, "&7Total: &e" + PunishmentHandler.getNumKick(uuid))));

        return inv;
    }
}
