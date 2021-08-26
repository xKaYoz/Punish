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

public class HistoryGUIHandler {

    private static String beautify(String s) {
        if (s.equalsIgnoreCase("BAN")) {
            return "Bans";
        } else if (s.equalsIgnoreCase("WARN")) {
            return "Warns";
        } else if (s.equalsIgnoreCase("MUTE")) {
            return "Mutes";
        } else if (s.equalsIgnoreCase("KICK")) {
            return "Kicks";
        }
        return "";
    }

    public static Inventory addPunishments(PunishmentGUI punishmentGUI, ArrayList<Punishment> data, String name, String ptype, int category, int order) {
        Inventory inv = Bukkit.createInventory(punishmentGUI, 54, Chat.format("&c&l" + name + "'s " + beautify(ptype)));
        ArrayList<ItemStack> punishments = new ArrayList<>();

        Set<Punishment> f = new HashSet<>();

        //Category 1 = Date | 2 = Duration
        //Order 1 = Newest First | 2 = Oldest First

        if(category == 1) {
            HashMap<Punishment, Long> temp = new HashMap<>();

            for(Punishment punishment : data) {
                temp.put(punishment, punishment.getStart());
            }
            if(order == 1) {
                // Date Newest First
                f = sortNew(temp).keySet();
            } else if(order == 2) {
                // Date Newest First
                f = sortOld(temp).keySet();
            }
        }

        if(category == 2) {
            HashMap<Punishment, Long> temp = new HashMap<>();
            for(Punishment punishment : data) {
                if(punishment.getEnd() == -1)
                    temp.put(punishment, Long.MAX_VALUE);
                else
                    temp.put(punishment, punishment.getEnd() - punishment.getStart());
            }
            if(order == 1) {
                f = sortNew(temp).keySet();
            } else {
                f = sortOld(temp).keySet();
            }
        }

        for(Punishment p : f) {

            long l = p.getEnd() - p.getStart() + 1;

            String duration = "Permanent";

            if (p.getEnd() != -1) {
                duration = Chat.formatTime((l) / 1000);
            }

            String staffName;
            if (p.getStaff().equalsIgnoreCase("Console")) {
                staffName = "CONSOLE";
            } else {
                staffName = Bukkit.getOfflinePlayer(UUID.fromString(p.getStaff())).getName();
            }

            Date now = new Date(p.getStart());
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

            if(PunishmentType.getType(ptype) == p.getType() || (PunishmentType.getType(ptype) == PunishmentType.BAN && p.getType() == PunishmentType.TEMPBAN)) {

                ItemStack item = ItemBuilder.build(Material.BOOK, 1, "&4" + format.format(now), Arrays.asList("&cStaff: &f" + staffName, "&cReason: &f" + p.getReason(), "&cID:&f " + p.getId(), "&cDuration: &f" + duration, "", "&7Left click for more info.", "&7Shift + Right Click to remove punishment."));

                if (p.isActive()) item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

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
            inv.setItem(t, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.singletonList("")));
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
        for (Iterator it = list.iterator(); it.hasNext();) {
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
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

}
