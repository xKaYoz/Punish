package net.akarian.punish.events.GUIEvents;

import net.akarian.punish.punishment.guis.HistoryMenuGUI;
import net.akarian.punish.utils.PunishmentGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HistoryMenuGUIEvent implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = p.getOpenInventory().getTopInventory();
        ItemStack item = e.getCurrentItem();

        if (inv.getHolder() instanceof PunishmentGUI) {
            e.setCancelled(true);
            if (ChatColor.stripColor(e.getView().getTitle()).contains("Punishments")
                    && item != null
                    && item.hasItemMeta())
                new HistoryMenuGUI().onGUIClick(p, e.getSlot(), item, e.getClick(), inv);
        }
    }
}
