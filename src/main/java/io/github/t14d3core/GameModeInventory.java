package io.github.t14d3core;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GameModeInventory {
    private Inventory inventory;

    public GameModeInventory() {
        this.inventory = Bukkit.createInventory(null, 27, "Game Mode Inventory");
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void addItem(ItemStack item) {
        if (inventory.firstEmpty() != -1) {
            inventory.addItem(item);
        }
    }

    public void removeItem(ItemStack item) {
        inventory.removeItem(item);
    }
}
