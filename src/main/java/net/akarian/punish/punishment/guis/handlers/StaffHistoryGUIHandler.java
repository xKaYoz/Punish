package net.akarian.punish.punishment.guis.handlers;

import net.akarian.punish.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.*;

public class StaffHistoryGUIHandler {

    public static Inventory addPunishments(PunishmentGUI punishmentGUI, ArrayList<Punishment> data, UUID uuid, PunishmentType type, int category, int order) {
        String name = Bukkit.getOfflinePlayer(uuid).getName();
        Inventory inv = Bukkit.createInventory(punishmentGUI, 54, Chat.format("&c&l" + name + "'s " + type.getString() + " History"));

        ArrayList<ItemStack> punishments = new ArrayList<>();

        Set<Punishment> f = new HashSet<>();

        if (category == 1) {
            HashMap<Punishment, Long> temp = new HashMap<>();

            for (Punishment punishment : data) {
                temp.put(punishment, punishment.getStart());
            }
            if (order == 1) {
                // Date Newest First
                f = sortNew(temp).keySet();
            } else if (order == 2) {
                // Date Newest First
                f = sortOld(temp).keySet();
            }
        }

        if (category == 2) {
            HashMap<Punishment, Long> temp = new HashMap<>();
            for (Punishment punishment : data) {
                if (punishment.getEnd() == -1)
                    temp.put(punishment, Long.MAX_VALUE);
                else
                    temp.put(punishment, punishment.getEnd() - punishment.getStart());
            }
            if (order == 1) {
                f = sortNew(temp).keySet();
            } else {
                f = sortOld(temp).keySet();
            }
        }

        for (Punishment p : f) {
            long l = p.getEnd() - p.getStart() + 1;

            String duration = "Permanent";

            if (p.getEnd() != -1) {
                duration = Chat.formatTime((l) / 1000);
            }

            String userName = Bukkit.getOfflinePlayer(p.getPunished()).getName();

            Date now = new Date(p.getStart());
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

            if(type == p.getType() || (type == PunishmentType.BAN && p.getType() == PunishmentType.TEMPBAN)) {

                ItemStack item = ItemBuilder.build(Material.BOOK, 1, "&6" + p.getId(), Arrays.asList(
                        "&cPunished User &8- &f" + userName,
                        "&cReason &8- &f" + p.getReason(),
                        "&cDate Punished &8- &f" + format.format(now),
                        "&cDuration &8- &f" + duration,
                        "",
                        "&7Left click for more info.",
                        "&7Shift + Right Click to Purge punishment."));

                if (p.isActive()) {

                    item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

                    ItemMeta tempMeta = item.getItemMeta();
                    List<String> lore = item.getItemMeta().getLore();
                    List<String> newlore = new ArrayList<>();

                    String expires = "&4Never";
                    if (p.getEnd() != -1) {
                        expires = Chat.formatTime((p.getEnd() - System.currentTimeMillis())/1000);
                    }

                    newlore.add(lore.get(0));
                    newlore.add(lore.get(1));
                    newlore.add(lore.get(2));
                    newlore.add(lore.get(3));
                    newlore.add("&cExpires &8- &f" + expires);
                    newlore.add(lore.get(4));
                    newlore.add(lore.get(5));
                    newlore.add(lore.get(6));

                    tempMeta.setLore(Chat.formatList(newlore));
                    item.setItemMeta(tempMeta);

                }

                ItemMeta im = item.getItemMeta();

                im.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                item.setItemMeta(im);

                punishments.add(item);
            }
        }

        //Setting the items to their spots.
        int i = punishments.size() - 1;

        for (ItemStack item : punishments) {
            inv.setItem(i, item);
            i--;
        }

        //Setting the bottom navigator
        for (int t = 45; t <= 53; t++) {
            inv.setItem(t, ItemBuilder.build(Material.STAINED_GLASS_PANE, 1, 7, " ", Collections.singletonList("")));
        }


        inv.setItem(47, ItemBuilder.build(Material.NETHER_STAR, 1, "&6Back to Menu", Collections.singletonList("&7Click to go back to the main menu.")));

        List<String> sortLore = new ArrayList<>();
        sortLore.add("");
        if(category == 1) {
            sortLore.add("        &6→ Date &8| &7Duration");
            if(order == 1) {
                sortLore.add("     &6→ Recent &8| &7Oldest");
            } else {
                sortLore.add("       &7Recent &8| &6Oldest ←");
            }
        } else {
            sortLore.add("          &7Date &8| &6Duration ←");
            if(order == 1) {
                sortLore.add("    &6→ Longest &8| &7Shortest");
            } else {
                sortLore.add("      &7Longest &8| &6Shortest ←");
            }
        }
        sortLore.add("&7Left click to change Category");
        sortLore.add("&7Right click to switch Order.");

        inv.setItem(51, ItemBuilder.build(Material.PAPER, 1, "&6Sort", sortLore));

        return inv;
    }

    private static HashMap<Punishment, Long> sortNew(HashMap<Punishment, Long> map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        list.sort(new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    private static HashMap<Punishment, Long> sortOld(HashMap<Punishment, Long> map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        list.sort(new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

}
