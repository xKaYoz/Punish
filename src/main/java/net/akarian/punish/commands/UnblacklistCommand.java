package net.akarian.punish.commands;

import net.akarian.punish.punishment.PunishmentHandler;
import net.akarian.punish.utils.Chat;
import net.akarian.punish.utils.Files;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnblacklistCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length > 0) {
            if (args[0].replace(".", ",").split(",").length == 4) {
                //Gave an IP
                Files files = new Files();
                FileConfiguration lang = files.getConfig("lang");
                boolean isSilent = args[args.length - 1].equalsIgnoreCase("-s");

                if (PunishmentHandler.unblacklist(args[0]) == 0) {
                    if (!isSilent) {
                        Chat.broadcast(lang.getString("UnBlacklist Message").replace("$ip$", args[0]).replace("$staff$", sender.getName()));
                    } else {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.hasPermission("punish.silent")) {
                                Chat.sendRawMessage(p, lang.getString("Silent UnBlacklist Message").replace("$ip$", args[0]).replace("$staff$", sender.getName()));
                            }
                        }
                    }
                }
            } else {
                //Gave a Name
                Files files = new Files();
                FileConfiguration lang = files.getConfig("lang");
                boolean isSilent = args[args.length - 1].equalsIgnoreCase("-s");
                UUID uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
                String ip = PunishmentHandler.getIP(uuid.toString());

                if (PunishmentHandler.unblacklist(ip) == 0) {
                    if (!isSilent) {
                        Chat.broadcast(lang.getString("UnBlacklist Message").replace("$ip$", args[0]).replace("$staff$", sender.getName()));
                    } else {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.hasPermission("punish.silent")) {
                                Chat.sendRawMessage(p, lang.getString("Silent UnBlacklist Message").replace("$ip$", args[0]).replace("$staff$", sender.getName()));
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
