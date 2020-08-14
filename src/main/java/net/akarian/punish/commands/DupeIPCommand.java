package net.akarian.punish.commands;

import net.akarian.punish.Punish;
import net.akarian.punish.punishment.PunishmentHandler;
import net.akarian.punish.utils.Chat;
import net.akarian.punish.utils.MySQL;
import net.akarian.punish.utils.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DupeIPCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.hasPermission("punish.dupeip")) {
                if (args.length == 1) {

                    if (args[0].replace(".", ",").split(",").length == 4) {
                        //It is an IP

                        try {

                            MySQL sql = Punish.getInstance().getMySQL();

                            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getIpTable() + " WHERE IP=?");

                            statement.setString(1, args[0]);

                            ResultSet results = statement.executeQuery();

                            List<String> uuids = new ArrayList<>();

                            while (results.next()) {
                                uuids.add(results.getString(1));
                            }

                            Chat.line(p);
                            Chat.sendRawMessage(p, "&e" + uuids.size() + " Accounts(s) have been associated with this IP. (&cBanned &aOnline &7Offline&e)");
                            for (String s : uuids) {

                                String name = Bukkit.getOfflinePlayer(UUID.fromString(s)).getName();

                                if (PunishmentHandler.isBanned(s) == 1) {
                                    Chat.sendRawMessage(p, "&c" + name);
                                } else if (Bukkit.getPlayer(UUID.fromString(s)) == null) {
                                    Chat.sendRawMessage(p, "&7" + name);
                                } else {
                                    Chat.sendRawMessage(p, "&a" + name);
                                }
                            }
                            Chat.line(p);

                        } catch (SQLException e) {
                            e.printStackTrace();
                            Chat.sendMessage(p, "Error");
                        }

                    } else {

                        String uuid;

                        if (Bukkit.getPlayerExact(args[0]) != null) {
                            uuid = Bukkit.getPlayerExact(args[0]).getUniqueId().toString();
                        } else {
                            uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString();
                        }

                        try {

                            MySQL sql = Punish.getInstance().getMySQL();

                            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getIpTable() + " WHERE UUID=?");

                            statement.setString(1, uuid);

                            ResultSet results = statement.executeQuery();

                            List<String> ips = new ArrayList<>();

                            while (results.next()) {

                                ips.add(results.getString(2));

                            }

                            Chat.line(p);
                            Chat.sendRawMessage(p, "&e" + ips.size() + " IP(s) have been associated with this account.");
                            for (String s : ips) {
                                new FancyMessage(Chat.format("&e" + s)).tooltip(Chat.format("&aClick to see all accounts associated with this IP.")).command("/dupeip " + s).send(p);
                            }
                            Chat.sendRawMessage(p, "&aClick on an IP to see the accounts associated with it.");
                            Chat.line(p);


                        } catch (SQLException e) {
                            e.printStackTrace();
                            Chat.sendMessage(p, "Error.");
                        }
                    }

                } else {
                    Chat.usage(p, "dupeip <player/IP>");
                }

            } else {
                Chat.noPermission(p);
            }
        }

        return true;
    }
}
