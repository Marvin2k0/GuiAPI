package de.marvin2k0.guiapi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    private HashMap<ItemStack, GuiInventory> nextPanels;
    private boolean hasNext;

    private GuiInventory(Inventory inventory)
    {
        configFile = new File(plugin.getDataFolder().getPath() + "/" + inventory.getTitle() + ".yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        try
        {
            config.save(configFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        setContent(inventory);
    }

    /**
     * Set up the next panel.
     *
     * @param navigator Item that when you click on it, takes you to the next panel.
     * @param nextPanel The next panel.
     */
    public void setNextPanel(ItemStack navigator, GuiInventory nextPanel)
    {
        this.nextPanels.put(navigator, nextPanel);
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
    private GuiInventory setContent(Inventory inventory)
    {
        this.content = inventory;
        List<String> items = new ArrayList<String>();

        for (ItemStack item : inventory.getContents())
        {
            /* TODO: nullpointer fixen und type in den namen einfügen */
            items.add(item.getType().toString() + "-" + item.getItemMeta().getDisplayName());
        }

        config.set("items", items);

        try
        {
            config.save(configFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return this;
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

        ItemStack navigatorItemForward = new ItemStack(Material.GREEN_GLAZED_TERRACOTTA);
        ItemStack navigatorItemBackward = new ItemStack(Material.RED_GLAZED_TERRACOTTA);

        ItemMeta forwardMeta = navigatorItemForward.getItemMeta();
        forwardMeta.setDisplayName("§aNächste Seite");
        navigatorItemForward.setItemMeta(forwardMeta);

        ItemMeta backwardMeta = navigatorItemBackward.getItemMeta();
        backwardMeta.setDisplayName("§cVorherige Seite");
        navigatorItemBackward.setItemMeta(backwardMeta);

        for (int i = 0; i < content.getSize(); i++)
        {
            ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName("§fx");
            glass.setItemMeta(meta);

            content.setItem(i, glass);
        }

        content.setItem(content.getSize() - 9 + 3, navigatorItemBackward);
        content.setItem(content.getSize() - 9 + 5, navigatorItemForward);

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
}
