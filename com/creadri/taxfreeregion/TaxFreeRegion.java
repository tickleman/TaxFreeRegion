/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creadri.taxfreeregion;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.creadri.util.inventory.InventoryManager;
import com.creadri.util.inventory.SavedInventory;
import com.creadri.util.Messages;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author creadri
 */
public class TaxFreeRegion extends JavaPlugin {

    private final TaxFreeRegionPluginListener pluginListener = new TaxFreeRegionPluginListener(this);
    private final TaxFreeRegionPlayerListener playerListener = new TaxFreeRegionPlayerListener(this);
    public static final Logger log = Logger.getLogger("Minecraft");
    public static Messages messages = new Messages();
    private boolean eventRegistered;
    private WorldEditPlugin worldEdit;
    private HashMap<String, List<Region>> regions;
    private HashMap<String, SavedInventory> outsideInventories;

    private ArrayList<String> blacklistCommands;
    private File blacklistFile;
    private File regionFile;
    private File inventoryFile;

    @Override
    public void onEnable() {

        try {
            File directory = new File("./plugins/TaxFreeRegion");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // region file
            regionFile = new File(directory, "regions.ser");
            if (!regionFile.exists() || !regionFile.isFile()) {
                regionFile.createNewFile();
            }

            // inventory file
            inventoryFile = new File(directory, "inventories.ser");
            if (!inventoryFile.exists() || !inventoryFile.isFile()) {
                inventoryFile.createNewFile();
            }

            // commands blacklist file
            blacklistFile = new File(directory, "CommandsBlacklist.txt");
            if (!blacklistFile.exists() || !blacklistFile.isFile()) {
                blacklistFile.createNewFile();
            }
            
            // messages file
            File msgFile = new File(directory, "messages.properties");
            if (!msgFile.exists() || !msgFile.isFile()) {
                msgFile.createNewFile();
            }

            messages.loadMessages(msgFile);

        } catch (IOException ex) {
            log.log(Level.SEVERE, "[TaxFreeRegion] : Error on Region File");
            return;
        }

        if (!eventRegistered) {
            PluginManager pm = getServer().getPluginManager();
            pm.registerEvent(Type.PLUGIN_ENABLE, pluginListener, Priority.Monitor, this);
            pm.registerEvent(Type.PLAYER_MOVE, playerListener, Priority.Monitor, this);
            pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Monitor, this);
            pm.registerEvent(Type.PLAYER_DROP_ITEM, playerListener, Priority.Monitor, this);

            eventRegistered = true;
        }

        loadRegions(regionFile);
        loadInventories(inventoryFile);
        loadCommandsBlackList(blacklistFile);

