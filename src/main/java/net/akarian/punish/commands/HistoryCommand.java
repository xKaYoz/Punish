package net.akarian.punish.commands;

import net.akarian.punish.punishment.PunishmentHandler;
import net.akarian.punish.punishment.guis.HistoryMenuGUI;
import net.akarian.punish.utils.Chat;
import net.akarian.punish.utils.NameManager;
import net.akarian.punish.utils.Punishment;
import net.akarian.punish.utils.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HistoryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender.hasPermission("punish.history")) {

            if (sender instanceof Player) {

                Player p = (Player) sender;

                if (args.length == 1) {

                    //Check if it is an ID

                    Punishment punishment = PunishmentHandler.getPunishment(args[0]);

                    //If not
                    if (punishment == null) {
                        String uuid;

                        if (Bukkit.getPlayer(args[0]) != null) {
                            uuid = Bukkit.getPlayer(args[0]).getUniqueId().toString();
                        } else {
                            uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString();
                        }

                        if (PunishmentHandler.getPunishments(uuid).size() == 0) {
                            Chat.sendMessage(sender, "&cThere is no Punishment History for that player.");
                        } else p.openInventory(new HistoryMenuGUI(Bukkit.getOfflinePlayer(args[0])).getInventory());
                    } else {
                        String duration = "&4Permanent";
                        String remaining = "&4Never";
                        String staffName = "CONSOLE";
                        long l = punishment.getEnd() - punishment.getStart() + 1;
                        if (punishment.getEnd() != -1) {
                            duration = Chat.formatTime((l) / 1000);
                            remaining = Chat.formatTime((punishment.getEnd() - System.currentTimeMillis()) / 1000);
                        }
                        if (!punishment.getStaff().equalsIgnoreCase("console")) {
                            staffName = Bukkit.getOfflinePlayer(UUID.fromString(punishment.getStaff())).getName();
                        }

                        Chat.line(p);
                        Chat.sendRawMessage(p, "&cPunishment information for ID &f" + punishment.getId() + "&c.");
                        Chat.blankMessage(p);
                        if(punishment.getType() == PunishmentType.BLACKLIST) {
                            Chat.sendRawMessage(p, "&cPunished IP&8 - &f" + punishment.getIp());
                            Chat.sendRawMessage(p, "&c# Accounts Associated&8 - &f" + PunishmentHandler.getPlayersFromIP(punishment.getIp()).size());
                        } else
                            Chat.sendRawMessage(p, "&cPunished Player&8 - &f" + NameManager.getName(punishment.getPunished()) + " (" + punishment.getPunished() + ")");
                        Chat.sendRawMessage(p, "&cPunishment Type&8 - &f" + punishment.getType().getString());
                        Chat.sendRawMessage(p, "&cStaff&8 - &f" + staffName + (!punishment.getStaff().equalsIgnoreCase("console") ? " (" + punishment.getStaff() + ")" : ""));
                        Chat.sendRawMessage(p, "&cReason&8 - &f" + punishment.getReason());
                        if (punishment.getType() != PunishmentType.KICK) {
                            Chat.sendRawMessage(p, "&cActive&8 - &f" + (punishment.isActive() ? "&aTrue" : "&cFalse"));
                            Chat.sendRawMessage(p, "&cDuration&8 - &f" + duration);
                            if (punishment.isActive()) {
                                Chat.sendRawMessage(p, "&cExpires&8 - &f" + remaining);
                            }
                        }
                        Chat.line(p);
                    }
                    return true;
                } else if (args.length == 2 && args[1].equalsIgnoreCase("purge")) {

                    if (sender.hasPermission("punish.history.purge")) {

                        Chat.sendRawMessage(p, "&aPurging... Please wait.");

                        PunishmentHandler.purge(Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString());

                        Chat.sendRawMessage(p, "&aYou have successfully purged " + args[0] + "'s punishment history.");
                    } else {
                        Chat.noPermission(sender);
                    }

                } else {
                    Chat.usage(p, "history <player/id> [purge]");
                }

            } else {
                Chat.sendMessage(sender, "&cYou must be a player to execute this command.");
                return true;
            }
        } else {
            Chat.noPermission(sender);
        }

        return true;
    }
}
