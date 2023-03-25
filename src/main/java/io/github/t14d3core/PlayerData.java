package io.github.t14d3core;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    private final Player player;
    private ItemStack[] inventory;
    private ItemStack[] armor;
    private ItemStack offhand;
    private float experience;
    private int totalExperience;
    private int level;
    private double health;
    private int foodLevel;
    private float saturation;
    private List<PotionEffect> potionEffects;

    public PlayerData(Player player) {
        this.player = player;
        this.inventory = player.getInventory().getContents();
        this.armor = player.getInventory().getArmorContents();
        this.offhand = player.getInventory().getItemInOffHand();
        this.experience = player.getExp();
        this.totalExperience = player.getTotalExperience();
        this.level = player.getLevel();
        this.health = player.getHealth();
        this.foodLevel = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.potionEffects = new ArrayList<>(player.getActivePotionEffects());
    }

    public void save() {
        // Save any necessary data here
    }

    public void restore(Player player) {
        player.getInventory().setContents(inventory);
        player.getInventory().setArmorContents(armor);
        player.getInventory().setItemInOffHand(offhand);
        player.setExp(experience);
        player.setTotalExperience(totalExperience);
        player.setLevel(level);
        player.setHealth(health);
        player.setFoodLevel(foodLevel);
        player.setSaturation(saturation);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        for (PotionEffect effect : potionEffects) {
            player.addPotionEffect(effect);
        }
    }
}
