package net.akarian.punish.events;

import net.akarian.punish.punishment.PunishmentHandler;
import net.akarian.punish.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

/**
 * Created by KaYoz on 12/7/2017.
 * Subscribe to me on Youtube:
 * http://www.youtube.com/c/KaYozMC/
 */

public class BanCheckEvent implements Listener {

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent e) {
        Files files = new Files();
        FileConfiguration lang = files.getConfig("lang");
        String uuid = e.getUniqueId().toString();

        Punishment ban = PunishmentHandler.getBan(uuid);
        Punishment blacklist = PunishmentHandler.getBlacklist(e.getAddress().toString().replace("/", ""));

        if (blacklist != null) {
            String staffName;
            if (blacklist.getStaff().equalsIgnoreCase("Console")) {
                staffName = "CONSOLE";
            } else {
                staffName = Bukkit.getOfflinePlayer(UUID.fromString(blacklist.getStaff())).getName();
            }
            String blacklistMessage = lang.getString("Disconnect Blacklist Message")
                    .replace("$reason$", blacklist.getReason())
                    .replace("$staff$", staffName)
                    .replace("$start$", Chat.formatTime(blacklist.getStart()))
                    .replace("$end$", Chat.formatTime(blacklist.getEnd()))
                    .replace("$id$", blacklist.getId());

            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Chat.format(blacklistMessage));

            if (ban == null) {
                PunishmentHandler.ban(uuid, "CONSOLE", "Blacklist #" + blacklist.getId(), true);
            }

            return;
        }

        if(ban != null) {
            String banMessage;
            String length;
            String staffName;
            if (ban.getStaff().equalsIgnoreCase("Console")) {
                staffName = "CONSOLE";
            } else {
                staffName = NameManager.getName(ban.getStaff());
            }
            if (ban.getEnd() == -1) {
                length = "Permanent";
            } else {
                length = Chat.formatTime((ban.getEnd() - System.currentTimeMillis()) / 1000);
            }
            if(ban.getType() == PunishmentType.TEMPBAN) {
                banMessage = lang.getString("Disconnect TempBan Message").replace("$length$", length)
                        .replace("$reason$", ban.getReason())
                        .replace("$staff$", staffName)
                        .replace("$start$", Chat.formatTime(ban.getStart()))
                        .replace("$end$", Chat.formatTime(ban.getEnd()))
                        .replace("$id$", ban.getId());
            } else {
                banMessage = lang.getString("Disconnect Ban Message").replace("$reason$", ban.getReason())
                        .replace("$staff$", staffName)
                        .replace("$start$", Chat.formatTime(ban.getStart()))
                        .replace("$end$", Chat.formatTime(ban.getEnd()))
                        .replace("$id$", ban.getId());
            }
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Chat.format(banMessage));
        }

    }
}
