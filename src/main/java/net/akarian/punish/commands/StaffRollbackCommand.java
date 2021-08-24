package net.akarian.punish.commands;

import net.akarian.punish.punishment.PunishmentHandler;
import net.akarian.punish.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class StaffRollbackCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (sender.hasPermission("punish.staffrollback")) {

            if (args.length == 2) {

                UUID uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
                String timeType = args[1];
                String[] timeTypeArray;
                int seconds;

                if (timeType.contains("s")) {
                    seconds = 1;
                    timeTypeArray = timeType.split("s");
                } else if (timeType.contains("mo")) {
                    seconds = 60 * 60 * 24 * 30;
                    timeTypeArray = timeType.split("mo");
                } else if (timeType.contains("m")) {
                    seconds = 60;
                    timeTypeArray = timeType.split("m");
                } else if (timeType.contains("h")) {
                    seconds = 60 * 60;
                    timeTypeArray = timeType.split("h");
                } else if (timeType.contains("d")) {
                    seconds = 60 * 60 * 24;
                    timeTypeArray = timeType.split("d");
                } else if (timeType.contains("w")) {
                    seconds = 60 * 60 * 24 * 7;
                    timeTypeArray = timeType.split("w");
                } else {
                    Chat.sendMessage(sender, "&7Incorrect Date Provided. Received " + timeType);
                    return true;
                }
                try {
                    Long.parseLong(timeTypeArray[0]);
                } catch (Exception e) {
                    Chat.sendMessage(sender, "&7" + timeTypeArray[0] + " is not a valid number.");
                    return true;
                }

                long time = (seconds * Long.parseLong(timeTypeArray[0])) * 1000;

                Chat.sendRawMessage(sender, "&aRolling back... Please wait.");
                PunishmentHandler.rollback(uuid, time);
                Chat.sendRawMessage(sender, "&aYou have successfully rolled back " + args[0] + "'s punishments back " + Chat.formatTime(time / 1000) + ".");

            } else {
                Chat.usage(sender, "staffrollback <player> <time>");
            }

        } else {
            Chat.noPermission(sender);
        }

        return false;
    }
}
