package de.marvin2k0.guiapi.listeners;

import de.marvin2k0.guiapi.GuiAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GuiInventoryListener implements Listener
{
    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || inventory.getTitle() == null)
            return;

        String title = inventory.getTitle();

        if (GuiAPI.isGuiInventory(title))
        {
            event.setCancelled(true);
        }
    }
}
