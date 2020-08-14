package net.akarian.punish.commands;

import net.akarian.punish.punishment.PunishmentHandler;
import net.akarian.punish.punishment.guis.StaffHistoryMenuGUI;
import net.akarian.punish.utils.Chat;
import net.akarian.punish.utils.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class StaffHistoryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("punish.staffhistory")) {

            if(sender instanceof Player) {

                 Player p = (Player) sender;

                 if(args.length == 1) {
                     String uuid;

                     if (Bukkit.getPlayer(args[0]) != null) {
                         uuid = Bukkit.getPlayer(args[0]).getUniqueId().toString();
                     } else {
                         uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString();
                     }
                     if(PunishmentHandler.getStaffPunishments(uuid).size() == 0) {
                         Chat.sendMessage(sender, "&cThere is no Staff History for that player.");
                     } else p.openInventory(new StaffHistoryMenuGUI(UUID.fromString(uuid)).getInventory());
                 } else if(args.length == 2 && args[1].equalsIgnoreCase("purge")) {
                     List<Punishment> punishments = PunishmentHandler.getStaffPunishments(Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString());

                     Chat.sendRawMessage(p, "&aPurging... Please wait.");

                     for(Punishment pn : punishments) {
                         PunishmentHandler.removePunishment(pn.getId());
                     }

                     Chat.sendRawMessage(p, "&aYou have successfully purged " + args[0] + "'s Staff History.");

                 }

            }

        }
        return false;
    }
}