        log.log(Level.INFO, "[TaxFreeRegion] Version " + getDescription().getVersion() + " is enabled!");
    }

    @Override
    public void onDisable() {
        //saveRegions(regionFile);
        saveInventories(inventoryFile);

        log.log(Level.INFO, "[TaxFreeRegion] is disabled!");
    }

    public WorldEditPlugin getWorldEdit() {
        return worldEdit;
    }

    public void setWorldEdit(WorldEditPlugin worldEdit) {
        this.worldEdit = worldEdit;
    }

    public boolean isWorldEditSet() {
        return worldEdit != null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 1) {
            return false;
        }

        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (!player.hasPermission("taxfreeregion.use")) {
                player.sendMessage(messages.getMessage("noPermission"));
                return true;
            }

            if (args.length >= 2 && args[0].equalsIgnoreCase("add")) {

                // get the name
                StringBuilder sb = new StringBuilder(args[1]);
                for (int i = 2; i < args.length; i++) {
                    sb.append(" ");
                    sb.append(args[i]);
                }
                String regionName = sb.toString();

                if (worldEdit == null) {
                    player.sendMessage(messages.getMessage("noWorldEdit"));
                    return true;
                }

                if (deleteRegionByName(regionName)) {
                    player.sendMessage(messages.getMessage("regionOverwriting"));
                }

                Selection sel = worldEdit.getSelection(player);

                if (sel == null) {
                    player.sendMessage(messages.getMessage("noSelection"));
                    return true;
                }

                Location max = sel.getMaximumPoint();
                Location min = sel.getMinimumPoint();

                if (max == null || min == null) {
                    player.sendMessage(messages.getMessage("selectionIncomplete"));
                    return true;
                }

                Region region = new Region();
                region.setName(regionName);
                region.setX1(max.getBlockX());
                region.setX2(min.getBlockX());
                region.setY1(max.getBlockY());
                region.setY2(min.getBlockY());
                region.setZ1(max.getBlockZ());
                region.setZ2(min.getBlockZ());

                addRegion(region, sel.getWorld().getName());

                saveRegions(regionFile);

                player.sendMessage(messages.getMessage("regionAdded"));

            } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {

                if (regions.isEmpty()) {
                    player.sendMessage(messages.getMessage("noRegion"));
                    return true;
                }
                
                String worldColor = messages.getMessage("listWorldColor");
                String regionColor = messages.getMessage("listRegionColor");

                Iterator<Entry<String, List<Region>>> it = regions.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, List<Region>> entry = it.next();
                    // print world
                    player.sendMessage(worldColor + entry.getKey());
                    // print regions in that world
                    Iterator<Region> regit = entry.getValue().iterator();
                    while (regit.hasNext()) {
                        player.sendMessage(regionColor + regit.next().toString());
                    }
                }

            } else if (args.length > 1 && args[0].equalsIgnoreCase("delete")) {

                // get the name
                StringBuilder sb = new StringBuilder(args[1]);
                for (int i = 2; i < args.length; i++) {
                    sb.append(" ");
                    sb.append(args[i]);
                }
                String regionName = sb.toString();

                if (!deleteRegionByName(regionName)) {
                    player.sendMessage(messages.getMessage("noRegion"));
                    return true;
                }

                player.sendMessage(messages.getMessage("regionDeleted"));

                saveRegions(regionFile);
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                loadCommandsBlackList(blacklistFile);
                
                player.sendMessage(messages.getMessage("blackListReloaded"));
            } else {
                return false;
            }
        }

        return true;
    }

    private void loadRegions(File file) {

        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(new FileInputStream(file));

            regions = (HashMap<String, List<Region>>) ois.readObject();

        } catch (EOFException ex) {
            regions = new HashMap<String, List<Region>>();
        } catch (Exception ex) {
            log.log(Level.WARNING, "[TaxFreeRegion] Region file error !");
            regions = new HashMap<String, List<Region>>();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                    log.log(Level.WARNING, "[TaxFreeRegion] Region file error on close !");
                }
            }
        }
    }

    private void saveRegions(File file) {

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));

            oos.writeObject(regions);
        } catch (Exception ex) {
            log.log(Level.SEVERE, "[TaxFreeRegion] Region file not saved !");
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ex) {
                    log.log(Level.WARNING, "[TaxFreeRegion] Region file error on close !");
                }
            }
        }
    }

    private void loadInventories(File file) {

        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(new FileInputStream(file));

            outsideInventories = (HashMap<String, SavedInventory>) ois.readObject();

        } catch (EOFException ex) {
            outsideInventories = new HashMap<String, SavedInventory>();
        } catch (Exception ex) {
            log.log(Level.WARNING, "[TaxFreeRegion] Inventories file error !");
            outsideInventories = new HashMap<String, SavedInventory>();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                    log.log(Level.WARNING, "[TaxFreeRegion] Inventory file error on close !");
                }
            }
        }
    }

    private void saveInventories(File file) {

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));

            oos.writeObject(outsideInventories);
        } catch (Exception ex) {
            log.log(Level.SEVERE, "[TaxFreeRegion] Inventory file not saved !");
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ex) {
                    log.log(Level.WARNING, "[TaxFreeRegion] Inventory file error on close !");
                }
            }
        }
    }

    private void loadCommandsBlackList(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));

            blacklistCommands = new ArrayList<String>();

            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() || !line.startsWith("#")) {
                    blacklistCommands.add(line);
                }
            }

        } catch (Exception ex) {
            log.log(Level.WARNING, "[TaxFreeRegion] Blacklist file error!");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    log.log(Level.WARNING, "[TaxFreeRegion] Blacklist file error on close !");
                }
            }
        }

    }

    public boolean isCommandBlackListed(String command) {
        Iterator<String> it = blacklistCommands.iterator();

        while (it.hasNext()) {
            if (command.startsWith(it.next())) {
                return true;
            }
        }
        return false;
    }

    private void addRegion(Region region, String world) {
        List<Region> list = regions.get(world);

        if (list == null) {
            list = new LinkedList<Region>();
            list.add(region);
            regions.put(world, list);
        } else {
            list.add(region);
        }

        Collections.sort(list);
    }
    

    private boolean deleteRegionByName(String name) {
        Iterator<List<Region>> it = regions.values().iterator();
        while (it.hasNext()) {
            Iterator<Region> regit = it.next().iterator();
            while (regit.hasNext()) {
                Region region = regit.next();
                if (region.getName().equalsIgnoreCase(name)) {
                    regit.remove();
                    return true;
                }
            }
        }
        return false;
    }

    public void RegionCheck(Player player) {

        if (player.hasPermission("taxfreeregion.noclear")) {
            return;
        }

        Location loc = player.getLocation();

        List<Region> list = regions.get(loc.getWorld().getName());

        SavedInventory inventory = null;
        String playerName = player.getName();

        if (list == null) {
            // is outside
            inventory = outsideInventories.get(playerName);
            if (inventory != null) {
                // save the inventory of the region the player was
                
                
                InventoryManager.setInventoryContent(inventory, player.getInventory());
                outsideInventories.remove(playerName);
                
                player.updateInventory();
            }

            return;
        }

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        Iterator<Region> it = list.iterator();

        while (it.hasNext()) {
            Region region = it.next();

            // exit as soon as It can as the list is sorted !
            if (x > region.getX1()) {
                break;
            }

            // check if inside
            if (region.contains(x, y, z)) {
                if (!outsideInventories.containsKey(playerName)) {
                    outsideInventories.put(playerName, InventoryManager.getInventoryContent(player.getInventory()));
                    
                    

                    player.updateInventory();

                    String msg = messages.getMessage("welcome");
                    msg = Messages.setField(msg, "%name%", region.getName());
                    player.sendMessage(msg);
                }
                // exit directly
                return;
            }
        }

        // not inside
        inventory = outsideInventories.get(playerName);
        if (inventory != null) {

            InventoryManager.setInventoryContent(inventory, player.getInventory());
            outsideInventories.remove(playerName);

            player.updateInventory();

            player.sendMessage(messages.getMessage("bye"));
        }
    }

    public boolean isPlayerInsideRegion(Player player) {
        return outsideInventories.containsKey(player.getName());
    }
    
    
}
