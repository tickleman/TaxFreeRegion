package com.creadri.taxfreeregion;

import com.creadri.util.Messages;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TaxFreeRegionPlayerListener extends PlayerListener
{
  private TaxFreeRegion plugin;

  public TaxFreeRegionPlayerListener(TaxFreeRegion plugin)
  {
    this.plugin = plugin;
  }

  public void onPlayerInteract(PlayerInteractEvent event)
  {
  	if (this.plugin.isPlayerInsideRegion(event.getPlayer())) {
  		if (this.plugin.isCommandBlackListed("CHEST")) {
  			if (
  				event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
  			) {
  				Block block = event.getClickedBlock();
  				if (
  					block.getType().equals(Material.CHEST)
  				) {
  					event.setCancelled(true);
  					event.getPlayer().sendMessage(TaxFreeRegion.messages.getMessage("blacklisted"));
  				}
				}
	    }
  	}
  }

  public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
  {
  	if (this.plugin.isPlayerInsideRegion(event.getPlayer())) {
  		if (this.plugin.isCommandBlackListed("STORAGE_MINECART")) {
 				Entity entity = event.getRightClicked();
 				if (
 					entity.getClass().getName().contains("StorageMinecart")
 					|| entity.getClass().getName().contains("PoweredMinecart")
 				) {
 					event.setCancelled(true);
					event.getPlayer().sendMessage(TaxFreeRegion.messages.getMessage("blacklisted"));
 				}
	    }
  	}
  }

  public void onPlayerMove(PlayerMoveEvent event)
  {
    if (event.isCancelled()) {
      return;
    }

    this.plugin.RegionCheck(event.getPlayer());
  }

  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
  {
    if (event.isCancelled()) {
      return;
    }

    Player player = event.getPlayer();
    String msg = event.getMessage();

    if (this.plugin.isPlayerInsideRegion(player)) {
      if (this.plugin.isCommandBlackListed(msg)) {
        player.sendMessage(TaxFreeRegion.messages.getMessage("blacklisted"));
        event.setCancelled(true);
      }
    } else {
    	if (
    		!player.hasPermission("taxfreeregion.nowhitelist")
    		&& !TaxFreeRegion.permissionHandler.has(player, "taxfreeregion.nowhitelist")
    	) {
    		if (this.plugin.isCommandWhiteListed(msg)) {
    			player.sendMessage(TaxFreeRegion.messages.getMessage("whitelisted"));
    			event.setCancelled(true);
    		}
    	}
    }
  }

  public void onPlayerDropItem(PlayerDropItemEvent event)
  {
    if (event.isCancelled()) {
      return;
    }

    if (this.plugin.isPlayerInsideRegion(event.getPlayer()))
      event.setCancelled(true);
  }
}