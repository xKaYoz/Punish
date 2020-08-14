package net.akarian.punish.commands;

import net.akarian.punish.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ChatCommands implements CommandExecutor {

    private static HashMap<UUID, Long> delay = new HashMap<>();
    private static boolean slowed = false;
    private static boolean muted = false;
    private static int slow = 0;

    public static HashMap<UUID, Long> getDelay() {
        return delay;
    }

    public static boolean isSlowed() {
        return slowed;
    }

    public static boolean isMuted() {
        return muted;
    }

    public static int getSlow() {
        return slow;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length >= 1) {

            if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {

                if (sender.hasPermission("punish.chat.clear")) {

                    for (Player p : Bukkit.getOnlinePlayers()) {

                        for (int i = 0; i < 100; i++) {

                            Chat.sendRawMessage(p, " ");

                        }

                        Chat.sendRawMessage(p, "&a&lChat has been cleared by " + sender.getName() + ".");
                        return false;
                    }

                } else {
                    Chat.noPermission(sender);
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("slow")) {

                if (sender.hasPermission("punish.chat.slow")) {

                    if (args.length == 1) {
                        slowed = !slowed;
                        if (!slowed) {
                            Chat.broadcast("&a&lChat delay has been removed by " + sender.getName() + ".");
                        } else {
                            slow = 5;
                            Chat.broadcast("&c&lChat has been slowed by " + sender.getName() + ". &7&o(" + Chat.formatTime(slow) + ")");
                        }
                        return false;
                    }
                    if (args.length == 2) {
                        try {
                            slow = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            Chat.sendMessage(sender, "&c" + args[1] + " is not a valid number.");
                            return false;
                        }
                        slowed = true;
                        Chat.broadcast("&c&lChat has been slowed by " + sender.getName() + ". &7&o(" + Chat.formatTime(slow) + ")");
                        return false;
                    }
                } else {
                    Chat.noPermission(sender);
                    return false;
                }

            } else if (args.length == 1 && args[0].equalsIgnoreCase("mute")) {

                if (sender.hasPermission("punish.chat.mute")) {

                    if (muted) {

                        Chat.broadcast("&c&lChat has been unmuted by " + sender.getName() + ".");
                        muted = false;

                    } else {

                        Chat.broadcast("&c&lChat has been muted by " + sender.getName() + ".");
                        muted = true;

                    }
                } else {
                    Chat.noPermission(sender);
                }
                return false;

            }
        }
        Chat.line(sender);
        Chat.sendRawMessage(sender, "&c&lChat &f&lHelp Menu");
        Chat.blankMessage(sender);
        Chat.sendRawMessage(sender, "&c/chat slow &7&o[seconds] &8- &fSlow chat for default or specified seconds.");
        Chat.sendRawMessage(sender, "&c/chat clear &8- &fClear the chat.");
        Chat.sendRawMessage(sender, "&c/chat mute &8- &fMute the chat.");
        Chat.line(sender);

        return false;
    }
}
