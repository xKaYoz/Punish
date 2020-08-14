package net.akarian.punish.punishment.guis;

import net.akarian.punish.punishment.PunishmentHandler;
import net.akarian.punish.utils.Chat;
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
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.UUID;

public class StaffHistoryMenuGUI implements PunishmentGUI {

    UUID staff;

    public StaffHistoryMenuGUI(UUID staff) {
        this.staff = staff;
    }
    public StaffHistoryMenuGUI(){

    }

    @Override
    public void onGUIClick(Player p, int slot, ItemStack item, ClickType click, Inventory inv) {

        OfflinePlayer op = Bukkit.getOfflinePlayer(ChatColor.stripColor(inv.getItem(4).getItemMeta().getDisplayName()));

        if (item.getType() == Material.WOOL) {

            if (item.getItemMeta().getDisplayName().equalsIgnoreCase(Chat.format("&cWarnings"))) {
                p.openInventory(new StaffHistoryGUI(op.getUniqueId(), PunishmentType.WARN, 1, 1).getInventory());
            } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(Chat.format("&cBans"))) {
                p.openInventory(new StaffHistoryGUI(op.getUniqueId(), PunishmentType.BAN,1,1).getInventory());
            } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(Chat.format("&cMutes"))) {
                p.openInventory(new StaffHistoryGUI(op.getUniqueId(), PunishmentType.MUTE,1,1).getInventory());
            } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(Chat.format("&cKicks"))) {
                p.openInventory(new StaffHistoryGUI(op.getUniqueId(), PunishmentType.KICK,1,1).getInventory());
            }

        }
    }

    @Override
    public Inventory getInventory() {
        OfflinePlayer op = Bukkit.getOfflinePlayer(staff);
        String name = op.getName();
        Inventory inv = Bukkit.createInventory(this, 36, Chat.format("&c&l" + name + "'s Staff History"));

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta smeta = (SkullMeta) skull.getItemMeta();

        smeta.setOwner(op.getName());
        smeta.setDisplayName(Chat.format("&e" + op.getName()));
        skull.setItemMeta(smeta);

        inv.setItem(4, skull);

        inv.setItem(19, ItemBuilder.build(Material.WOOL, 1, 14, "&cWarnings", Arrays.asList("&7See all warnings issued by " + name, "&7Total: &e" + PunishmentHandler.getNumStaffWarn(staff.toString()))));
        inv.setItem(21, ItemBuilder.build(Material.WOOL, 1, 15, "&cBans", Arrays.asList("&7See all bans issued by " + name, "&7Total: &e" + PunishmentHandler.getNumStaffBan(staff.toString()))));
        inv.setItem(23, ItemBuilder.build(Material.WOOL, 1, 8, "&cMutes", Arrays.asList("&7See all mutes issued by " + name, "&7Total: &e" + PunishmentHandler.getNumStaffMute(staff.toString()))));
        inv.setItem(25, ItemBuilder.build(Material.WOOL, 1, 4, "&cKicks", Arrays.asList("&7See all kicks issued by " + name, "&7Total: &e" + PunishmentHandler.getNumStaffKick(staff.toString()))));


        return inv;
    }
}
