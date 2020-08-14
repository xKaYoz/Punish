package net.akarian.punish.events.GUIEvents;

import net.akarian.punish.Punish;
import net.akarian.punish.punishment.guis.HistoryGUIStart;
import net.akarian.punish.utils.PunishmentGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HistoryGUIStartEvents implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = p.getOpenInventory().getTopInventory();
        ItemStack item = e.getCurrentItem();

        if(inv.getHolder() instanceof PunishmentGUI) {
            e.setCancelled(true);
            if(ChatColor.stripColor(inv.getName()).contains("Punishments")
                    && item != null
                    && item.hasItemMeta())
                new HistoryGUIStart().onGUIClick(p, e.getSlot(), item, e.getClick(), inv);
        }
    }
}
