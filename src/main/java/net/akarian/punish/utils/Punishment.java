package net.akarian.punish.utils;

import lombok.Getter;

public class Punishment {
    @Getter
    PunishmentType type;
    @Getter
    String punished;
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

    public Punishment(PunishmentType type, String id, String punished, String staff, long start, long end, String reason, boolean active) {
        this.type = type;
        this.id = id;
        if (punished.replace(".", ",").split(",").length == 4)
            this.ip = punished;
        else
            this.punished = punished;
        this.staff = staff;
        this.start = start;
        this.end = end;
        this.reason = reason;
        this.active = active;
    }

}
