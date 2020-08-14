package net.akarian.punish.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface PunishmentGUI extends InventoryHolder {

    void onGUIClick(Player p, int slot, ItemStack item, ClickType click, Inventory inv);

}
