package io.github.t14d3core;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SaveInvCommand implements CommandExecutor {

    private final T14D3Core plugin;

    public SaveInvCommand(T14D3Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }
        if (!sender.hasPermission("t14d3core.saveinv")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }


        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        String folderName = player.getGameMode().name().toLowerCase(); // Get the game mode folder name
        File dataFolder = new File(plugin.getDataFolder(), folderName); // Append the folder name to the data folder
        if (!dataFolder.exists()) { // If the folder doesn't exist, create it
            dataFolder.mkdir();
        }
        File playerFile = new File(dataFolder, uuid.toString() + ".yml");
        YamlConfiguration playerData = new YamlConfiguration();

        // Save the player's inventory contents
        List<ItemStack> inventoryContents = new ArrayList<>();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                inventoryContents.add(item);
            }
        }
        playerData.set("inventory", inventoryContents);

        // Save the player's armor contents
        List<ItemStack> armorContents = new ArrayList<>();
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null) {
                armorContents.add(item);
            }
        }
        playerData.set("armor", armorContents);

        // Save the player's offhand item
        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (offhand != null) {
            playerData.set("offhand", offhand);
        }

        // Save the player's experience
        playerData.set("experience", player.getExp());
        playerData.set("totalExperience", player.getTotalExperience());
        playerData.set("level", player.getLevel());

        // Save the player's health and hunger
        playerData.set("health", player.getHealth());
        playerData.set("foodLevel", player.getFoodLevel());
        playerData.set("saturation", player.getSaturation());

        // Save the player's active potion effects
        Map<String, Map<String, Integer>> potionEffectsData = new HashMap<>();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            Map<String, Integer> effectData = new HashMap<>();
            effectData.put("duration", effect.getDuration());
            effectData.put("amplifier", effect.getAmplifier());
            potionEffectsData.put(effect.getType().getName(), effectData);
        }
        playerData.set("potionEffects", potionEffectsData);

        // Save the player data to the file
        try {
            playerData.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendMessage("Your inventory has been saved.");

        return true;
    }
}
