package com.creadri.taxfreeregion;

import com.creadri.util.Messages;
import com.creadri.util.inventory.InventoryManager;
import com.creadri.util.inventory.SavedInventory;
import com.nijiko.permissions.PermissionHandler;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TaxFreeRegion extends JavaPlugin
{
  private final TaxFreeRegionPluginListener pluginListener = new TaxFreeRegionPluginListener(this);
  private final TaxFreeRegionPlayerListener playerListener = new TaxFreeRegionPlayerListener(this);
  public static final Logger log = Logger.getLogger("Minecraft");
  public static Messages messages = new Messages();
  private boolean eventRegistered;
  private WorldEditPlugin worldEdit;
  public static PermissionHandler permissionHandler;
  private HashMap<String, List<Region>> regions;
  private HashMap<String, SavedInventory> inventories;
  private ArrayList<String> blacklistCommands;
  private ArrayList<String> whitelistCommands;
  private File blacklistFile;
  private File whitelistFile;
  private File regionFile;
  private File inventoryFile;

  public void onEnable()
  {
    try
    {
      File directory = new File("./plugins/TaxFreeRegion");
      if (!directory.exists()) {
        directory.mkdirs();
      }

      this.regionFile = new File(directory, "regions.ser");
      if ((!this.regionFile.exists()) || (!this.regionFile.isFile())) {
        this.regionFile.createNewFile();
      }

      this.inventoryFile = new File(directory, "inventories.ser");
      if ((!this.inventoryFile.exists()) || (!this.inventoryFile.isFile())) {
        this.inventoryFile.createNewFile();
      }

      this.blacklistFile = new File(directory, "CommandsBlacklist.txt");
      if ((!this.blacklistFile.exists()) || (!this.blacklistFile.isFile())) {
        this.blacklistFile.createNewFile();
      }

      this.whitelistFile = new File(directory, "CommandsWhitelist.txt");
      if ((!this.whitelistFile.exists()) || (!this.whitelistFile.isFile())) {
        this.whitelistFile.createNewFile();
      }

      File msgFile = new File(directory, "messages.properties");
      if ((!msgFile.exists()) || (!msgFile.isFile())) {
        msgFile.createNewFile();
      }

      messages.loadMessages(msgFile);
    }
    catch (IOException ex) {
      log.log(Level.SEVERE, "[TaxFreeRegion] : Error on Region File");
      return;
    }

    if (!this.eventRegistered) {
      PluginManager pm = getServer().getPluginManager();
      pm.registerEvent(Event.Type.PLUGIN_ENABLE, this.pluginListener, Event.Priority.Monitor, this);
      pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Monitor, this);
      pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Event.Priority.Monitor, this);
      pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, this.playerListener, Event.Priority.Monitor, this);
  		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
   		pm.registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, this.playerListener, Event.Priority.Normal, this);

      this.eventRegistered = true;
    }

    loadRegions(this.regionFile);
    loadInventories(this.inventoryFile);
    loadCommandsBlackList(this.blacklistFile);
    loadCommandsWhiteList(this.whitelistFile);

    log.log(Level.INFO, new StringBuilder().append("[TaxFreeRegion] Version ").append(getDescription().getVersion()).append(" is enabled!").toString());
  }

  public void onDisable()
  {
    saveInventories(this.inventoryFile);

    log.log(Level.INFO, "[TaxFreeRegion] is disabled!");
  }

  public WorldEditPlugin getWorldEdit() {
    return this.worldEdit;
  }

  public void setWorldEdit(WorldEditPlugin worldEdit) {
    this.worldEdit = worldEdit;
  }

  public boolean isWorldEditSet() {
    return this.worldEdit != null;
  }

  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length < 1) {
      return false;
    }

    if ((sender instanceof Player))
    {
      Player player = (Player)sender;

      if (
      	!player.hasPermission("taxfreeregion.use")
      	&& !permissionHandler.has(player,  "taxfreeregion.use")
      ) {
        player.sendMessage(messages.getMessage("noPermission"));
        return true;
      }

      if ((args.length >= 2) && (args[0].equalsIgnoreCase("add")))
      {
        StringBuilder sb = new StringBuilder(args[1]);
        for (int i = 2; i < args.length; i++) {
          sb.append(" ");
          sb.append(args[i]);
        }
        String regionName = sb.toString();

        if (this.worldEdit == null) {
          player.sendMessage(messages.getMessage("noWorldEdit"));
          return true;
        }

        if (deleteRegionByName(regionName)) {
          player.sendMessage(messages.getMessage("regionOverwriting"));
        }

        Selection sel = this.worldEdit.getSelection(player);

        if (sel == null) {
          player.sendMessage(messages.getMessage("noSelection"));
          return true;
        }

        Location max = sel.getMaximumPoint();
        Location min = sel.getMinimumPoint();

        if ((max == null) || (min == null)) {
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

        saveRegions(this.regionFile);

        player.sendMessage(messages.getMessage("regionAdded"));
      }
      else if ((args.length == 1) && (args[0].equalsIgnoreCase("list")))
      {
        if (this.regions.isEmpty()) {
          player.sendMessage(messages.getMessage("noRegion"));
          return true;
        }

        String worldColor = messages.getMessage("listWorldColor");
        String regionColor = messages.getMessage("listRegionColor");

        Iterator it = this.regions.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry entry = (Map.Entry)it.next();

          player.sendMessage(new StringBuilder().append(worldColor).append((String)entry.getKey()).toString());

          Iterator regit = ((List)entry.getValue()).iterator();
          while (regit.hasNext()) {
            player.sendMessage(new StringBuilder().append(regionColor).append(((Region)regit.next()).toString()).toString());
          }
        }
      }
      else if ((args.length > 1) && (args[0].equalsIgnoreCase("delete")))
      {
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

        saveRegions(this.regionFile);
      } else if ((args.length == 1) && (args[0].equalsIgnoreCase("reload"))) {
        loadCommandsBlackList(this.blacklistFile);
        loadCommandsWhiteList(this.whitelistFile);

        player.sendMessage(messages.getMessage("blackListReloaded"));
        player.sendMessage(messages.getMessage("whiteListReloaded"));
      } else {
        return false;
      }
    }

    return true;
  }

  private void loadRegions(File file)
  {
    ObjectInputStream ois = null;
    try
    {
      ois = new ObjectInputStream(new FileInputStream(file));

      this.regions = ((HashMap)ois.readObject());
    }
    catch (EOFException ex) {
      this.regions = new HashMap();
    } catch (Exception ex) {
      log.log(Level.WARNING, "[TaxFreeRegion] Region file error !");
      this.regions = new HashMap();
    } finally {
      if (ois != null)
        try {
          ois.close();
        } catch (IOException ex) {
          log.log(Level.WARNING, "[TaxFreeRegion] Region file error on close !");
        }
    }
  }

  private void saveRegions(File file)
  {
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(new FileOutputStream(file));

      oos.writeObject(this.regions);
    } catch (Exception ex) {
      log.log(Level.SEVERE, "[TaxFreeRegion] Region file not saved !");
    } finally {
      if (oos != null)
        try {
          oos.close();
        } catch (IOException ex) {
          log.log(Level.WARNING, "[TaxFreeRegion] Region file error on close !");
        }
    }
  }

  private void loadInventories(File file)
  {
    ObjectInputStream ois = null;
    try
    {
      ois = new ObjectInputStream(new FileInputStream(file));

      this.inventories = ((HashMap)ois.readObject());
    }
    catch (EOFException ex) {
      this.inventories = new HashMap();
    } catch (Exception ex) {
      log.log(Level.WARNING, "[TaxFreeRegion] Inventories file error !");
      this.inventories = new HashMap();
    } finally {
      if (ois != null)
        try {
          ois.close();
        } catch (IOException ex) {
          log.log(Level.WARNING, "[TaxFreeRegion] Inventory file error on close !");
        }
    }
  }

  private void saveInventories(File file)
  {
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(new FileOutputStream(file));

      oos.writeObject(this.inventories);
    } catch (Exception ex) {
      log.log(Level.SEVERE, "[TaxFreeRegion] Inventory file not saved !");
    } finally {
      if (oos != null)
        try {
          oos.close();
        } catch (IOException ex) {
          log.log(Level.WARNING, "[TaxFreeRegion] Inventory file error on close !");
        }
    }
  }

  private void loadCommandsBlackList(File file)
  {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(file));

      this.blacklistCommands = new ArrayList();
      String line;
      while ((line = br.readLine()) != null) {
        line = line.trim();
        if ((!line.isEmpty()) || (!line.startsWith("#")))
          this.blacklistCommands.add(line);
      }
    }
    catch (Exception ex)
    {
      log.log(Level.WARNING, "[TaxFreeRegion] Blacklist file error!");
    } finally {
      if (br != null)
        try {
          br.close();
        } catch (IOException ex) {
          log.log(Level.WARNING, "[TaxFreeRegion] Blacklist file error on close !");
        }
    }
  }

  private void loadCommandsWhiteList(File file)
  {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(file));

      this.whitelistCommands = new ArrayList();
      String line;
      while ((line = br.readLine()) != null) {
        line = line.trim();
        if ((!line.isEmpty()) || (!line.startsWith("#")))
          this.whitelistCommands.add(line);
      }
    }
    catch (Exception ex)
    {
      log.log(Level.WARNING, "[TaxFreeRegion] Whitelist file error!");
    } finally {
      if (br != null)
        try {
          br.close();
        } catch (IOException ex) {
          log.log(Level.WARNING, "[TaxFreeRegion] Whitelist file error on close !");
        }
    }
  }

  public boolean isCommandBlackListed(String command)
  {
    Iterator it = this.blacklistCommands.iterator();
    while (it.hasNext()) {
    	String itEquals = (String)it.next();
    	String itWithParams = itEquals + " ";
    	if (command.startsWith(itWithParams) || command.equals(itEquals)) {
        return true;
      }
    }
    return false;
  }

  public boolean isCommandWhiteListed(String command)
  {
    Iterator it = this.whitelistCommands.iterator();

    while (it.hasNext()) {
    	String itEquals = (String)it.next();
    	String itWithParams = itEquals + " ";
    	if (command.startsWith(itWithParams) || command.equals(itEquals)) {
        return true;
      }
    }
    return false;
  }

  private void addRegion(Region region, String world) {
    List list = (List)this.regions.get(world);

    if (list == null) {
      list = new LinkedList();
      list.add(region);
      this.regions.put(world, list);
    } else {
      list.add(region);
    }

    Collections.sort(list);
  }

  private boolean deleteRegionByName(String name) {
    Iterator it = this.regions.values().iterator();
    while (it.hasNext()) {
      Iterator regit = ((List)it.next()).iterator();
      while (regit.hasNext()) {
        Region region = (Region)regit.next();
        if (region.getName().equalsIgnoreCase(name)) {
          regit.remove();
          return true;
        }
      }
    }
    return false;
  }

  private Region getRegionByName(String name) {
    Iterator it = this.regions.values().iterator();
    while (it.hasNext()) {
      Iterator regit = ((List)it.next()).iterator();
      while (regit.hasNext()) {
        Region region = (Region)regit.next();
        if (region.getName().equalsIgnoreCase(name)) {
          return region;
        }
      }
    }
    return null;
  }

  public void RegionCheck(Player player)
  {
    if (
    	player.hasPermission("taxfreeregion.noclear")
    	|| permissionHandler.has(player,  "taxfreeregion.noclear")
    ) {
      return;
    }

    Location loc = player.getLocation();

    List list = (List)this.regions.get(loc.getWorld().getName());

    SavedInventory inventory = null;
    String playerName = player.getName();

    if (list == null) {
      inventory = (SavedInventory)this.inventories.get(playerName);
      if (inventory != null) {
        InventoryManager.setInventoryContent(inventory, player.getInventory());
        this.inventories.remove(playerName);

        player.updateInventory();
      }

      return;
    }

    int x = loc.getBlockX();
    int y = loc.getBlockY();
    int z = loc.getBlockZ();

    Iterator it = list.iterator();

    while (it.hasNext()) {
      Region region = (Region)it.next();

      if (x > region.getX1())
      {
        break;
      }

      if (region.contains(x, y, z)) {
        if (!this.inventories.containsKey(playerName)) {
          this.inventories.put(playerName, InventoryManager.getInventoryContent(player.getInventory()));
          InventoryManager.clearInventory(player.getInventory());

          player.updateInventory();

          String msg = messages.getMessage("welcome");
          msg = Messages.setField(msg, "%name%", region.getName());
          player.sendMessage(msg);
        }

        return;
      }

    }

    inventory = (SavedInventory)this.inventories.get(playerName);
    if (inventory != null)
    {
      InventoryManager.setInventoryContent(inventory, player.getInventory());
      this.inventories.remove(playerName);

      player.updateInventory();

      player.sendMessage(messages.getMessage("bye"));
    }
  }

  public boolean isPlayerInsideRegion(Player player) {
    return this.inventories.containsKey(player.getName());
  }
}