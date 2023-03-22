package io.github.t14d3core;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;

public class T14D3Core extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("tsaveinv").setExecutor(new SaveInvCommand());
    }

    @Override
    public void onDisable() {
        // plugin shutdown logic
    }


}

