package net.akarian.punish.events;

import net.akarian.punish.commands.ChatCommands;
import net.akarian.punish.utils.Chat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatCommandManager implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        if (ChatCommands.isMuted()) {
            if (!e.getPlayer().hasPermission("punish.chat.mute.bypass")) {
                e.setCancelled(true);
                Chat.sendRawMessage(e.getPlayer(), "&c&lChat is muted.");
            }
        } else if (ChatCommands.isSlowed()) {
            if (!e.getPlayer().hasPermission("punish.chat.slow.bypass")) {
                if (ChatCommands.getDelay().containsKey(e.getPlayer().getUniqueId())) {

                    long left = ChatCommands.getDelay().get(e.getPlayer().getUniqueId()) - System.currentTimeMillis();

                    if (left <= 0) {
                        //Allow person to chat, then add them to the delay.
                        ChatCommands.getDelay().remove(e.getPlayer().getUniqueId());
                        ChatCommands.getDelay().put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + (ChatCommands.getSlow() * 1000));
                    } else {
                        e.setCancelled(true);
                        Chat.sendRawMessage(e.getPlayer(), "&c&lChat is slowed. You have " + Chat.formatTime(left / 1000) + " left before you can chat again.");
                    }

                } else {
                    ChatCommands.getDelay().put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + (ChatCommands.getSlow() * 1000));
                }
            }
        }

    }

}
