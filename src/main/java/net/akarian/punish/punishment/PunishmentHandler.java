package net.akarian.punish.punishment;

import net.akarian.punish.Punish;
import net.akarian.punish.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PunishmentHandler {

    private static MySQL sql = Punish.getInstance().getMySQL();

    /* Bans */
    public static int ban(String uuid, String staff, String reason) {
        String id = generateID();

        Player p = Bukkit.getPlayer(UUID.fromString(uuid));
        Files files = new Files();
        FileConfiguration lang = files.getConfig("lang");
        long start = System.currentTimeMillis();

        if (p != null) {
            String staffName;
            if (staff.equalsIgnoreCase("Console")) {
                staffName = "CONSOLE";
            } else {
                staffName = Bukkit.getOfflinePlayer(UUID.fromString(staff)).getName();
            }
            p.kickPlayer(Chat.format(lang.getString("Disconnect Ban Message").replace("$reason$", reason)
                    .replace("$staff$", staffName)
                    .replace("$start$", Chat.formatTime(start))
                    .replace("$end$", Chat.formatTime(-1))
                    .replace("$id$", id)));
        }

        if (isBanned(uuid) != 1) {

            try {

                PreparedStatement insert = sql.getConnection().prepareStatement("INSERT INTO " + sql.getBanTable() + " (ID,UUID,STAFF,START,END,REASON,ACTIVE) VALUE (?,?,?,?,?,?,?)");
                insert.setString(1, id);
                insert.setString(2, uuid);
                insert.setString(3, staff);
                insert.setLong(4, start);
                insert.setLong(5, -1);
                insert.setString(6, reason);
                insert.setBoolean(7, true);

                insert.executeUpdate();
                insert.close();

            } catch (SQLException e) {
                e.printStackTrace();
                return 2;
            }

        } else {
            return 1;
        }

        return 0;
    }

    public static int tempBan(String uuid, String staff, String reason, long time) {
        String id = generateID();

        Player p = Bukkit.getPlayer(UUID.fromString(uuid));
        Files files = new Files();
        FileConfiguration lang = files.getConfig("lang");
        long start = System.currentTimeMillis();

        if (p != null) {
            String staffName;
            if (staff.equalsIgnoreCase("Console")) {
                staffName = "CONSOLE";
            } else {
                staffName = Bukkit.getOfflinePlayer(UUID.fromString(staff)).getName();
            }
            p.kickPlayer(Chat.format(lang.getString("Disconnect TempBan Message").replace("$length$", Chat.formatTime((time - System.currentTimeMillis()) / 1000))
                    .replace("$reason$", reason)
                    .replace("$staff$", staffName)
                    .replace("$start$", Chat.formatTime(start))
                    .replace("$end$", Chat.formatTime(time))
                    .replace("$id$", id)));
        }

        int banned = isBanned(uuid);

        if (banned != 1) {

            try {

                PreparedStatement insert = sql.getConnection().prepareStatement("INSERT INTO " + sql.getBanTable() + " (ID,UUID,STAFF,START,END,REASON,ACTIVE) VALUE (?,?,?,?,?,?,?)");
                insert.setString(1, id);
                insert.setString(2, uuid);
                insert.setString(3, staff);
                insert.setLong(4, System.currentTimeMillis());
                insert.setLong(5, time);
                insert.setString(6, reason);
                insert.setBoolean(7, true);

                insert.executeUpdate();
                insert.close();

                Chat.log("&aSuccessfully banned.", true);

            } catch (SQLException e) {
                e.printStackTrace();
                return 2;
            }

        } else {
            return 1;
        }

        return 0;
    }

    public static int getNumBan(String uuid) {
        int amt = 0;

        try {

            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getBanTable() + " WHERE UUID=?");

            statement.setString(1, uuid);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                amt++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amt;
    }

    public static int getNumStaffBan(String uuid) {
        int amt = 0;

        try {

            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getBanTable() + " WHERE STAFF=?");

            statement.setString(1, uuid);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                amt++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amt;
    }

    public static int unban(String uuid) {

        try {
            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getBanTable() + " WHERE UUID=?");

            statement.setString(1, uuid);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                if (r.getBoolean(7)) {

                    PreparedStatement update = sql.getConnection().prepareStatement("UPDATE " + sql.getBanTable() + " SET ACTIVE=? WHERE UUID=?");

                    update.setBoolean(1, false);
                    update.setString(2, uuid);

                    update.executeUpdate();
                    update.close();

                }
            }
            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 2;

    }

    public static int isBanned(String uuid) {

        try {
            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getBanTable() + " WHERE UUID=?");

            statement.setString(1, uuid);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                if (r.getBoolean(7)) {

                    if ((r.getLong(5) == -1) || r.getLong(5) - System.currentTimeMillis() > 0) {
                        //Is permanent or has not expired.
                        return 1;
                    } else if (r.getLong(5) - System.currentTimeMillis() <= 0) {
                        //Ban has expired
                        unban(uuid);
                        return 0;
                    }
                }
            }

            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 2;
    }

    public static Punishment getBan(String uuid) {

        try {
            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getBanTable() + " WHERE UUID=? AND ACTIVE=?");

            statement.setString(1, uuid);
            statement.setBoolean(2, true);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                if ((r.getLong(5) != -1) && r.getLong(5) - System.currentTimeMillis() <= 0) {
                    unban(uuid);
                    continue;
                }
                if (r.getLong(5) != -1) {
                    return new Punishment(PunishmentType.TEMPBAN, r.getString(1), UUID.fromString(uuid), r.getString(3), r.getLong(4), r.getLong(5), r.getString(6), r.getBoolean(7));
                }
                return new Punishment(PunishmentType.BAN, r.getString(1), UUID.fromString(uuid), r.getString(3), r.getLong(4), r.getLong(5), r.getString(6), r.getBoolean(7));
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* Mutes */
    public static int mute(String uuid, String staff, String reason) {
        String id = generateID();
        if (isMuted(uuid) != 1) {
            try {

                PreparedStatement insert = sql.getConnection().prepareStatement("INSERT INTO " + sql.getMuteTable() + " (ID,UUID,STAFF,START,END,REASON,ACTIVE) VALUE (?,?,?,?,?,?,?)");
                insert.setString(1, id);
                insert.setString(2, uuid);
                insert.setString(3, staff);
                insert.setLong(4, System.currentTimeMillis());
                insert.setLong(5, -1);
                insert.setString(6, reason);
                insert.setBoolean(7, true);

                insert.executeUpdate();
                insert.close();

                Chat.log("&aSuccessfully muted.", true);

            } catch (SQLException e) {
                e.printStackTrace();
                return 2;
            }
        } else {
            return 1;
        }
        return 0;
    }

    public static int tempMute(String uuid, String staff, String reason, long time) {
        String id = generateID();
        if (isMuted(uuid) != 1) {
            try {

                PreparedStatement insert = sql.getConnection().prepareStatement("INSERT INTO " + sql.getMuteTable() + " (ID,UUID,STAFF,START,END,REASON,ACTIVE) VALUE (?,?,?,?,?,?,?)");
                insert.setString(1, id);
                insert.setString(2, uuid);
                insert.setString(3, staff);
                insert.setLong(4, System.currentTimeMillis());
                insert.setLong(5, time);
                insert.setString(6, reason);
                insert.setBoolean(7, true);

                insert.executeUpdate();
                insert.close();

                Chat.log("&aSuccessfully Muted.", true);

            } catch (SQLException e) {
                e.printStackTrace();
                return 2;
            }
        } else {
            return 1;
        }
        return 0;
    }

    public static int getNumMute(String uuid) {
        int amt = 0;

        try {

            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getMuteTable() + " WHERE UUID=?");

            statement.setString(1, uuid);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                amt++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amt;
    }

    public static int getNumStaffMute(String uuid) {
        int amt = 0;

        try {

            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getMuteTable() + " WHERE STAFF=?");

            statement.setString(1, uuid);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                amt++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amt;
    }

    public static int unmute(String uuid) {
        try {
            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getMuteTable() + " WHERE UUID=?");

            statement.setString(1, uuid);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                if (r.getBoolean(7)) {

                    PreparedStatement update = sql.getConnection().prepareStatement("UPDATE " + sql.getMuteTable() + " SET ACTIVE=? WHERE UUID=?");

                    update.setBoolean(1, false);
                    update.setString(2, uuid);

                    update.executeUpdate();
                    update.close();

                }
            }
            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 2;
    }

    public static int isMuted(String uuid) {

        try {
            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getMuteTable() + " WHERE UUID=?");

            statement.setString(1, uuid);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                if (r.getBoolean(7)) {

                    if ((r.getLong(5) == -1) || r.getLong(5) - System.currentTimeMillis() > 0) {
                        return 1;
                    } else if (r.getLong(5) - System.currentTimeMillis() <= 0) {
                        unmute(uuid);
                        return 0;
                    }

                }
            }

            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 2;
    }

    public static Punishment getMute(String uuid) {

        try {
            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getMuteTable() + " WHERE UUID=? AND ACTIVE=?");

            statement.setString(1, uuid);
            statement.setBoolean(2, true);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                if ((r.getLong(5) != -1) && r.getLong(5) - System.currentTimeMillis() <= 0) {
                    unmute(uuid);
                    continue;
                }
                return new Punishment(PunishmentType.MUTE, r.getString(1), UUID.fromString(uuid), r.getString(3), r.getLong(4), r.getLong(5), r.getString(6), r.getBoolean(7));
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* Kicks */
    public static int kick(String uuid, String staff, String reason) {
        String id = generateID();

        Files files = new Files();
        YamlConfiguration lang = files.getConfig("lang");

        Player p = Bukkit.getPlayer(UUID.fromString(uuid));

        if (p != null) {
            p.kickPlayer(Chat.format(lang.getString("Player Kick Message").replace("$reason$", reason).replace("$id$", id)));
        }

        try {
            PreparedStatement s = sql.getConnection().prepareStatement("INSERT INTO " + sql.getKickTable() + " (ID,UUID,STAFF,START,REASON) VALUE (?,?,?,?,?)");

            s.setString(1, id);
            s.setString(2, uuid);
            s.setString(3, staff);
            s.setLong(4, System.currentTimeMillis());
            s.setString(5, reason);

            s.executeUpdate();
            s.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    public static int getNumKick(String uuid) {
        int amt = 0;

        try {

            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getKickTable() + " WHERE UUID=?");

            statement.setString(1, uuid);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                amt++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amt;
    }

    public static int getNumStaffKick(String uuid) {
        int amt = 0;

        try {

            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getKickTable() + " WHERE STAFF=?");

            statement.setString(1, uuid);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                amt++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amt;
    }

    /* Warns */
    public static int warn(String uuid, String staff, long time, String reason) {
        long nt = System.currentTimeMillis() + (time * 1000);
        String id = generateID();
        try {
            PreparedStatement insert = sql.getConnection().prepareStatement("INSERT INTO " + sql.getWarnTable() + " (ID,UUID,STAFF,START,END,REASON,ACTIVE) VALUE (?,?,?,?,?,?,?)");
            insert.setString(1, id);
            insert.setString(2, uuid);
            insert.setString(3, staff);
            insert.setLong(4, System.currentTimeMillis());
            insert.setLong(5, nt);
            insert.setString(6, reason);
            insert.setBoolean(7, true);

            insert.executeUpdate();
            insert.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }

    public static int getNumWarn(String uuid) {
        int amt = 0;

        try {

            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getWarnTable() + " WHERE UUID=?");

            statement.setString(1, uuid);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                amt++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amt;
    }

    public static int getNumStaffWarn(String uuid) {
        int amt = 0;

        try {

            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getWarnTable() + " WHERE STAFF=?");

            statement.setString(1, uuid);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                amt++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amt;
    }

    /* Removing Punishments */
    public static void purge(String uuid) {

        try {
            PreparedStatement ban = sql.getConnection().prepareStatement("DELETE FROM " + sql.getBanTable() + " WHERE UUID=?");
            PreparedStatement mute = sql.getConnection().prepareStatement("DELETE FROM " + sql.getMuteTable() + " WHERE UUID=?");
            PreparedStatement warn = sql.getConnection().prepareStatement("DELETE FROM " + sql.getWarnTable() + " WHERE UUID=?");
            PreparedStatement kick = sql.getConnection().prepareStatement("DELETE FROM " + sql.getKickTable() + " WHERE UUID=?");

            ban.setString(1, uuid);
            mute.setString(1, uuid);
            warn.setString(1, uuid);
            kick.setString(1, uuid);

            ban.executeUpdate();
            mute.executeUpdate();
            warn.executeUpdate();
            kick.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static boolean removePunishment(String id) {

        try {
            PreparedStatement ban = sql.getConnection().prepareStatement("DELETE FROM " + sql.getBanTable() + " WHERE ID=?");
            PreparedStatement mute = sql.getConnection().prepareStatement("DELETE FROM " + sql.getMuteTable() + " WHERE ID=?");
            PreparedStatement warn = sql.getConnection().prepareStatement("DELETE FROM " + sql.getWarnTable() + " WHERE ID=?");
            PreparedStatement kick = sql.getConnection().prepareStatement("DELETE FROM " + sql.getKickTable() + " WHERE ID=?");

            ban.setString(1, id);
            mute.setString(1, id);
            warn.setString(1, id);
            kick.setString(1, id);

            ban.executeUpdate();
            mute.executeUpdate();
            warn.executeUpdate();
            kick.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    /* Retrieving Punishments */
    public static Punishment getPunishment(String id) {

        try {
            PreparedStatement ban = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getBanTable() + " WHERE ID=?");

            ban.setString(1, id);

            ResultSet bs = ban.executeQuery();

            if (bs.next()) {
                if (bs.getLong(5) == -1)
                    return new Punishment(PunishmentType.TEMPBAN, bs.getString(1), UUID.fromString(bs.getString(2)), bs.getString(3), bs.getLong(4), bs.getLong(5), bs.getString(6), bs.getBoolean(7));
                return new Punishment(PunishmentType.BAN, bs.getString(1), UUID.fromString(bs.getString(2)), bs.getString(3), bs.getLong(4), bs.getLong(5), bs.getString(6), bs.getBoolean(7));
            }

            PreparedStatement mute = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getMuteTable() + " WHERE ID=?");

            mute.setString(1, id);

            ResultSet ms = mute.executeQuery();

            if (ms.next()) {
                return new Punishment(PunishmentType.MUTE, ms.getString(1), UUID.fromString(ms.getString(2)), ms.getString(3), ms.getLong(4), ms.getLong(5), ms.getString(6), ms.getBoolean(7));
            }

            PreparedStatement warn = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getWarnTable() + " WHERE ID=?");

            warn.setString(1, id);

            ResultSet ws = warn.executeQuery();

            if (ws.next()) {
                return new Punishment(PunishmentType.WARN, ws.getString(1), UUID.fromString(ws.getString(2)), ws.getString(3), ws.getLong(4), ws.getLong(5), ws.getString(6), ws.getBoolean(7));
            }

            PreparedStatement kick = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getKickTable() + " WHERE ID=?");

            kick.setString(1, id);

            ResultSet ks = kick.executeQuery();

            if (ks.next()) {
                return new Punishment(PunishmentType.KICK, ks.getString(1), UUID.fromString(ks.getString(2)), ks.getString(3), ks.getLong(4), 1, ks.getString(5), false);
            }

            PreparedStatement blacklist = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getBlTable() + " WHERE ID=?");

            blacklist.setString(1, id);

            ResultSet bls = kick.executeQuery();

            if (bls.next()) {
                return new Punishment(PunishmentType.BLACKLIST, bls.getString(1), bls.getString(2), bls.getString(3), bls.getLong(4), 1, bls.getString(5), bls.getBoolean(6));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static ArrayList<Punishment> getPunishments(String uuid) {

        ArrayList<Punishment> punishments = new ArrayList<>();

        try {
            PreparedStatement ban = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getBanTable() + " WHERE UUID=?");

            ban.setString(1, uuid);

            ResultSet bs = ban.executeQuery();

            while (bs.next()) {
                punishments.add(new Punishment(PunishmentType.TEMPBAN, bs.getString(1), UUID.fromString(uuid), bs.getString(3), bs.getLong(4), bs.getLong(5), bs.getString(6), bs.getBoolean(7)));
            }

            PreparedStatement mute = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getMuteTable() + " WHERE UUID=?");

            mute.setString(1, uuid);

            ResultSet ms = mute.executeQuery();

            while (ms.next()) {
                punishments.add(new Punishment(PunishmentType.MUTE, ms.getString(1), UUID.fromString(uuid), ms.getString(3), ms.getLong(4), ms.getLong(5), ms.getString(6), ms.getBoolean(7)));
            }
            PreparedStatement warn = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getWarnTable() + " WHERE UUID=?");

            warn.setString(1, uuid);

            ResultSet ws = warn.executeQuery();

            while (ws.next()) {
                punishments.add(new Punishment(PunishmentType.WARN, ws.getString(1), UUID.fromString(uuid), ws.getString(3), ws.getLong(4), ws.getLong(5), ws.getString(6), ws.getBoolean(7)));
            }

            PreparedStatement kick = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getKickTable() + " WHERE UUID=?");

            kick.setString(1, uuid);

            ResultSet ks = kick.executeQuery();

            while (ks.next()) {
                punishments.add(new Punishment(PunishmentType.KICK, ks.getString(1), UUID.fromString(uuid), ks.getString(3), ks.getLong(4), 1, ks.getString(5), false));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return punishments;
    }

    public static ArrayList<Punishment> getStaffPunishments(String uuid) {

        ArrayList<Punishment> punishments = new ArrayList<>();

        try {
            PreparedStatement ban = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getBanTable() + " WHERE STAFF=?");

            ban.setString(1, uuid);

            ResultSet bs = ban.executeQuery();

            while (bs.next()) {
                punishments.add(new Punishment(PunishmentType.TEMPBAN, bs.getString(1), UUID.fromString(uuid), bs.getString(3), bs.getLong(4), bs.getLong(5), bs.getString(6), bs.getBoolean(7)));
            }

            PreparedStatement mute = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getMuteTable() + " WHERE STAFF=?");

            mute.setString(1, uuid);

            ResultSet ms = mute.executeQuery();

            while (ms.next()) {
                punishments.add(new Punishment(PunishmentType.MUTE, ms.getString(1), UUID.fromString(uuid), ms.getString(3), ms.getLong(4), ms.getLong(5), ms.getString(6), ms.getBoolean(7)));
            }
            PreparedStatement warn = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getWarnTable() + " WHERE STAFF=?");

            warn.setString(1, uuid);

            ResultSet ws = warn.executeQuery();

            while (ws.next()) {
                punishments.add(new Punishment(PunishmentType.WARN, ws.getString(1), UUID.fromString(uuid), ws.getString(3), ws.getLong(4), ws.getLong(5), ws.getString(6), ws.getBoolean(7)));
            }

            PreparedStatement kick = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getKickTable() + " WHERE STAFF=?");

            kick.setString(1, uuid);

            ResultSet ks = kick.executeQuery();

            while (ks.next()) {
                punishments.add(new Punishment(PunishmentType.KICK, ks.getString(1), UUID.fromString(uuid), ks.getString(3), ks.getLong(4), 1, ks.getString(5), false));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return punishments;
    }

    public static void rollback(UUID staff, long time) {

        long rollbackTo = System.currentTimeMillis() - time;

        try {

            List<Punishment> purge = new ArrayList<>();

            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getBanTable() + " WHERE STAFF=?");

            statement.setString(1, staff.toString());

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                if (rs.getLong(4) >= rollbackTo) {
                    purge.add(getPunishment(rs.getString(1)));
                }
            }

            statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getMuteTable() + " WHERE STAFF=?");

            statement.setString(1, staff.toString());

            rs = statement.executeQuery();

            while (rs.next()) {
                if (rs.getLong(4) >= rollbackTo) {
                    purge.add(getPunishment(rs.getString(1)));
                }
            }

            statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getWarnTable() + " WHERE STAFF=?");

            statement.setString(1, staff.toString());

            rs = statement.executeQuery();

            while (rs.next()) {
                if (rs.getLong(4) >= rollbackTo) {
                    purge.add(getPunishment(rs.getString(1)));
                }
            }

            statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getKickTable() + " WHERE STAFF=?");

            statement.setString(1, staff.toString());

            rs = statement.executeQuery();

            while (rs.next()) {
                if (rs.getLong(4) >= rollbackTo) {
                    purge.add(getPunishment(rs.getString(1)));
                }
            }

            for (Punishment p : purge) {
                removePunishment(p.getId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* IPs */
    public static int blacklist(String ip, String staff, String reason) {

        Files files = new Files();
        FileConfiguration lang = files.getConfig("lang");
        String id = generateID();

        if(!isBlacklisted(ip)) {

            try {
                PreparedStatement statement = sql.getConnection().prepareStatement("INSERT INTO " + sql.getBlTable() + " (ID,IP,STAFF,START,REASON,ACTIVE) VALUE (?,?,?,?,?,?)");

                statement.setString(1, id);
                statement.setString(2, ip);
                statement.setString(3, staff);
                statement.setLong(4, System.currentTimeMillis());
                statement.setString(5, reason);
                statement.setBoolean(6, true);

                statement.executeUpdate();
                statement.close();

                String staffName;
                if (staff.equalsIgnoreCase("Console")) {
                    staffName = "CONSOLE";
                } else {
                    staffName = Bukkit.getOfflinePlayer(UUID.fromString(staff)).getName();
                }

                for (Player p : PunishmentHandler.getPlayersFromIP(ip)) {
                    p.kickPlayer(Chat.format(lang.getString("Disconnect Blacklist Message").replace("$reason$", reason)
                            .replace("$staff$", staffName)
                            .replace("$start$", Chat.formatTime(System.currentTimeMillis()))
                            .replace("$end$", Chat.formatTime(-1))
                            .replace("$id$", id)));
                }

                for(UUID uuid : getUUIDsFromIP(ip)) {
                    ban(uuid.toString(), staff, reason);
                }

                return 2;
            } catch (SQLException e) {
                e.printStackTrace();
                return 1;
            }
        }

        return 0;
    }

    public static int unblacklist(String ip) {
        try {
            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getBlTable() + " WHERE IP=?");

            statement.setString(1, ip);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                if (r.getBoolean(6)) {

                    PreparedStatement update = sql.getConnection().prepareStatement("UPDATE " + sql.getBlTable() + " SET ACTIVE=? WHERE IP=?");

                    update.setBoolean(1, false);
                    update.setString(2, ip);

                    update.executeUpdate();
                    update.close();

                }
            }
            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 2;
    }

    public static boolean isBlacklisted(String ip) {
        try {
            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getBlTable() + " WHERE IP=? AND ACTIVE=?");

            statement.setString(1, ip);
            statement.setBoolean(2, true);

            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static int logIP(String uuid) {

        String ip;

        Player p = Bukkit.getPlayer(UUID.fromString(uuid));

        if (p != null) {
            ip = p.getAddress().getAddress().toString().replace("/", "");
        } else {
            return 1;
        }

        try {

            PreparedStatement statement = sql.getConnection().prepareStatement("INSERT INTO " + sql.getIpTable() + " (UUID,IP) VALUE (?,?)");

            statement.setString(1, uuid);
            statement.setString(2, ip);

            statement.execute();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;

    }

    public static boolean isIPLogged(String uuid) {

        try {

            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getIpTable() + " WHERE UUID=?");

            statement.setString(1, uuid);

            ResultSet r = statement.executeQuery();

            return r.next();


        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;

    }

    public static String getIP(String uuid) {
        try {
            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getIpTable() + " WHERE UUID=?");

            statement.setString(1, uuid);

            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                return rs.getString(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(Bukkit.getPlayer(UUID.fromString(uuid)) != null) return Bukkit.getPlayer(UUID.fromString(uuid)).getAddress().toString().replace("/", "");

        return "";
    }

    public static List<Player> getPlayersFromIP(String ip) {

        List<Player> players = new ArrayList<>();
        ip = ip.replace("/", "");

        for (Player p : Bukkit.getOnlinePlayers()) {
            String playerIP = p.getAddress().getAddress().toString().replace("/", "");
            if (ip.equals(playerIP)) players.add(p);
        }

        return players;
    }

    public static List<UUID> getUUIDsFromIP(String ip) {

        List<UUID> uuids = new ArrayList<>();

        try {
            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getIpTable() + " WHERE IP=?");

            statement.setString(1, ip);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                uuids.add(UUID.fromString(rs.getString(1)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return uuids;

    }

    public static Punishment getBlacklist(String ip) {
        try {
            PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM " + sql.getBlTable() + " WHERE IP=? AND ACTIVE=?");

            statement.setString(1, ip);
            statement.setBoolean(2, true);

            ResultSet r = statement.executeQuery();

            if (r.next()) {
                return new Punishment(PunishmentType.BLACKLIST, r.getString(1), ip, r.getString(3), r.getLong(4),1, r.getString(5), r.getBoolean(6));
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* ID Manager */
    public static String generateID() {
        Random r = new Random();
        String st = "#" + getLetter() + r.nextInt(9) + r.nextInt(9) + getLetter() + r.nextInt(9) + getLetter() + r.nextInt(9) + r.nextInt(9);
        return st;
    }

    public static char getLetter() {
        String input = "abcdefghijklmnopqrstuvwxyz";
        String outputSt;
        char output;
        Random r = new Random();

        int i = r.nextInt(25) + 1;

        outputSt = input.charAt(i) + "";

        int uc = r.nextInt(2);

        if (uc == 0) {
            output = outputSt.toUpperCase().charAt(0);
        } else {
            output = outputSt.toLowerCase().charAt(0);
        }

        return output;
    }
}
