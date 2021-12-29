package com.icurety.temeredrops;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import sun.text.resources.ext.FormatData_ga;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public final class TemereDrops extends JavaPlugin {

    public static TemereDrops instance;

    static void log(String message) {
        Bukkit.getLogger().info("[TemereDrops] " + message);
    }

    private Long getSavedSeed() {
        File dataFile = new File(getDataFolder(), "seed.txt");
        if(dataFile.exists())
        {
            try {
                List<String> lines = Files.readAllLines(dataFile.toPath());
                long seed = Long.parseLong(lines.get(0));
                return seed;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        Long savedSeed = getSavedSeed();
        long seed = savedSeed != null ? savedSeed : getServer().getWorlds().get(0).getSeed();
        DropRegistry.load(seed);

        getServer().getPluginManager().registerEvents(new DropEventListener(), this);
        this.getCommand("temerescheme").setExecutor(new CommandScheme());
        this.getCommand("temereReassign").setExecutor(new CommandNewSeed());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
