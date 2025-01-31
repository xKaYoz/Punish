package net.akarian.punish.commands;

import net.akarian.punish.Punish;
import net.akarian.punish.punishment.PunishmentHandler;
import net.akarian.punish.utils.Chat;
import net.akarian.punish.utils.Files;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Created by KaYoz on 12/6/2017.
 * Subscribe to me on Youtube:
 * http://www.youtube.com/c/KaYozMC/
 */

public class TempBanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("punish.tempban")) {
            if (args.length >= 2) {

                /* Creating our variables. */
                Files files = new Files();
                FileConfiguration lang = files.getConfig("lang");
                FileConfiguration config = Punish.getInstance().getConfig();
                String staff = sender instanceof Player ? ((Player) sender).getUniqueId().toString() : sender.getName();
                boolean isSilent = args[args.length - 1].equalsIgnoreCase("-s");
                String uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString();
                String name = Bukkit.getOfflinePlayer(args[0]).getName();
                String timeType = args[1];
                String[] timeTypeArray;
                StringBuilder reason = new StringBuilder();
                int seconds;

                /* Checking if the user provided a reason. If not, grad the default reason. */
                if (args.length == 2) {
                    reason = new StringBuilder(config.getString("DefaultBanReason"));
                } else {
                    for (int i = 2; i < args.length; i++) {
                        reason.append(args[i]).append(" ");
                    }
                }

                /* Selecting the time frame that the user specified and verifying that the time is a valid number. */
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

                /* Determining how long the player will be banned for. */
                long time = System.currentTimeMillis() + ((seconds * Long.parseLong(timeTypeArray[0]) * 1000));

                /* Banning the player and searching the responding code to the ban. */
                String trim = reason.toString().replace("-s", "").trim();
                switch (PunishmentHandler.tempBan(uuid, staff, trim, time, isSilent)) {
                    case 0:
                        if (!isSilent) {
                            Chat.broadcast(lang.getString("TempBan Message").replace("$player$", name).replace("$reason$", trim).replace("$staff$", sender.getName()));
                        } else {
                            Chat.sendRawMessage(Bukkit.getConsoleSender(), "&7[SILENT] &a" + name + " has been banned from the server for \"" + trim + "\" by " + sender.getName() + ".");
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (p.hasPermission("punish.silent")) {
                                    Chat.sendRawMessage(p, lang.getString("Silent TempBan Message").replace("$player$", name).replace("$reason$", trim).replace("$staff$", sender.getName()));
                                }
                            }
                        }
                        break;
                    case 1:
                        Chat.sendRawMessage(sender, "&7That player is already banned.");
                        break;
                    case 2:
                        Chat.sendMessage(sender, "&cAn error has occurred! Please contact an administrator.");
                        break;
                }
            } else {
                Chat.usage(sender, "tempban <player> <time> <reason>");
            }
        } else {
            Chat.noPermission(sender);
        }
        return true;
    }
}
