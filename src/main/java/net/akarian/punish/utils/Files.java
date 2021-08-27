package net.akarian.punish.utils;

import net.akarian.punish.Punish;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by KaYoz on 7/10/2017.
 * Subscribe to me on Youtube:
 * http://www.youtube.com/c/KaYozMC/
 */

public class Files {

    private File file;
    private YamlConfiguration config;


    public void createFile(String name) {
        this.file = new File(Punish.getInstance().getDataFolder(), name + ".yml");

        if (!Punish.getInstance().getDataFolder().exists()) {
            Punish.getInstance().getDataFolder().mkdir();
        }

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.config = YamlConfiguration.loadConfiguration(file);
        } else {
            try {
                throw new IOException();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void createFile(String dir, String name) {
        File f = new File(dir);
        this.file = new File(dir, name + ".yml");

        if (!f.isDirectory()) {
            f.mkdirs();
        }

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.config = YamlConfiguration.loadConfiguration(file);
        } else {
            try {
                throw new IOException();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getFile(String name) {
        this.file = new File(Punish.getInstance().getDataFolder(), name + ".yml");
        return this.file;
    }

    public File getFile(String dir, String name) {
        this.file = new File(dir, name + ".yml");

        if (!this.file.exists()) {
            return null;
        }

        return this.file;
    }

    public File getDir(String dir) {
        this.file = new File(dir);

        if (!this.file.exists()) {
            return null;
        }

        return this.file;

    }

    public YamlConfiguration getConfig(String name) {
        this.file = new File(Punish.getInstance().getDataFolder(), name + ".yml");

        if (!this.file.exists()) {
            return null;
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);
        return this.config;
    }

    public YamlConfiguration getConfig(String dir, String name) {
        this.file = new File(dir, name + ".yml");

        if (!this.file.exists()) {
            return null;
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);
        return this.config;
    }

}
