package com.creadri.taxfreeregion;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class TaxFreeRegionPluginListener extends ServerListener
{
  private TaxFreeRegion plugin;

  public TaxFreeRegionPluginListener(TaxFreeRegion plugin)
  {
    this.plugin = plugin;
  }

  public void onPluginEnable(PluginEnableEvent event)
  {
    if (!this.plugin.isWorldEditSet()) {
      Plugin worldEdit = this.plugin.getServer().getPluginManager().getPlugin("WorldEdit");
      if ((worldEdit != null) && 
        (worldEdit.isEnabled())) {
        this.plugin.setWorldEdit((WorldEditPlugin)worldEdit);
        TaxFreeRegion.log.log(Level.INFO, "[TaxFreeRegion] Successfully linked with WorldEdit.");
      }
    }
    if (TaxFreeRegion.permissionHandler == null) {
	    Permissions permissions = (Permissions)this.plugin.getServer().getPluginManager().getPlugin("Permissions");
	    if ((permissions != null) && permissions.isEnabled()) {
	    	TaxFreeRegion.permissionHandler = permissions.getHandler();
	      TaxFreeRegion.log.log(Level.INFO, "[TaxFreeRegion] Successfully linked with Permissions.");
	    }
    }
  }
}