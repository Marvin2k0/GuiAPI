package de.marvin2k0.guiapi;

import de.marvin2k0.guiapi.listeners.GuiInventoryListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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

        player.openInventory(GuiInventory.createInventory(title, size).getInventory());

        return true;
    }

    public static boolean isGuiInventory(String title)
    {
        inventories = PLUGIN.getConfig().getStringList("inventories");

        return inventories.contains(title);
    }
}
