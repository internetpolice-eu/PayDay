package de.brightstorm;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Bukkit;
import java.io.File;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

public class payday extends JavaPlugin
{
    public static List<String> groups;
    public static List<String> worlds;
    public static Plugin dies;
    public static FileConfiguration users;
    public static Logger log;
    public static boolean money;
    
    static {
        payday.money = true;
    }
    
    public void onEnable() {
        payday.dies = this;
        payday.log = this.getLogger();
        if (!this.getConfig().contains("groups")) {
            this.getConfig().options().header("Generated " + new Timestamp(new Date().getTime()) + "\nFor help consult http://dev.bukkit.org/server-mods/payday/");
            final List<String> grouplist = new ArrayList<String>();
            grouplist.add("vip");
            grouplist.add("normal");
            this.getConfig().addDefault("groups", grouplist);
            this.getConfig().addDefault("normal.time", 60);
            this.getConfig().addDefault("normal.amount", 20);
            this.getConfig().addDefault("normal.interest", 0.5);
            this.getConfig().addDefault("vip.time", 60);
            this.getConfig().addDefault("vip.amount", 50);
            this.getConfig().addDefault("vip.interest", 2);
            final List<String> worldlist = new ArrayList<>();
            this.getConfig().addDefault("restricted_worlds", worldlist);
        }
        this.getConfig().addDefault("message", "You just got %a for being online %t minutes.");
        this.getConfig().addDefault("paycheck-message", "Your next payday is in %t minutes.");
        this.getConfig().addDefault("use_vault", true);
        this.getConfig().addDefault("reward_item", 264);
        this.getConfig().addDefault("use_essentials", false);
        this.getConfig().set("version", this.getDescription().getVersion());
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        g.useEssentials = this.getConfig().getBoolean("use_essentials");
        if (g.useEssentials) {
            if (this.getServer().getPluginManager().isPluginEnabled("Essentials")) {
                payday.log.info("Hooked into Essentials!");
            }
            else {
                payday.log.severe("Essentials support could not be activated, cause Essentials isn't installed!");
                g.useEssentials = false;
            }
        }
        payday.groups = (List<String>)this.getConfig().getList("groups");
        final File userFile = new File(this.getDataFolder() + System.getProperty("file.separator") + "users.yml");
        payday.money = this.getConfig().getBoolean("use_vault");
        payday.worlds = this.getConfig().getStringList("restricted_worlds");
        if (payday.money && !this.getServer().getPluginManager().isPluginEnabled("Vault")) {
            payday.log.warning("Vault seems to be not installed. Gonna disable myself...");
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    payday.this.getServer().getPluginManager().disablePlugin(payday.dies);
                }
            }, 60L);
            payday.money = false;
        }
        payday.users = new YamlConfiguration();
        if (!userFile.exists()) {
            try {
                userFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            payday.users.load(userFile);
        }
        catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InvalidConfigurationException e3) {
            e3.printStackTrace();
        }
        if (payday.money) {
            final moneyRewarder rewarder = new moneyRewarder();
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, rewarder, 1200L, 1200L);
        }
        else {
            final itemRewarder rewarder2 = new itemRewarder();
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, rewarder2, 1200L, 1200L);
        }
        payday.log.info(this.toString() + " enabled!");
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("payday")) {
            if (sender.hasPermission("payday.admincommand")) {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "-----------------------------------------------------");
                    sender.sendMessage(ChatColor.RED + "--------------------" + ChatColor.BOLD + "PayDay Help" + ChatColor.RESET + ChatColor.RED + "---------------------");
                    sender.sendMessage(ChatColor.RED + "-----------------------------------------------------");
                    sender.sendMessage(ChatColor.RED + "/payday reset     | Deletes ALL user data!");
                    sender.sendMessage(ChatColor.RED + "/payday reload    | Reloads the config.yml");
                    sender.sendMessage(ChatColor.RED + "-----------------------------------------------------");
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.ITALIC).append("For more info visit http://dev.bukkit.org/server-mods/payday/").toString());
                    sender.sendMessage(ChatColor.RED + "-----------------------------------------------------");
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    this.reloadConfig();
                    payday.groups = (List<String>)this.getConfig().getList("groups");
                    payday.money = this.getConfig().getBoolean("use_vault");
                    payday.worlds = this.getConfig().getStringList("restricted_worlds");
                    sender.sendMessage(ChatColor.DARK_GREEN + "config.yml reloaded!");
                }
                else {
                    if (!args[0].equalsIgnoreCase("reset")) {
                        sender.sendMessage(ChatColor.RED + "-----------------------------------------------------");
                        sender.sendMessage(ChatColor.RED + "--------------------" + ChatColor.BOLD + "PayDay Help" + ChatColor.RESET + ChatColor.RED + "---------------------");
                        sender.sendMessage(ChatColor.RED + "-----------------------------------------------------");
                        sender.sendMessage(ChatColor.RED + "/payday reset     | Deletes ALL user data!");
                        sender.sendMessage(ChatColor.RED + "/payday reload    | Reloads the config.yml");
                        sender.sendMessage(ChatColor.RED + "-----------------------------------------------------");
                        sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.ITALIC).append("For more info visit http://dev.bukkit.org/server-mods/payday/").toString());
                        sender.sendMessage(ChatColor.RED + "-----------------------------------------------------");
                        return true;
                    }
                    final File userFile = new File(this.getDataFolder() + System.getProperty("file.separator") + "users.yml");
                    try {
                        payday.users.save(userFile);
                        userFile.delete();
                        userFile.createNewFile();
                        payday.users.load(userFile);
                        sender.sendMessage(ChatColor.DARK_GREEN + "users.yml has been cleared.");
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch (InvalidConfigurationException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "You don't have permission!");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("paycheck")) {
            final PDPlayer p = new PDPlayer((Player)sender);
            p.findGroup();
            if (p.ignore()) {
                sender.sendMessage(ChatColor.RED + "You don't have permission!");
            }
            else {
                final String raw = payday.dies.getConfig().getString("paycheck-message");
                final String message = StringUtils.replace(raw, "%t", String.valueOf(payday.dies.getConfig().getInt(p.getGroup() + ".time") - payday.users.getInt(p.getPlayer().getName())));
                sender.sendMessage(ChatColor.BLUE + message);
            }
            return true;
        }
        return false;
    }
    
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        final File userFile = new File(this.getDataFolder() + System.getProperty("file.separator") + "users.yml");
        try {
            payday.users.save(userFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        payday.log.info(this.toString() + " disabled!");
    }
}
