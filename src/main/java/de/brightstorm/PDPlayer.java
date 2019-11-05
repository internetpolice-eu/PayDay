package de.brightstorm;

import java.util.Iterator;
import org.bukkit.entity.Player;

public class PDPlayer
{
    private String group;
    private Player p;
    private boolean ignore;
    
    public PDPlayer(final Player p) {
        this.ignore = false;
        this.p = p;
    }
    
    public void findGroup() {
        this.ignore = true;
        for (final String group : payday.groups) {
            if (this.p.hasPermission("payday." + group)) {
                this.group = group;
                this.ignore = false;
            }
        }
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public Player getPlayer() {
        return this.p;
    }
    
    public boolean ignore() {
        return this.ignore;
    }
}
