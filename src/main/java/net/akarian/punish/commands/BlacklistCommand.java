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

import java.util.UUID;

public class BlacklistCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length > 0) {
            if (args[0].replace(".", ",").split(",").length == 4) {
                //An IP is provided
                Files files = new Files();
                FileConfiguration lang = files.getConfig("lang");
                FileConfiguration config = Punish.getInstance().getConfig();
                boolean isSilent = args[args.length - 1].equalsIgnoreCase("-s");
                String staff = sender instanceof Player ? ((Player) sender).getUniqueId().toString() : sender.getName();
                StringBuilder reason = new StringBuilder();

                /* Checking if the user provided a reason. If not, grab the default reason. */
                if (args.length == 1) {
                    reason = new StringBuilder(config.getString("DefaultBlacklistReason"));
                } else {
                    for (int i = 1; i < args.length; i++) {
                        reason.append(args[i]).append(" ");
                    }
                }

                switch (PunishmentHandler.blacklist(args[0], staff, reason.toString())) {
                    case (0):
                        if (!isSilent) {
                            Chat.broadcast(lang.getString("Blacklist Message").replace("$ip$", args[0]).replace("$reason$", reason.toString().replace("-s", "").trim()).replace("$staff$", sender.getName()));
                        } else {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (p.hasPermission("punish.silent")) {
                                    Chat.sendRawMessage(p, lang.getString("Silent Blacklist Message").replace("$ip$", args[0]).replace("$reason$", reason.toString().replace("-s", "").trim()).replace("$staff$", sender.getName()));
                                }
                            }
                        }
                        break;
                    case 1:
                        Chat.sendMessage(sender, "&7That IP is already blacklisted.");
                        return true;
                    case 2:
                        Chat.sendMessage(sender, "&cAn error has occurred! Please contact an administrator.");
                        return true;
                }

            } else {
                //A Name is provided
                UUID uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();

                if (PunishmentHandler.isIPLogged(uuid.toString())) {
                    String ip = PunishmentHandler.getIP(uuid.toString());

                    if(ip.equalsIgnoreCase("")){
                        Chat.sendMessage(sender, "&cWe cannot find an IP associated with this account. Please enter the IP manually.");
                        return false;
                    }

                    Files files = new Files();
                    FileConfiguration lang = files.getConfig("lang");
                    FileConfiguration config = Punish.getInstance().getConfig();
                    boolean isSilent = args[args.length - 1].equalsIgnoreCase("-s");
                    String staff = sender instanceof Player ? ((Player) sender).getUniqueId().toString() : sender.getName();
                    StringBuilder reason = new StringBuilder();

                    /* Checking if the user provided a reason. If not, grab the default reason. */
                    if (args.length == 1) {
                        reason = new StringBuilder(config.getString("DefaultBlacklistReason"));
                    } else {
                        for (int i = 1; i < args.length; i++) {
                            reason.append(args[i]).append(" ");
                        }
                    }

                    switch (PunishmentHandler.blacklist(ip, staff, reason.toString())) {
                        case (0):
                            String trim = reason.toString().replace("-s", "").trim();
                            if (!isSilent) {
                                Chat.broadcast(lang.getString("Blacklist Message").replace("$ip$", ip).replace("$reason$", trim).replace("$staff$", sender.getName()));
                            } else {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (p.hasPermission("punish.silent")) {
                                        Chat.sendRawMessage(p, lang.getString("Blacklist Message").replace("$ip$", args[0]).replace("$reason$", trim).replace("$staff$", sender.getName()));
                                    }
                                }
                            }
                            break;
                        case 1:
                            Chat.sendMessage(sender, "&7That IP is already blacklisted.");
                            return true;
                        case 2:
                            Chat.sendMessage(sender, "&cAn error has occurred! Please contact an administrator.");
                            return true;
                    }
                }
            }
        }
        return false;
    }
}
