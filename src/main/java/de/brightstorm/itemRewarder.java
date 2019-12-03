package de.brightstorm;

import org.bukkit.inventory.PlayerInventory;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;

public class itemRewarder implements Runnable
{
    Material item;
    
    public itemRewarder() {
        this.item = Material.getMaterial(payday.dies.getConfig().getInt("reward_item"));
    }
    
    private void doJob(final PDPlayer p) {
        if (payday.users.contains(p.getPlayer().getName())) {
            payday.users.set(p.getPlayer().getName(), payday.users.getInt(p.getPlayer().getName()) + 1);
        }
        else {
            payday.users.set(p.getPlayer().getName(), 0);
        }
        final String group = p.getGroup();
        if (payday.users.getInt(p.getPlayer().getName()) >= payday.dies.getConfig().getInt(group + ".time") && (payday.dies.getConfig().getDouble(group + ".maxAmount") == 0.0 || getAmount(p.getPlayer(), payday.dies.getConfig().getInt("reward_item")) < payday.dies.getConfig().getDouble(group + ".maxAmount")) && !payday.worlds.contains(p.getPlayer().getLocation().getWorld().getName())) {
            final String raw = payday.dies.getConfig().getString("message");
            final String ph1 = StringUtils.replace(raw, "%a", String.valueOf(payday.dies.getConfig().getDouble(group + ".amount")));
            final String message = StringUtils.replace(ph1, "%t", String.valueOf(payday.dies.getConfig().getInt(group + ".time")));
            final ItemStack s = new ItemStack(this.item, payday.dies.getConfig().getInt(group + ".amount"));
            p.getPlayer().getInventory().addItem(s);
            g.given += payday.dies.getConfig().getInt(group + ".amount");
            payday.users.set(p.getPlayer().getName(), 0);
            p.getPlayer().sendMessage(ChatColor.BLUE + message);
            payday.log.info(p.getPlayer().getName() + " just got " + payday.dies.getConfig().getDouble(group + ".amount") + " " + this.item.name() + " for being online " + payday.dies.getConfig().getInt(group + ".time") + " minutes.");
        }
    }
    
    @Override
    public void run() {
        payday.groups = payday.dies.getConfig().getStringList("groups");
        for (Player player : payday.dies.getServer().getOnlinePlayers()) {
            final PDPlayer p = new PDPlayer(player);
            p.findGroup();
            if (g.useEssentials) {
                final EssentialsInterface ei = new EssentialsInterface();
                if (!ei.isAfk(p.getPlayer())) {
                    this.doJob(p);
                }
            }
            else if (!p.ignore()) {
                this.doJob(p);
            }
        }
    }
    
    public static int getAmount(final Player player, final int id) {
        final PlayerInventory inventory = player.getInventory();
        final ItemStack[] items = inventory.getContents();
        int has = 0;
        ItemStack[] array;
        for (int length = (array = items).length, i = 0; i < length; ++i) {
            final ItemStack item = array[i];
            if (item != null && item.getTypeId() == id && item.getAmount() > 0) {
                has += item.getAmount();
            }
        }
        return has;
    }
}
