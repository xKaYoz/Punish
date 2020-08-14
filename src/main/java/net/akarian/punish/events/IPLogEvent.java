package net.akarian.punish.events;

import net.akarian.punish.punishment.PunishmentHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class IPLogEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        if (!PunishmentHandler.isIPLogged(p.getUniqueId().toString())) {

            PunishmentHandler.logIP(p.getUniqueId().toString());

        }

    }

}
