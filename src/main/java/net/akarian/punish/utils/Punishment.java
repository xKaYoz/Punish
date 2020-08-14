package net.akarian.punish.utils;

import lombok.Getter;

import java.util.UUID;

public class Punishment {
    @Getter
    PunishmentType type;
    @Getter
    UUID punished;
    @Getter
    String staff;
    @Getter
    String reason;
    @Getter
    long start;
    @Getter
    long end;
    @Getter
    String id;
    @Getter
    boolean active;
    @Getter
    String ip;

    public Punishment(PunishmentType type, String id, UUID punished, String staff, long start, long end, String reason, boolean active){
        this.type = type;
        this.id = id;
        this.punished = punished;
        this.staff = staff;
        this.start = start;
        this.end = end;
        this.reason = reason;
        this.active = active;
    }

    public Punishment(PunishmentType type, String id, String ip, String staff, long start, long end, String reason, boolean active){
        this.type = type;
        this.id = id;
        this.ip = ip;
        this.staff = staff;
        this.start = start;
        this.end = end;
        this.reason = reason;
        this.active = active;
    }

}
