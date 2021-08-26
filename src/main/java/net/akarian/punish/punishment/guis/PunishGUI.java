package net.akarian.punish.punishment.guis;

import net.akarian.punish.Punish;
import net.akarian.punish.punishment.guis.handlers.PunishGUIHandler;
import net.akarian.punish.utils.Chat;
import net.akarian.punish.utils.Files;
import net.akarian.punish.utils.ItemBuilder;
import net.akarian.punish.utils.PunishmentGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.List;

/**
 * Created by KaYoz on 12/7/2017.
 * Subscribe to me on Youtube:
 * http://www.youtube.com/c/KaYozMC/
 */

public class PunishGUI implements PunishmentGUI {

    OfflinePlayer punish;
    int page;

    public PunishGUI(OfflinePlayer punish, int page){
        this.punish = punish;
        this.page = page;
    }

    //Use for Events
    public PunishGUI(){

    }

    @Override
    public void onGUIClick(Player p, int slot, ItemStack item, ClickType click, Inventory inv) {

        OfflinePlayer op = Bukkit.getOfflinePlayer(ChatColor.stripColor(inv.getItem(4).getItemMeta().getDisplayName()));

        Files files = new Files();
        if (files.getConfig("guiconfig") == null) {
            Punish.getInstance().saveGuiConfig();
        }
        YamlConfiguration config = files.getConfig("guiconfig");
        String punishment;


        if (!item.hasItemMeta()) return;
        if (!item.getItemMeta().hasDisplayName()) return;

        switch (item.getType()) {
            case NETHER_STAR:
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(Chat.format("&eNext Page"))) {
                    String name = ChatColor.stripColor(p.getOpenInventory().getTitle());
                    String[] strs = name.split(" ");
                    int page = Integer.parseInt(strs[strs.length - 1]);

                    page++;

                    p.closeInventory();

                    p.openInventory(new PunishGUI(op, page).getInventory());
                } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(Chat.format("&ePrevious Page"))) {
                    String name = ChatColor.stripColor(p.getOpenInventory().getTitle());
                    String[] strs = name.split(" ");
                    int page = Integer.parseInt(strs[strs.length - 1]);

                    page--;

                    p.closeInventory();

                    p.openInventory(new PunishGUI(op, page).getInventory());
                }
                break;
            case LIME_STAINED_GLASS_PANE:
                punishment = ChatColor.stripColor(inv.getItem(slot - 9).getItemMeta().getLore().toArray()[0].toString().split(" ")[1]);
                Chat.broadcast(punishment);
                if (config.contains(punishment + ".Tier 1.Player Commands")) {
                    for (String s : config.getStringList(punishment + ".Tier 1.Player Commands")) {
                        Bukkit.dispatchCommand(p, s.replace("%player%", op.getName()));
                    }
                }
                if (config.contains(punishment + ".Tier 1.Console Commands")) {
                    for (String s : config.getStringList(punishment + ".Tier 1.Console Commands")) {
                        if (!s.equalsIgnoreCase("none")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", op.getName()));
                        }
                    }
                }
                break;
            case YELLOW_STAINED_GLASS_PANE:
                punishment = ChatColor.stripColor(inv.getItem(slot - 18).getItemMeta().getDisplayName());
                if (config.contains(punishment + ".Tier 2.Player Commands")) {
                    for (String s : config.getStringList(punishment + ".Tier 2.Player Commands")) {
                        Bukkit.dispatchCommand(p, s.replace("%player%", op.getName()));
                    }
                }
                if (config.contains(punishment + ".Tier 2.Console Commands")) {
                    for (String s : config.getStringList(punishment + ".Tier 2.Console Commands")) {
                        if (!s.equalsIgnoreCase("none")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", op.getName()));
                        }
                    }
                }
                break;
            case ORANGE_STAINED_GLASS_PANE:
                punishment = ChatColor.stripColor(inv.getItem(slot - 27).getItemMeta().getDisplayName());
                if (config.contains(punishment + ".Tier 3.Player Commands")) {
                    for (String s : config.getStringList(punishment + ".Tier 3.Player Commands")) {
                        Bukkit.dispatchCommand(p, s.replace("%player%", op.getName()));
                    }
                }
                if (config.contains(punishment + ".Tier 3.Console Commands")) {
                    for (String s : config.getStringList(punishment + ".Tier 3.Console Commands")) {
                        if (!s.equalsIgnoreCase("none")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", op.getName()));
                        }
                    }
                }
                break;
            case RED_STAINED_GLASS_PANE:
                punishment = ChatColor.stripColor(inv.getItem(slot - 36).getItemMeta().getDisplayName());
                if (config.contains(punishment + ".Tier 4.Player Commands")) {
                    for (String s : config.getStringList(punishment + ".Tier 4.Player Commands")) {
                        Bukkit.dispatchCommand(p, s.replace("%player%", op.getName()));
                    }
                }
                if (config.contains(punishment + ".Tier 4.Console Commands")) {
                    for (String s : config.getStringList(punishment + ".Tier 4.Console Commands")) {
                        if (!s.equalsIgnoreCase("none")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", op.getName()));
                        }
                    }
                }
                break;
        }
    }

    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, 54, Chat.format("&c&lPunish " + punish.getName() + " page " + page));

        List<String> punishments = PunishGUIHandler.getPunishments();
        int row = 10;
        int end = page * 7;
        int start = end - 7;

        //7 on one page

        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, "&6Previous Page", Collections.singletonList("&7Go to the previous page."));
            inv.setItem(45, previous);
        }
        if (punishments.size() > end) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, "&6Next Page", Collections.singletonList("&7Go to the next page."));
            inv.setItem(53, next);
        }

        ItemStack skull = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (short) 3);
        SkullMeta smeta = (SkullMeta) skull.getItemMeta();

        smeta.setOwner(punish.getName());
        smeta.setDisplayName(Chat.format("&e" + punish.getName()));
        skull.setItemMeta(smeta);

        for (int i = start; i < end; i++) {
            if (punishments.size() == i) break;
            int place = row;
            ItemStack book = PunishGUIHandler.getBook(punishments.get(i));
            ItemStack t1 = PunishGUIHandler.getTier(punishments.get(i), 1);
            ItemStack t2 = PunishGUIHandler.getTier(punishments.get(i), 2);
            ItemStack t3 = PunishGUIHandler.getTier(punishments.get(i), 3);
            ItemStack t4 = PunishGUIHandler.getTier(punishments.get(i), 4);

            inv.setItem(place, book);
            place += 9;
            inv.setItem(place, t1);
            place += 9;
            inv.setItem(place, t2);
            place += 9;
            inv.setItem(place, t3);
            place += 9;
            inv.setItem(place, t4);

            row++;

        }

        inv.setItem(4, skull);

        return inv;
    }
}
