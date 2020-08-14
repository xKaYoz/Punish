package net.akarian.punish;

import lombok.Getter;
import net.akarian.punish.commands.*;
import net.akarian.punish.events.BanCheckEvent;
import net.akarian.punish.events.ChatCommandManager;
import net.akarian.punish.events.GUIEvents.*;
import net.akarian.punish.events.IPLogEvent;
import net.akarian.punish.events.MuteCheckEvent;
import net.akarian.punish.punishment.guis.handlers.PunishGUIHandler;
import net.akarian.punish.utils.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Punish extends JavaPlugin {

    @Getter
    private static Punish instance;
    @Getter
    private MySQL mySQL;

    public static Punish getInstance() {
        return instance;
    }

    public MySQL getMySQL() {
        return mySQL;
    }


    @Override
    public void onEnable() {
        instance = this;
        mySQL = new MySQL();
        saveDefaultConfig();
        saveGuiConfig();
        saveLang();

        if(!mySQL.setup()){
            setEnabled(false);
        } else {

            registerCommands();
            registerListeners();

            PunishGUIHandler.loadPunishments();
        }
    }

    private void registerCommands() {

        this.getCommand("tempban").setExecutor(new TempBanCommand());
        this.getCommand("ban").setExecutor(new BanCommand());
        this.getCommand("tempmute").setExecutor(new TempMuteCommand());
        this.getCommand("mute").setExecutor(new MuteCommand());
        this.getCommand("unban").setExecutor(new UnbanCommand());
        this.getCommand("unmute").setExecutor(new UnmuteCommand());
        this.getCommand("kick").setExecutor(new KickCommand());
        this.getCommand("punish").setExecutor(new PunishCommand());
        this.getCommand("history").setExecutor(new HistoryCommand());
        this.getCommand("staffhistory").setExecutor(new StaffHistoryCommand());
        this.getCommand("staffrollback").setExecutor(new StaffRollbackCommand());
        this.getCommand("dupeip").setExecutor(new DupeIPCommand());
        this.getCommand("warn").setExecutor(new WarnCommand());
        this.getCommand("chat").setExecutor(new ChatCommands());
        this.getCommand("blacklist").setExecutor(new BlacklistCommand());
        this.getCommand("unblacklist").setExecutor(new UnblacklistCommand());

    }

    private void registerListeners() {

        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new BanCheckEvent(), this);
        pm.registerEvents(new MuteCheckEvent(), this);
        pm.registerEvents(new PunishGUIEvent(), this);
        pm.registerEvents(new HistoryMenuGUIEvent(), this);
        pm.registerEvents(new HistoryGUIEvent(), this);
        pm.registerEvents(new StaffHistoryMenuEvent(), this);
        pm.registerEvents(new StaffHistoryGUIEvent(), this);
        pm.registerEvents(new IPLogEvent(), this);
        pm.registerEvents(new ChatCommandManager(), this);

    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public void saveGuiConfig() {
        saveResource("guiconfig.yml", false);
    }

    public void saveLang() {
        saveResource("lang.yml", false);
    }
}
