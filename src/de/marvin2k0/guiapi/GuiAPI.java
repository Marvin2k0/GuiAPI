package de.marvin2k0.guiapi;

import de.marvin2k0.guiapi.listeners.GuiInventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

/**
 * @author Marvin Leiers
 * @version 1.0
 */
public class GuiAPI extends JavaPlugin implements CommandExecutor
{
    public static Plugin PLUGIN;
    private static List<String> inventories;

    public void onEnable()
    {
        setUp(this);

        this.getCommand("creategui").setExecutor(this);
    }

    /**
     * Needs to be called at the beginning.
     *
     * @param plugin Main class of your plugin (class that extends JavaPlugin)
     */
    public static void setUp(Plugin plugin)
    {
        PLUGIN = plugin;

        plugin.getServer().getPluginManager().registerEvents(new GuiInventoryListener(), plugin);
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();

        inventories = plugin.getConfig().getStringList("inventories");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("§cNur fuer Spieler!");

            return true;
        }

        Player player = (Player) sender;

        if (!(args.length  <= 2) || args.length == 0)
        {
            player.sendMessage("§cBitte benutze /" + label + " <name> [zeilen]");

            return true;
        }

        int size = 27;

        if (args.length == 2)
        {
            try
            {
                size = Integer.valueOf(args[1]) * 9;
            }
            catch (Exception e)
            {
                player.sendMessage("§cBitte benutze /" + label + " <name> [zeilen]");
            }
        }

        String title = ChatColor.translateAlternateColorCodes('&', args[0]);
/*
        ItemStack navigatorItemForward = new ItemStack(Material.GREEN_GLAZED_TERRACOTTA);
        ItemStack navigatorItemBackward = new ItemStack(Material.RED_GLAZED_TERRACOTTA);
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        ItemMeta forwardMeta = navigatorItemForward.getItemMeta();
        forwardMeta.setDisplayName("§aNächste Seite");
        navigatorItemForward.setItemMeta(forwardMeta);

        ItemMeta backwardMeta = navigatorItemBackward.getItemMeta();
        backwardMeta.setDisplayName("§cVorherige Seite");
        navigatorItemBackward.setItemMeta(backwardMeta);

        OfflinePlayer offlinePlayer = null;

        for (OfflinePlayer p : Bukkit.getOfflinePlayers())
        {
            if (p.getName().equals("Marvin2k0"))
            {
                offlinePlayer = p;
            }
        }

        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwningPlayer(offlinePlayer);
        head.setItemMeta(skullMeta);

        for (int i = 0; i < content.getSize(); i++)
        {
            ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName("§fx");
            glass.setItemMeta(meta);

            content.setItem(i, glass);
        }

        content.setItem(content.getSize() - 9 + 3, navigatorItemBackward);
        content.setItem(content.getSize() - 9 + 4, head);
        content.setItem(content.getSize() - 9 + 5, navigatorItemForward);*/

        GuiInventory gui = GuiInventory.createInventory(title, size);
        Inventory content = Bukkit.createInventory(null, size, title);

        GuiItem placeHolder = new GuiItem(Material.STAINED_GLASS_PANE, 1, (short) 0);
        placeHolder.setGuiItemAction(GuiItemAction.NOTHING);

        for (int i = 0; i < content.getSize(); i++)
        {
            content.setItem(i, placeHolder);
        }

        GuiItem forward = new GuiItem(Material.GREEN_GLAZED_TERRACOTTA);
        forward.setGuiItemAction(GuiItemAction.NEXT_PANEL);
        forward.setNextPanel("§6gelb");

        gui.setContent(content);
        gui.addActionItem(forward, 0);

        player.openInventory(gui.getInventory());

        return true;
    }

    public static GuiInventory inventoryFromName(String name)
    {
        File file = new File(PLUGIN.getDataFolder().getPath() + "/GUIs/" + name + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (!config.getStringList("items").isEmpty())
        {
            Inventory content = Bukkit.createInventory(null, config.getInt("size"), name);

            for (String materialName : config.getStringList("items"))
            {
                content.addItem(new GuiItem(Material.GREEN_GLAZED_TERRACOTTA));
                System.out.println("added item!");
            }

            GuiInventory gui = GuiInventory.createInventory(content.getTitle(), 27);
            gui.setContent(content);

            return gui;
        }

        //TODO: aus listener
        /*event.getWhoClicked().sendMessage(event.getCurrentItem().getType().toString());
        if (gui.isActionItem(event.getCurrentItem()))
        {
            event.getWhoClicked().sendMessage("ist action item");
        }*/

        return null;
    }

    public static boolean isGuiInventory(String title)
    {
        inventories = PLUGIN.getConfig().getStringList("inventories");

        return inventories.contains(title);
    }
}
