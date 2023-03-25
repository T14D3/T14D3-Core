package io.github.t14d3core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class T14D3Core extends JavaPlugin {

    private GameModeChangeListener listener;

    @Override
    public void onEnable() {
        // Register the save inventory command
        SaveInvCommand saveInvCommand = new SaveInvCommand(this);
        getCommand("tsaveinv").setExecutor(saveInvCommand);

        // Register the load inventory command
        LoadInvCommand loadInvCommand = new LoadInvCommand(this);
        getCommand("tloadinv").setExecutor(loadInvCommand);

        // Register the tbuild command
        TBuildCommand tBuildCommand = new TBuildCommand(loadInvCommand, saveInvCommand);
        getCommand("tbuild").setExecutor(tBuildCommand);

        // Register the event listener
        listener = new GameModeChangeListener(this);
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
