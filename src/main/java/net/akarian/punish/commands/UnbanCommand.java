package net.akarian.punish.commands;

import net.akarian.punish.punishment.PunishmentHandler;
import net.akarian.punish.utils.Chat;
import net.akarian.punish.utils.Files;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Created by KaYoz on 12/7/2017.
 * Subscribe to me on Youtube:
 * http://www.youtube.com/c/KaYozMC/
 */

public class UnbanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("punish.unban")) {
            if (args.length == 1 || args.length == 2) {

                String name = args[0];
                OfflinePlayer t = Bukkit.getOfflinePlayer(name);
                String uuid = t.getUniqueId().toString();
                boolean isSilent = args[args.length - 1].equalsIgnoreCase("-s");
                Files files = new Files();
                FileConfiguration lang = files.getConfig("lang");

                if(PunishmentHandler.isBanned(uuid) == 1){
                    //Player is banned.

                    PunishmentHandler.unban(uuid);

                    if (!isSilent) {
                        Chat.broadcast(lang.getString("Unban Message").replace("$player$", name).replace("$staff$", sender.getName()));
                    } else {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.hasPermission("punish.silent")) {
                                Chat.sendRawMessage(p, lang.getString("Silent Unban Message").replace("$player$", name).replace("$staff$", sender.getName()));
                            }
                        }
                    }

                } else {
                    Chat.sendMessage(sender, "&7That player is not currently banned.");
                }
            } else {
                Chat.usage(sender, "unban <player>");
            }
        } else {
            Chat.noPermission(sender);
        }
        return true;
    }
}
