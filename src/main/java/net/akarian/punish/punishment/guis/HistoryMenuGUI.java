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

        if (item.getType() == Material.RED_WOOL) {

        }

        switch (item.getType()) {
            case RED_WOOL:
                p.openInventory(new HistoryGUI(op.getUniqueId().toString(), "WARN", 1, 1).getInventory());
                break;
            case BLACK_WOOL:
                p.openInventory(new HistoryGUI(op.getUniqueId().toString(), "BAN", 1, 1).getInventory());
                break;
            case LIGHT_GRAY_WOOL:
                p.openInventory(new HistoryGUI(op.getUniqueId().toString(), "MUTE", 1, 1).getInventory());
                break;
            case YELLOW_WOOL:
                p.openInventory(new HistoryGUI(op.getUniqueId().toString(), "KICK", 1, 1).getInventory());
                break;
        }
    }

    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, 36, Chat.format("&c&l" + name + "'s Punishments"));

        inv.setItem(4, ItemBuilder.getPlayerHead(op.getUniqueId()));

        inv.setItem(19, ItemBuilder.build(Material.RED_WOOL, 1, "&cWarnings", Arrays.asList("&7See all warnings for " + name, "&7Total: &e" + PunishmentHandler.getNumWarn(uuid))));
        inv.setItem(21, ItemBuilder.build(Material.BLACK_WOOL, 1, "&cBans", Arrays.asList("&7See all bans for " + name, "&7Total: &e" + PunishmentHandler.getNumBan(uuid))));
        inv.setItem(23, ItemBuilder.build(Material.LIGHT_GRAY_WOOL, 1, "&cMutes", Arrays.asList("&7See all mutes for " + name, "&7Total: &e" + PunishmentHandler.getNumMute(uuid))));
        inv.setItem(25, ItemBuilder.build(Material.YELLOW_WOOL, 1, "&cKicks", Arrays.asList("&7See all kicks for " + name, "&7Total: &e" + PunishmentHandler.getNumKick(uuid))));

        return inv;
    }
}
