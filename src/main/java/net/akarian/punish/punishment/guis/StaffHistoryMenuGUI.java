package net.akarian.punish.punishment.guis;

import net.akarian.punish.punishment.PunishmentHandler;
import net.akarian.punish.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

        switch (item.getType()) {
            case RED_WOOL:
                p.openInventory(new StaffHistoryGUI(op.getUniqueId(), PunishmentType.WARN, 1, 1).getInventory());
                break;
            case BLACK_WOOL:
                p.openInventory(new StaffHistoryGUI(op.getUniqueId(), PunishmentType.BAN, 1, 1).getInventory());
                break;
            case LIGHT_GRAY_WOOL:
                p.openInventory(new StaffHistoryGUI(op.getUniqueId(), PunishmentType.MUTE, 1, 1).getInventory());
                break;
            case YELLOW_WOOL:
                p.openInventory(new StaffHistoryGUI(op.getUniqueId(), PunishmentType.KICK, 1, 1).getInventory());
                break;
        }
    }

    @Override
    public Inventory getInventory() {
        OfflinePlayer op = Bukkit.getOfflinePlayer(staff);
        String name = NameManager.getName(op.getUniqueId());
        Inventory inv = Bukkit.createInventory(this, 36, Chat.format("&c&l" + name + "'s Staff History"));

        inv.setItem(4, ItemBuilder.getPlayerHead(op.getUniqueId()));

        inv.setItem(19, ItemBuilder.build(Material.RED_WOOL, 1, "&cWarnings", Arrays.asList("&7See all warnings issued by " + name, "&7Total: &e" + PunishmentHandler.getNumStaffWarn(staff.toString()))));
        inv.setItem(21, ItemBuilder.build(Material.BLACK_WOOL, 1, "&cBans", Arrays.asList("&7See all bans issued by " + name, "&7Total: &e" + PunishmentHandler.getNumStaffBan(staff.toString()))));
        inv.setItem(23, ItemBuilder.build(Material.LIGHT_GRAY_WOOL, 1, "&cMutes", Arrays.asList("&7See all mutes issued by " + name, "&7Total: &e" + PunishmentHandler.getNumStaffMute(staff.toString()))));
        inv.setItem(25, ItemBuilder.build(Material.YELLOW_WOOL, 1, "&cKicks", Arrays.asList("&7See all kicks issued by " + name, "&7Total: &e" + PunishmentHandler.getNumStaffKick(staff.toString()))));


        return inv;
    }
}
