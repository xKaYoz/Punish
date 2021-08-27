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
 * Created by KaYoz on 12/7/2017.
 */

public class BanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("punish.ban")) {
            if (args.length >= 1) {

                /* Creating our variables. */
                Files files = new Files();
                FileConfiguration lang = files.getConfig("lang");
                FileConfiguration config = Punish.getInstance().getConfig();
                String staff = sender instanceof Player ? ((Player)sender).getUniqueId().toString() : sender.getName();
                boolean isSilent = args[args.length - 1].equalsIgnoreCase("-s");
                String uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString();
                String name = Bukkit.getOfflinePlayer(args[0]).getName();
                StringBuilder reason = new StringBuilder();

                /* Checking if the user provided a reason. If not, grab the default reason. */
                if (args.length == 1) {
                    reason = new StringBuilder(config.getString("DefaultBanReason"));
                } else {
                    for (int i = 1; i < args.length; i++) {
                        reason.append(args[i]).append(" ");
                    }
                }

                /* Banning the player and searching the responding code to the ban. */
                String trim = reason.toString().replace("-s", "").trim();
                switch (PunishmentHandler.ban(uuid, staff, trim, isSilent)) {
                    case 0:
                        if (!isSilent) {
                            Chat.broadcast(lang.getString("Ban Message").replace("$player$", name).replace("$reason$", trim).replace("$staff$", sender.getName()));
                        } else {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (p.hasPermission("punish.silent")) {
                                    Chat.sendRawMessage(p, lang.getString("Silent Ban Message").replace("$player$", name).replace("$reason$", trim).replace("$staff$", sender.getName()));
                                }
                            }
                        }
                        break;
                    case 1:
                        Chat.sendMessage(sender, "&7That player is already banned.");
                        return true;
                    case 2:
                        Chat.sendMessage(sender, "&cAn error has occurred! Please contact an administrator.");
                        return true;

                }
            } else {
                Chat.usage(sender, "ban <player> {reason}");
            }
        } else {
            Chat.noPermission(sender);
        }
        return true;
    }
}
