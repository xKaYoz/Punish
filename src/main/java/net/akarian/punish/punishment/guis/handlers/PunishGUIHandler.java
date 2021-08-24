package net.akarian.punish.punishment.guis.handlers;

import net.akarian.punish.Punish;
import net.akarian.punish.utils.Files;
import net.akarian.punish.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by KaYoz on 12/9/2017.
 * Subscribe to me on Youtube:
 * http://www.youtube.com/c/KaYozMC/
 */

public class PunishGUIHandler {

    private static final ArrayList<String> punishments = new ArrayList<>();

    public static void loadPunishments() {
        Files files = new Files();
        if (files.getFile("guiconfig") == null) {
            Punish.getInstance().saveGuiConfig();
        }
        YamlConfiguration config = files.getConfig("guiconfig");

        Set<String> p = config.getKeys(false);

        punishments.addAll(p);

    }

    public static ItemStack getBook(String p) {
        Files files = new Files();
        if (files.getConfig("guiconfig") == null) {
            Punish.getInstance().saveGuiConfig();
        }
        YamlConfiguration config = files.getConfig("guiconfig");

        String name = config.getString(p + ".Display Name");
        List<String> lore = new ArrayList<>();
        lore.add("&8Category: " + p);
        lore.addAll(config.getStringList(p + ".Lore"));

        return ItemBuilder.build(Material.BOOK, 1, name, lore);
    }

    public static ItemStack getTier(String p, int tier) {
        Files files = new Files();
        if (files.getConfig("guiconfig") == null) {
            Punish.getInstance().saveGuiConfig();
        }
        YamlConfiguration config = files.getConfig("guiconfig");

        String name = config.getString(p + ".Tier " + tier + ".Display Name");
        List<String> lore = config.getStringList(p + ".Tier " + tier + ".Lore");
        int type = 0;

        if (tier == 1) {
            type = 5;
        } else if (tier == 2) {
            type = 4;
        } else if (tier == 3) {
            type = 1;
        } else if (tier == 4) {
            type = 14;
        }

        return ItemBuilder.build(Material.STAINED_GLASS_PANE, 1, type, name, lore);
    }

    public static ArrayList<String> getPunishments() {
        return punishments;
    }
}
