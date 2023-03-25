package io.github.t14d3core;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TBuildCommand implements CommandExecutor {

    private static SaveInvCommand saveInvCommand = null;
    private static LoadInvCommand loadInvCommand = null;

    public TBuildCommand(LoadInvCommand loadInvCommand, SaveInvCommand saveInvCommand) {
        this.loadInvCommand = loadInvCommand;
        this.saveInvCommand = saveInvCommand;
    }

    public static void tbuild(Player player) {
        GameMode currentMode = player.getGameMode();

        if (currentMode == GameMode.SURVIVAL) {
            // Save inventory and switch to creative
            saveInvCommand.saveInventory(player);
            player.setGameMode(GameMode.CREATIVE);
            loadInvCommand.loadInventory(player);
            player.sendMessage("Your inventory has been saved and loaded for building.");
        } else if (currentMode == GameMode.CREATIVE) {
            // Save creative inventory and switch to survival
            saveInvCommand.saveInventory(player);
            player.setGameMode(GameMode.SURVIVAL);
            loadInvCommand.loadInventory(player);
            player.sendMessage("Your creative inventory has been saved and your survival inventory has been loaded.");
        } else {
            player.sendMessage("You must be in either survival or creative mode to use this command.");
        }
    }

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
        TBuildCommand.tbuild(player);

        return true;
    }
}

