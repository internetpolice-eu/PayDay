package de.brightstorm;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault.economy.Economy;

public class moneyRewarder implements Runnable
{
    private Economy economy;
    
    private boolean setupEconomy() {
        final RegisteredServiceProvider<Economy> economyProvider = (RegisteredServiceProvider<Economy>)payday.dies.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (economyProvider != null) {
            this.economy = (Economy)economyProvider.getProvider();
        }
        return this.economy != null;
    }
    
    public moneyRewarder() {
        this.setupEconomy();
        payday.log.info("Hooked into " + this.economy.getName());
    }
    
    private void doJob(final PDPlayer p) {
        if (payday.users.contains(p.getPlayer().getName())) {
            payday.users.set(p.getPlayer().getName(), (Object)(payday.users.getInt(p.getPlayer().getName()) + 1));
        }
        else {
            payday.users.set(p.getPlayer().getName(), (Object)0);
        }
        final String group = p.getGroup();
        if (payday.users.getInt(p.getPlayer().getName()) >= payday.dies.getConfig().getInt(String.valueOf(group) + ".time") && (payday.dies.getConfig().getDouble(String.valueOf(group) + ".maxAmount") == 0.0 || this.economy.getBalance(p.getPlayer().getName()) <= payday.dies.getConfig().getDouble(String.valueOf(group) + ".maxAmount")) && !payday.worlds.contains(p.getPlayer().getLocation().getWorld().getName())) {
            double amount = payday.dies.getConfig().getDouble(String.valueOf(group) + ".amount");
            amount += this.economy.getBalance(p.getPlayer().getName()) / 100.0 * payday.dies.getConfig().getDouble(String.valueOf(group) + ".interest");
            amount = Math.round(amount * 100.0) / 100.0;
            final String raw = payday.dies.getConfig().getString("message");
            final String ph1 = StringUtils.replace(raw, "%a", String.valueOf(amount) + " " + this.economy.currencyNamePlural());
            final String message = StringUtils.replace(ph1, "%t", String.valueOf(payday.dies.getConfig().getInt(String.valueOf(group) + ".time")));
            this.economy.depositPlayer(p.getPlayer().getName(), amount);
            g.paid += (int)amount;
            payday.users.set(p.getPlayer().getName(), (Object)0);
            p.getPlayer().sendMessage(ChatColor.BLUE + message);
            payday.log.info(String.valueOf(p.getPlayer().getName()) + " just got " + amount + " " + this.economy.currencyNamePlural() + " for being online " + payday.dies.getConfig().getInt(String.valueOf(group) + ".time") + " minutes.");
        }
    }
    
    @Override
    public void run() {
        payday.groups = (List<String>)payday.dies.getConfig().getStringList("groups");
        for (int i = 0; i < payday.dies.getServer().getOnlinePlayers().length; ++i) {
            final Player pl = payday.dies.getServer().getOnlinePlayers()[i];
            final PDPlayer p = new PDPlayer(pl);
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
}
