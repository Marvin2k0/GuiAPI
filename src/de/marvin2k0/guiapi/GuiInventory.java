package de.marvin2k0.guiapi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiInventory implements Listener
{
    private static Plugin plugin = GuiAPI.PLUGIN;

    private File configFile;
    private FileConfiguration config;

    private Inventory content;
    private HashMap<String, GuiInventory> nextPanels = new HashMap<>();
    private boolean hasNext;

    private GuiInventory(Inventory inventory)
    {
        configFile = new File(plugin.getDataFolder().getPath() + "/GUIs/" + inventory.getTitle() + ".yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        config.set("name", inventory.getTitle());

        saveConfig();

        setContent(inventory);
    }

    public String getName()
    {
        return config.getString("name");
    }

    /**
     * Set up the next panel.
     *
     * @param navigator Item that when you click on it, takes you to the next panel.
     * @param nextPanel The next panel.
     */
    public void setNextPanel(ItemStack navigator, GuiInventory nextPanel)
    {
        this.nextPanels.put(navigator.getItemMeta().getDisplayName(), nextPanel);

        this.config.set("nextPanels", navigator.getItemMeta().getDisplayName() + "-" + nextPanel.getInventory().getName());

        saveConfig();
    }

    /**
     * @return Returns the next panel.
     */
    public GuiInventory getNextPanel(ItemStack navigator)
    {
        return this.nextPanels.get(navigator);
    }

    /**
     * @return Indicates whether this exact panel points at a next panel.
     */
    public boolean hasNext()
    {
        return !this.nextPanels.isEmpty();
    }

    /**
     * Creates the GuiInventory.
     *
     * @param inventory The GuiInventory's content.
     * @return The GuiInventory.
     */
    public GuiInventory setContent(Inventory inventory)
    {
        this.content = inventory;
        List<String> items = new ArrayList<String>();

        for (ItemStack item : inventory.getContents())
        {
            /* TODO: nullpointer fixen und type in den namen einfügen */

            if (item == null)
                continue;

            String name = item.getType().toString() + "-" + item.getItemMeta().getDisplayName();

            if (item instanceof GuiItem && ((GuiItem) item).getGuiItemAction() != GuiItemAction.NOTHING)
            {
                System.out.println(item.getType().toString() + " ist ein actin item");
                this.addActionItem((GuiItem) item, 0);

                GuiInventory nextPanel = this.nextPanels.get(item.getItemMeta().getDisplayName());
                name += "-" + nextPanel.getInventory().getName();
            }
            else
            {
                System.out.println(item.getType().toString() + " ist KEIN actin item");
            }

            items.add(name);
        }

        config.set("items", items);

        saveConfig();

        return this;
    }

    public void addActionItem(GuiItem actionItem, int slot)
    {
        if (actionItem.getGuiItemAction() == GuiItemAction.NEXT_PANEL)
        {
            if (!this.nextPanels.containsKey(actionItem.getItemMeta().getDisplayName()))
                this.nextPanels.put(actionItem.getItemMeta().getDisplayName(), actionItem.getNextPanel());

            this.getInventory().setItem(slot, actionItem);
        }
    }

    public boolean isActionItem(ItemStack itemStack)
    {
        return this.nextPanels.containsKey(itemStack.getItemMeta().getDisplayName());
    }

    public Inventory getInventory()
    {
        return this.content;
    }

    /**
     * Creates a standard-sized inventory with custom title.
     *
     * @param title The invenory's title (can use color-codes)
     * @return The inventory
     */
    public static GuiInventory createInventory(String title)
    {
        return createInventory(title, 27);
    }

    /**
     * Creates a custom inventory.
     *
     * @param title The inventory's title (can use color-codes)
     * @param size  The inventory's size.
     * @return The inventory
     */
    public static GuiInventory createInventory(String title, int size)
    {
        if (plugin == null)
            return null;

        Inventory content = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', title));
        List<String> inventories;

        GuiInventory inventory = new GuiInventory(content);

        if (plugin.getConfig().getStringList("inventories").contains(title))
            return inventory;

        if (plugin.getConfig().isSet("inventories"))
        {
            inventories = plugin.getConfig().getStringList("inventories");

            inventories.add(title);
            plugin.getConfig().set("inventories", inventories);
        }
        else
        {
            inventories = new ArrayList<String>();
            inventories.add(title);

            plugin.getConfig().set("inventories", inventories);
        }

        plugin.saveConfig();
        plugin.reloadConfig();

        return inventory;
    }

    private void saveConfig()
    {
        try
        {
            config.save(configFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
