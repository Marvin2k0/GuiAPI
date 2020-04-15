package de.marvin2k0.guiapi;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GuiItem extends ItemStack
{
    private GuiInventory guiInventory;
    private GuiItemAction guiItemAction;
    private String nextPanel;

    public GuiItem(Material type)
    {
        super(type);
    }

    public GuiItem(Material type, int amount, short damage)
    {
        super(type, amount, damage);
    }

    public void setGuiItemAction(GuiItemAction guiItemAction)
    {
        this.guiItemAction = guiItemAction;
    }

    public void setNextPanel(String nextPanelName)
    {
        this.nextPanel = nextPanelName;
    }

    public GuiInventory getNextPanel()
    {
        return GuiAPI.inventoryFromName(nextPanel);
    }

    public GuiInventory getGuiInventory()
    {
        return this.guiInventory;
    }

    public GuiItemAction getGuiItemAction()
    {
        return this.guiItemAction;
    }
}
