/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creadri.taxfreeregion;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Adrien
 */
public class TaxFreeRegionPlayerListener extends PlayerListener {

    private TaxFreeRegion plugin;

    public TaxFreeRegionPlayerListener(TaxFreeRegion plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        plugin.RegionCheck(event.getPlayer());

    }

    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        if (plugin.isPlayerInsideRegion(player)) {
            String msg = event.getMessage();
            
            if (plugin.isCommandBlackListed(msg)) {
                player.sendMessage(TaxFreeRegion.messages.getMessage("blacklisted"));
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        if (plugin.isPlayerInsideRegion(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    
}
