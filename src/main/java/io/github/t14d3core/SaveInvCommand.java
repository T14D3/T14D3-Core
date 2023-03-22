package io.github.t14d3core;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class SaveInvCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tsaveinv") && sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack[] inventoryContents = player.getInventory().getContents();
            YamlConfiguration config = new YamlConfiguration();

            for (int i = 0; i < inventoryContents.length; i++) {
                if (inventoryContents[i] != null) {
                    config.set("inventory." + i, inventoryContents[i]);
                }
            }

            File file = new File(T14D3Core.getPlugin(T14D3Core.class).getDataFolder(), player.getUniqueId() + ".yml");

            try {
                config.save(file);
                sender.sendMessage("Inventory saved to " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                sender.sendMessage("Failed to save inventory!");
            }
            return true;
        }
        return false;
    }
}
