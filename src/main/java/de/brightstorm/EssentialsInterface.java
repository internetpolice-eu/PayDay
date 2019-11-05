package de.brightstorm;

import com.earth2me.essentials.User;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import com.earth2me.essentials.Essentials;

public class EssentialsInterface
{
    Essentials ie;
    
    public EssentialsInterface() {
        this.ie = (Essentials)Bukkit.getServer().getPluginManager().getPlugin("Essentials");
    }
    
    public boolean isAfk(final Player p) {
        final User user = this.ie.getUser((Object)p);
        return user.isAfk();
    }
}
