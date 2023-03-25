package io.github.t14d3core;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LoadInvCommand implements CommandExecutor {

    private final T14D3Core plugin;

    public LoadInvCommand(T14D3Core plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }
        if (!sender.hasPermission("t14d3core.loadinv")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }

        Player player = (Player) sender;
        loadInventory(player);

        return true;
    }

    public void loadInventory(Player player) {
        UUID uuid = player.getUniqueId();
        GameMode gameMode = player.getGameMode();

        File dataFolder = plugin.getDataFolder();
        File playerFile;

        if (gameMode == GameMode.SURVIVAL) {
            File survivalFolder = new File(dataFolder, "survival");
            playerFile = new File(survivalFolder, uuid.toString() + ".yml");
        } else {
            File creativeFolder = new File(dataFolder, "creative");
            playerFile = new File(creativeFolder, uuid.toString() + ".yml");
        }

        YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);

        // Clear the player's inventory before setting the new contents
        player.getInventory().clear();

        // Set the player's inventory contents
        List<Map<String, Object>> inventoryContents = (List<Map<String, Object>>) playerData.getList("inventory");
        for (Map<String, Object> itemData : inventoryContents) {
            ItemStack item = (ItemStack) itemData.get("item");
            int slot = (int) itemData.get("slot");
            player.getInventory().setItem(slot, item);
        }

        // Set the player's armor contents
        ItemStack[] armorContents = ((List<ItemStack>) playerData.getList("armor")).toArray(new ItemStack[0]);
        player.getInventory().setArmorContents(armorContents);

        // Set the player's offhand item
        ItemStack offhand = playerData.getItemStack("offhand");
        player.getInventory().setItemInOffHand(offhand);

        // Set the player's experience
        player.setExp((float) playerData.getDouble("experience"));
        player.setTotalExperience(playerData.getInt("totalExperience"));
        player.setLevel(playerData.getInt("level"));

        // Set the player's health and hunger
        player.setHealth(playerData.getDouble("health"));
        player.setFoodLevel(playerData.getInt("foodLevel"));
        player.setSaturation((float) playerData.getDouble("saturation"));

        // Set the player's active potion effects
        Map<String, Object> potionEffectsData = playerData.getConfigurationSection("potionEffects").getValues(false);
        for (String effectName : potionEffectsData.keySet()) {
            PotionEffectType effectType = PotionEffectType.getByName(effectName);
            if (effectType == null) {
                continue;
            }

            int duration = playerData.getInt("potionEffects." + effectName + ".duration");
            int amplifier = playerData.getInt("potionEffects." + effectName + ".amplifier");
            PotionEffect effect = new PotionEffect(effectType, duration, amplifier);
            player.addPotionEffect(effect);
        }

        player.sendMessage("Your inventory has been loaded.");
    }
}
