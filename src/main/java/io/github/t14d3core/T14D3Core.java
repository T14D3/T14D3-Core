package io.github.t14d3core;

import org.bukkit.plugin.java.JavaPlugin;

public class T14D3Core extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register the save inventory command
        SaveInvCommand saveInvCommand = new SaveInvCommand(this);
        getCommand("tsaveinv").setExecutor(saveInvCommand);

        // Register the load inventory command
        LoadInvCommand loadInvCommand = new LoadInvCommand(this);
        getCommand("tloadinv").setExecutor(loadInvCommand);

        // Register the tbuild command
        TBuildCommand tBuildCommand = new TBuildCommand(this);
        getCommand("tbuild").setExecutor(tBuildCommand);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
