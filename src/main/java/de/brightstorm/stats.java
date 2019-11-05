package de.brightstorm;

import java.io.IOException;
import org.bukkit.plugin.Plugin;

public class stats
{
    public static long paid;
    public static long items;
    Metrics metrics;
    
    static {
        stats.paid = 0L;
        stats.items = 0L;
    }
    
    public stats(final payday p) throws IOException {
        this.metrics = new Metrics((Plugin)p);
        final Metrics.Graph graph = this.metrics.createGraph("Reward type");
        if (payday.money) {
            graph.addPlotter(new Metrics.Plotter("Money") {
                @Override
                public int getValue() {
                    return 1;
                }
            });
        }
        else {
            graph.addPlotter(new Metrics.Plotter("Item") {
                @Override
                public int getValue() {
                    return 1;
                }
            });
        }
        final Metrics.Graph graph2 = this.metrics.createGraph("Money paid");
        graph2.addPlotter(new Metrics.Plotter("$") {
            @Override
            public int getValue() {
                return g.paid;
            }
            
            @Override
            public void reset() {
                g.paid = 0;
            }
        });
        graph2.addPlotter(new Metrics.Plotter("Items") {
            @Override
            public int getValue() {
                return g.given;
            }
            
            @Override
            public void reset() {
                g.given = 0;
            }
        });
    }
    
    public void send() {
    }
    
    public void start() {
        this.metrics.start();
    }
}
