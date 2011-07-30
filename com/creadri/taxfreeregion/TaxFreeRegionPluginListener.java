/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creadri.taxfreeregion;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.util.logging.Level;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Adrien
 */
public class TaxFreeRegionPluginListener extends ServerListener {

    private TaxFreeRegion plugin;

    public TaxFreeRegionPluginListener(TaxFreeRegion plugin) {
        this.plugin = plugin;
    }    

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (!plugin.isWorldEditSet()) {
            Plugin worldEdit = plugin.getServer().getPluginManager().getPlugin("WorldEdit");
            if (worldEdit != null) {
                if (worldEdit.isEnabled()) {
                    plugin.setWorldEdit((WorldEditPlugin) worldEdit);
                    TaxFreeRegion.log.log(Level.INFO, "[TaxFreeRegion] Successfully linked with WorldEdit.");
                }
            }
        }
    }
}
