package io.github.t14d3core;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryManager {
    private final DatabaseManager databaseManager;

    public InventoryManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void saveInventory(PlayerInventory inventory, String inventoryType, String serializedInventory) {
        ItemStack[] inventoryContents = inventory.getContents();
        databaseManager.saveInventory(inventory.getHolder().getUniqueId(), inventoryType, serializedInventory);
    }

    public String loadInventory(PlayerInventory inventory, String inventoryType) {
        String serializedInventory = databaseManager.loadInventory(inventory.getHolder().getUniqueId(), inventoryType);
        if (serializedInventory != null) {
            ItemStack[] inventoryContents = InventorySerializer.fromBase64(serializedInventory);
            inventory.setContents(inventoryContents);
        }
        return serializedInventory;
    }

}
