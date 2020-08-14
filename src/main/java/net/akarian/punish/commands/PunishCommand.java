package net.akarian.punish.commands;

import net.akarian.punish.punishment.guis.PunishGUI;
import net.akarian.punish.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by KaYoz on 12/6/2017.
 * Subscribe to me on Youtube:
 * http://www.youtube.com/c/KaYozMC/
 */

public class PunishCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender.hasPermission("punish.punishgui")) {

            if (sender instanceof Player) {

                Player p = (Player) sender;

                if (args.length == 1) {

                    p.openInventory(new PunishGUI(Bukkit.getOfflinePlayer(args[0]), 1).getInventory());

                } else {
                    Chat.usage(p, "punish <player>");
                }

            } else {
                Chat.sendRawMessage(sender, "&cYou must be a player to execute this command.");
                return true;
            }
        } else {
            Chat.noPermission(sender);
        }

        return true;
    }
}
