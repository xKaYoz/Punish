package net.akarian.punish.utils;

public enum PunishmentType {

    BAN("Ban"),
    TEMPBAN("TempBan"),
    MUTE("Mute"),
    KICK("Kick"),
    WARN("Warn"),
    BLACKLIST("Blacklist");

    public String getString() {
        return string;
    }

    String string;

    PunishmentType(String string){
        this.string = string;
    }

    public static PunishmentType getType(String s){
        if(s.equalsIgnoreCase("ban")) return BAN;
        if(s.equalsIgnoreCase("mute")) return MUTE;
        if(s.equalsIgnoreCase("kick")) return KICK;
        if(s.equalsIgnoreCase("tempban")) return TEMPBAN;
        if(s.equalsIgnoreCase("warn")) return WARN;
        if(s.equalsIgnoreCase("blacklist")) return BLACKLIST;
        return null;
    }

}
