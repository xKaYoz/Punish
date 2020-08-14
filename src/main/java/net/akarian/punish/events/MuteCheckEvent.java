package net.akarian.punish.events;

import net.akarian.punish.punishment.PunishmentHandler;
import net.akarian.punish.utils.Chat;
import net.akarian.punish.utils.Files;
import net.akarian.punish.utils.Punishment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MuteCheckEvent implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();
        String uuid = p.getUniqueId().toString();
        Punishment mute = PunishmentHandler.getMute(uuid);
        Files files = new Files();
        FileConfiguration lang = files.getConfig("lang");

        if(mute != null) {
            String length;
            if (mute.getEnd() == -1) {
                length = "Permanent";
            } else {
                length = Chat.formatTime((mute.getEnd() - System.currentTimeMillis()) / 1000);
            }

            Chat.sendRawMessage(p, lang.getString("Mute Chat Message").replace("$reason$", mute.getReason())
                    .replace("$staff$", mute.getStaff())
                    .replace("$start$", Chat.formatTime(mute.getStart()))
                    .replace("$end$", Chat.formatTime(mute.getEnd()))
                    .replace("$id$", mute.getId())
                    .replace("$length$", length));

            e.setCancelled(true);
        }
    }
}
