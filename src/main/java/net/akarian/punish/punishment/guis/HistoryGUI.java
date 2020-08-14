package net.akarian.punish.punishment.guis;

import net.akarian.punish.punishment.PunishmentHandler;
import net.akarian.punish.punishment.guis.handlers.HistoryGUIHandler;
import net.akarian.punish.utils.ItemBuilder;
import net.akarian.punish.utils.PunishmentGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.UUID;

public class HistoryGUI implements PunishmentGUI {

    String uuid;
    String type;
    String name;
    int category;
    int order;

    public HistoryGUI(String uuid, String type, int category, int order) {
        if (Bukkit.getPlayer(UUID.fromString(uuid)) != null) {
            this.name = Bukkit.getPlayer(UUID.fromString(uuid)).getName();
        } else {
            this.name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
        }
        this.uuid = uuid;
        this.type = type;
        this.category = category;
        this.order = order;
    }

    //Use for events
    public HistoryGUI() {
    }

    @Override
    public void onGUIClick(Player p, int slot, ItemStack item, ClickType type, Inventory inv) {

        String punished = ChatColor.stripColor(p.getOpenInventory().getTopInventory().getName().split(" ")[1].replace("s", ""));

        if (item.getType() == Material.BOOK) {
            if (type == ClickType.SHIFT_RIGHT) {
                String main = ChatColor.stripColor(p.getOpenInventory().getTopInventory().getName().split(" ")[0].split("'")[0]);
                String ID = ChatColor.stripColor(item.getItemMeta().getLore().get(2)).replace("ID: ", "");
                for (int i = 0; i <= 44; i++) {
                    p.getOpenInventory().setItem(i, ItemBuilder.build(Material.BARRIER, 1, "&cRefreshing...", Collections.emptyList()));
                }
                PunishmentHandler.removePunishment(ID);
                p.openInventory(new HistoryGUI(Bukkit.getOfflinePlayer(main).getUniqueId().toString(), punished, 1, 1).getInventory());
            } else if (type == ClickType.LEFT) {
                String ID = ChatColor.stripColor(item.getItemMeta().getLore().get(2)).replace("ID: ", "");
                p.performCommand("history " + ID);
                p.closeInventory();
            }
        } else if (item.getType() == Material.NETHER_STAR) {
            String main = ChatColor.stripColor(p.getOpenInventory().getTopInventory().getName().split(" ")[0].split("'")[0]);
            p.openInventory(new HistoryMenuGUI(Bukkit.getOfflinePlayer(main)).getInventory());
        } else if (item.getType() == Material.PAPER) {
            String main = ChatColor.stripColor(p.getOpenInventory().getTopInventory().getName().split(" ")[0].split("'")[0]);
            //Left click change category Right Click change order
            //  Date | Duration
            if (type == ClickType.LEFT) {
                int c = item.getItemMeta().getLore().get(1).contains("→") ? 1 : 2;
                int o = item.getItemMeta().getLore().get(2).contains("→") ? 1 : 2;
                //Is 1
                if (c == 1) {
                    p.openInventory(new HistoryGUI(Bukkit.getOfflinePlayer(main).getUniqueId().toString(), punished, 2, o).getInventory());
                } else {
                    p.openInventory(new HistoryGUI(Bukkit.getOfflinePlayer(main).getUniqueId().toString(), punished, 1, o).getInventory());
                }
            }
            if (type == ClickType.RIGHT) {
                int c = item.getItemMeta().getLore().get(1).contains("→") ? 1 : 2;
                int o = item.getItemMeta().getLore().get(2).contains("→") ? 1 : 2;
                //Is 1
                if (o == 1) {
                    p.openInventory(new HistoryGUI(Bukkit.getOfflinePlayer(main).getUniqueId().toString(), punished, c, 2).getInventory());
                } else {
                    p.openInventory(new HistoryGUI(Bukkit.getOfflinePlayer(main).getUniqueId().toString(), punished, c, 1).getInventory());
                }
            }
        }

    }

    @Override
    public Inventory getInventory() {

        return HistoryGUIHandler.addPunishments(this, PunishmentHandler.getPunishments(uuid), name, type, category, order);

    }
}
