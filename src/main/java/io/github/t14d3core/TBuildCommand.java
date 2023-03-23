package io.github.t14d3core;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TBuildCommand implements CommandExecutor {

    private final T14D3Core plugin;

    public TBuildCommand(T14D3Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }
        if (!sender.hasPermission("t14d3core.tbuild")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }


        Player player = (Player) sender;
        GameMode currentMode = player.getGameMode();

        if (currentMode == GameMode.SURVIVAL) {
            // Save inventory and switch to creative
            plugin.getServer().dispatchCommand(player, "tsaveinv");
            player.setGameMode(GameMode.CREATIVE);
            plugin.getServer().dispatchCommand(player, "tloadinv");
            player.sendMessage("Your inventory has been saved and loaded for building.");
        } else if (currentMode == GameMode.CREATIVE) {
            // Save creative inventory and switch to survival
            plugin.getServer().dispatchCommand(player, "tsaveinv");
            player.setGameMode(GameMode.SURVIVAL);
            plugin.getServer().dispatchCommand(player, "tloadinv");
            player.sendMessage("Your creative inventory has been saved and your survival inventory has been loaded.");
        } else {
            player.sendMessage("You must be in either survival or creative mode to use this command.");
        }

        return true;
    }
}
