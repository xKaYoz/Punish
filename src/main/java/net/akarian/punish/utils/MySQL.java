package net.akarian.punish.utils;

import lombok.Getter;
import lombok.Setter;
import net.akarian.punish.Punish;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class MySQL {

    @Getter
    @Setter
    private Connection connection;
    @Getter
    private String host, database, username, password, banTable, muteTable, warnTable, kickTable, ipTable, blTable;
    @Getter
    private int port;
    @Getter
    private long syncTime;

    public boolean setup() {
        FileConfiguration config = Punish.getInstance().getConfig();

        host = config.getString("MYSQL.host");
        database = config.getString("MYSQL.database");
        username = config.getString("MYSQL.username");
        password = config.getString("MYSQL.password");
        banTable = "punish_" + config.getString("MYSQL.banTable");
        muteTable = "punish_" + config.getString("MYSQL.muteTable");
        warnTable = "punish_" + config.getString("MYSQL.warnTable");
        kickTable = "punish_" + config.getString("MYSQL.kickTable");
        ipTable = "punish_" + config.getString("MYSQL.ipTable");
        blTable = "punish_" + config.getString("MYSQL.blacklistTable");
        port = config.getInt("MYSQL.port");

        CommandSender sender = Bukkit.getConsoleSender();

        Chat.sendRawMessage(sender, "------------ &c&lAkarian Punish MySQL Manager &f------------");
        Chat.sendRawMessage(sender, "");

        try {

            Class.forName("com.mysql.jdbc.Driver");
            Chat.sendRawMessage(sender, "&aConnecting to the MySQL database...");
            setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password));
            Chat.sendRawMessage(sender, "");
            Chat.sendRawMessage(sender, "&aPunish has successfully established a connection to the MySQL database.");

            Statement s = connection.createStatement();

            Chat.sendRawMessage(sender, "");
            Chat.sendRawMessage(sender, "&aPunish will now check the tables...");
            Chat.sendRawMessage(sender, "");

            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + banTable + " (ID varchar(255) NOT NULL PRIMARY KEY, UUID varchar(255), STAFF varchar(255), START BIGINT(20), END BIGINT(20), REASON varchar(255), ACTIVE TINYINT(1), SILENT TINYINT(1))");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + muteTable + " (ID varchar(255) NOT NULL PRIMARY KEY, UUID varchar(255), STAFF varchar(255), START BIGINT(20), END BIGINT(20), REASON varchar(255), ACTIVE TINYINT(1), SILENT TINYINT(1))");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + warnTable + " (ID varchar(255) NOT NULL PRIMARY KEY, UUID varchar(255), STAFF varchar(255), START BIGINT(20), END BIGINT(20), REASON varchar(255), ACTIVE TINYINT(1), SILENT TINYINT(1))");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + kickTable + " (ID varchar(255) NOT NULL PRIMARY KEY, UUID varchar(255), STAFF varchar(255), START BIGINT(20), REASON varchar(255), SILENT TINYINT(1))");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + ipTable + " (UUID varchar(255) NOT NULL PRIMARY KEY, IP varchar(255))");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + blTable + " (ID varchar(255) NOT NULL PRIMARY KEY, IP varchar(255), STAFF varchar(255), START BIGINT(20), REASON varchar(255), ACTIVE TINYINT(1), SILENT TINYINT(1))");

            Chat.sendRawMessage(sender, "&aPunish has updated all tables. The plugin will now enable.");
            Chat.sendRawMessage(sender, "");
            Chat.sendRawMessage(sender, "&8&m&l---------------------------------------------");

            startConnectionTimer();
            startSyncTimer();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            Punish.getInstance().getLogger().log(Level.SEVERE, Chat.format("&c&lAn error has occurred while connecting to the database. Please see stacktrace above."));
            Chat.sendRawMessage(sender, "");
            Chat.sendRawMessage(sender, "&8&m&l---------------------------------------------");
            Chat.log("&c&lAkarian Punish has encountered an error connecting to the MySQL database. Please check console. E" + e.getCause().getLocalizedMessage(), true);
            return false;
        }
    }

    private void startConnectionTimer() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Punish.getInstance(), this::reconnect, 0, 20 * 60 * 60);

    }

    private void startSyncTimer() {
        syncTime = System.currentTimeMillis();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Punish.getInstance(), this::sync, 0, 20);
    }

    public boolean shutdown() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void sync() {
        try {
            PreparedStatement ban = this.getConnection().prepareStatement("SELECT * FROM " + this.getBanTable());
            PreparedStatement kick = this.getConnection().prepareStatement("SELECT * FROM " + this.getKickTable());

            ResultSet banSet = ban.executeQuery();
            ResultSet kickSet = kick.executeQuery();

            Files files = new Files();
            FileConfiguration lang = files.getConfig("lang");

            while (banSet.next()) {
                if (banSet.getLong(4) >= syncTime) {
                    UUID uuid = UUID.fromString(banSet.getString(2));

                    if (Bukkit.getPlayer(uuid) != null) {
                        if (banSet.getLong(5) == -1) { //Perm Ban
                            //Broadcast
                            if (!banSet.getBoolean(8)) {
                                Chat.broadcast(lang.getString("Ban Message").replace("$player$", Bukkit.getPlayer(uuid).getName())
                                        .replace("$reason$", banSet.getString(6))
                                        .replace("$staff$", NameManager.getName(banSet.getString(3))));
                            } else {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (p.hasPermission("punish.silent")) {
                                        Chat.broadcast(lang.getString("Ban Message").replace("$player$", Bukkit.getPlayer(uuid).getName())
                                                .replace("$reason$", banSet.getString(6))
                                                .replace("$staff$", NameManager.getName(banSet.getString(3))));
                                    }
                                }
                            }
                            //Kick Player
                            Bukkit.getPlayer(uuid).kickPlayer(Chat.format(lang.getString("Disconnect Ban Message").replace("$reason$", banSet.getString(6))
                                    .replace("$staff$", NameManager.getName(banSet.getString(3)))
                                    .replace("$start$", Chat.formatTime(banSet.getLong(4)))
                                    .replace("$end$", Chat.formatTime(-1))
                                    .replace("$id$", banSet.getString(1))));
                        } else { //Temp Ban
                            //Broadcast
                            if (!banSet.getBoolean(8)) {
                                Chat.broadcast(lang.getString("TempBan Message").replace("$player$", Bukkit.getPlayer(uuid).getName())
                                        .replace("$reason$", banSet.getString(6))
                                        .replace("$staff$", NameManager.getName(banSet.getString(3))));
                            } else {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (p.hasPermission("punish.silent")) {
                                        Chat.broadcast(lang.getString("TempBan Message").replace("$player$", Bukkit.getPlayer(uuid).getName())
                                                .replace("$reason$", banSet.getString(6))
                                                .replace("$staff$", NameManager.getName(banSet.getString(3))));
                                    }
                                }
                            }
                            //Kick player
                            Bukkit.getPlayer(uuid).kickPlayer(Chat.format(lang.getString("Disconnect TempBan Message").replace("$reason$", banSet.getString(6))
                                    .replace("$staff$", NameManager.getName(banSet.getString(3)))
                                    .replace("$start$", Chat.formatTime(banSet.getLong(4)))
                                    .replace("$end$", Chat.formatTime(-1))
                                    .replace("$id$", banSet.getString(1))));
                        }
                    }
                }
            }
            while (kickSet.next()) {
                if(kickSet.getLong(4) >= syncTime) {
                    UUID uuid = UUID.fromString(kickSet.getString(2));

                    if(Bukkit.getPlayer(uuid) != null) {
                        //Broadcast
                        if (!kickSet.getBoolean(6)) {
                            Chat.broadcast("&r" + lang.getString("Kick Message").replace("$player$", Bukkit.getPlayer(uuid).getName())
                                    .replace("$reason$", kickSet.getString(5))
                                    .replace("$staff$", NameManager.getName(kickSet.getString(3))));
                        } else {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (p.hasPermission("punish.silent")) {
                                    Chat.broadcast("&r" + lang.getString("Kick Message").replace("$player$", Bukkit.getPlayer(uuid).getName())
                                            .replace("$reason$", kickSet.getString(5))
                                            .replace("$staff$", NameManager.getName(kickSet.getString(3))));
                                }
                            }
                        }
                        //Kick player
                        Bukkit.getPlayer(uuid).kickPlayer(Chat.format(lang.getString("Player Kick Message").replace("$reason$", kickSet.getString(5))
                                .replace("$id$", kickSet.getString(1))
                                .replace("$staff$", NameManager.getName(kickSet.getString(3)))));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        syncTime = System.currentTimeMillis();
    }

    public void reconnect() {
        try {
            setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
