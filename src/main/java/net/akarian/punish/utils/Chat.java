package net.akarian.punish.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Chat {

    private static Files files = new Files();
    private static FileConfiguration lang = files.getConfig("lang");

    private final static String prefix = lang.getString("Prefix");

    public static String format(String m) {
        return ChatColor.translateAlternateColorCodes('&', m);
    }

    public static void sendRawMessage(CommandSender p, String str) {
        if (!(p instanceof Player))
            System.out.println(format(str));
        else
            p.sendMessage(format(str));
    }

    public static void noPermission(CommandSender sender) {
        sender.sendMessage(format("&cNo Permission"));
    }

    public static void broadcast(String message) {
        Bukkit.broadcastMessage(format(message));
    }

    public static void usage(CommandSender sender, String m) {
        sender.sendMessage(format("&cIncorrect Usage: /" + m));
    }

    public static void sendMessage(CommandSender sender, String m) {
        sender.sendMessage(format(prefix + " &8» &7" + m));
    }

    public static void log(String str, boolean players) {
        System.out.println(format(prefix + " Punish Debug &7» &e" + str));

        if (players) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOp()) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    p.sendMessage(Chat.format("&8[&e" + dtf.format(now) + "&8] [" + prefix + " Punish Debug&8] &e" + str));
                }
            }
        }
    }

    public static void line(CommandSender sender) {
        sendRawMessage(sender, "&8&l&m-----------------------------------------------------");
    }

    public static List<String> formatList(List<String> list) {
        List<String> Format = new ArrayList();
        if (list == null) return null;
        for (java.lang.String String : list) {
            Format.add(format(String));
        }
        return Format;
    }

    public static void blankMessage(CommandSender sender) {
        sender.sendMessage(" ");
    }

    public static String formatTime(long se) {
        long delay = se;
        String s = "";
        if (delay >= 86400) {
            long days = delay / 86400;
            if (days >= 2) s += days + "d ";
            else s += "1d ";
            delay = delay - (days * 86400);

            if (delay >= 3600) {
                long hours = delay / 3600;
                if (hours >= 2) s += hours + "h ";
                else s += "1h ";

                delay = delay - (hours * 3600);

                if (delay >= 60) {
                    long minutes = delay / 60;
                    if (minutes >= 2) s += minutes + "m ";
                    else s += "1m";

                    delay = delay - (minutes * 60);

                    if (delay >= 1) {
                        long seconds = delay;
                        if (seconds >= 2) s += seconds + "s ";
                        else s += "1s ";
                    } else {
                        s += "0s ";
                    }
                } else {

                    s += "0s ";

                    if (delay >= 1) {
                        long seconds = delay;
                        if (seconds >= 2) s += seconds + "s ";
                        else s += "1s ";
                    } else {
                        s += "0s ";
                    }

                }

            } else {

                s += "0h ";

                if (delay >= 60) {
                    long minutes = delay / 60;
                    if (minutes >= 2) s += minutes + "m ";
                    else s += "1m";

                    delay = delay - (minutes * 60);

                    if (delay >= 1) {
                        long seconds = delay;
                        if (seconds >= 2) s += seconds + "s ";
                        else s += "1s ";
                    } else {
                        s += "0s ";
                    }
                } else {

                    s += "0m ";

                    if (delay >= 1) {
                        long seconds = delay;
                        if (seconds >= 2) s += seconds + "s ";
                        else s += "1s ";
                    } else {
                        s += "0s ";
                    }

                }

            }

        } else {
            if (delay >= 3600) {

                long hours = delay / 3600;
                if (hours >= 2) s += hours + "h ";
                else s += "1h ";

                delay = delay - (hours * 3600);

                if (delay >= 60) {
                    long minutes = delay / 60;
                    if (minutes >= 2) s += minutes + "m ";
                    else s += "1m ";

                    delay = delay - (minutes * 60);

                    if (delay >= 1) {
                        long seconds = delay;
                        if (seconds >= 2) s += seconds + "s ";
                        else s += "1s ";
                    } else {
                        s += "0s ";
                    }
                } else {
                    if (delay >= 1) {
                        long seconds = delay;
                        if (seconds >= 2) s += seconds + "s ";
                        else s += "1s ";
                    } else {
                        s += "0s ";
                    }
                }
            } else {
                if (delay >= 60) {
                    long minutes = delay / 60;
                    if (minutes >= 2) s += minutes + "m ";
                    else s += "1m ";

                    delay = delay - (minutes * 60);

                    if (delay >= 1) {
                        long seconds = delay;
                        if (seconds >= 2) s += seconds + "s ";
                        else s += "1s ";
                    } else {
                        s += "0s ";
                    }
                } else {
                    if (delay >= 1) {
                        long seconds = delay;
                        if (seconds >= 2) s += seconds + "s ";
                        else s += "1s ";
                    } else {
                        s += "0s ";
                    }
                }
            }
        }
        return s.trim();
    }

}
