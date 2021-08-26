package net.akarian.punish.punishment.guis;

import net.akarian.punish.punishment.PunishmentHandler;
import net.akarian.punish.punishment.guis.handlers.StaffHistoryGUIHandler;
import net.akarian.punish.utils.ItemBuilder;
import net.akarian.punish.utils.PunishmentGUI;
import net.akarian.punish.utils.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.UUID;

public class StaffHistoryGUI implements PunishmentGUI {

    UUID staff;
    PunishmentType type;
    int category;
    int order;

    public StaffHistoryGUI(UUID uuid, PunishmentType type, int category, int order) {
        this.staff = uuid;
        this.type = type;
        this.category = category;
        this.order = order;
    }

    public StaffHistoryGUI() {
    }

    @Override
    public void onGUIClick(Player p, int slot, ItemStack item, ClickType click, Inventory inv) {

        String[] split = p.getOpenInventory().getTitle().split(" ");
        String main = ChatColor.stripColor(split[0].split("'")[0]);
        String punished = ChatColor.stripColor(split[1].replace("s", ""));

        PunishmentType pType = PunishmentType.getType(punished);
        OfflinePlayer op = Bukkit.getOfflinePlayer(main);

        if (item.getType() == Material.BOOK) {
            String id = ChatColor.stripColor(item.getItemMeta().getDisplayName());

            if (click == ClickType.SHIFT_RIGHT) {

                if (p.hasPermission("punish.staffhistory.purge")) {

                    for (int i = 0; i <= 44; i++) {
                        p.getOpenInventory().setItem(i, ItemBuilder.build(Material.BARRIER, 1, "&cRefreshing...", Collections.emptyList()));
                    }

                    PunishmentHandler.removePunishment(id);
                    p.openInventory(new StaffHistoryGUI(op.getUniqueId(), pType, 1, 1).getInventory());

                }

            } else if (click == ClickType.LEFT) {

                p.performCommand("history " + id);
                p.closeInventory();

            }

        } else if (item.getType() == Material.NETHER_STAR) {

            p.openInventory(new StaffHistoryMenuGUI(op.getUniqueId()).getInventory());

        } else if (item.getType() == Material.PAPER) {

            if (click == ClickType.LEFT) {

                int c = item.getItemMeta().getLore().get(1).contains("→") ? 1 : 2;
                int o = item.getItemMeta().getLore().get(2).contains("→") ? 1 : 2;

                if (c == 1) {
                    p.openInventory(new StaffHistoryGUI(op.getUniqueId(), pType, 2, o).getInventory());
                } else {
                    p.openInventory(new StaffHistoryGUI(op.getUniqueId(), pType, 1, o).getInventory());
                }
            }

            if (click == ClickType.RIGHT) {

                int c = item.getItemMeta().getLore().get(1).contains("→") ? 1 : 2;
                int o = item.getItemMeta().getLore().get(2).contains("→") ? 1 : 2;

                if (o == 1) {
                    p.openInventory(new StaffHistoryGUI(op.getUniqueId(), pType, c, 2).getInventory());
                } else {
                    p.openInventory(new StaffHistoryGUI(op.getUniqueId(), pType, c, 1).getInventory());
                }
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return StaffHistoryGUIHandler.addPunishments(this, PunishmentHandler.getStaffPunishments(staff.toString()), staff, type, category, order);
    }
}
