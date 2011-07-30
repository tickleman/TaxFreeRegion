package com.creadri.taxfreeregion;

import com.creadri.util.inventory.InventoryManager;
import com.creadri.util.inventory.SavedInventory;
import java.io.Serializable;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author creadri
 */
public class Region implements Comparable<Region>, Serializable {

    private static final int NOMANSLANDSIZE = 4;
    
    private String name;
    // exterior box
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    private int z1;
    private int z2;
    // interior box
    private int ix1;
    private int ix2;
    private int iy1;
    private int iy2;
    private int iz1;
    private int iz2;
    // priviosly savec inventories
    private HashMap<Player, SavedInventory> inventories;

    public Region() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        if (x1 < x2) {
            this.x1 = x2;
            this.x2 = x1;
        }
        
        if (x1 >= 0) {
            ix1 = x1 - NOMANSLANDSIZE;
        } else {
            ix1 = x1 + NOMANSLANDSIZE;
        }
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        if (x2 > x1) {
            this.x2 = x1;
            this.x1 = x2;
        }
        
        if (x2 >= 0) {
            ix2 = x2 + NOMANSLANDSIZE;
        } else {
            ix2 = x2 - NOMANSLANDSIZE;
        }
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        if (y1 < y2) {
            this.y1 = y2;
            this.y2 = y1;
        }
        
        if (y1 >= 0) {
            iy1 = y1 - NOMANSLANDSIZE;
        } else {
            iy1 = y1 + NOMANSLANDSIZE;
        }
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        if (y2 > y1) {
            this.y2 = y1;
            this.y1 = y2;
        }
        
        if (y2 >= 0) {
            iy2 = y2 + NOMANSLANDSIZE;
        } else {
            iy2 = y2 - NOMANSLANDSIZE;
        }
    }

    public int getZ1() {
        return z1;
    }

    public void setZ1(int z1) {
        if (z1 < z2) {
            this.z1 = z2;
            this.z2 = z1;
        }
        
        if (z1 >= 0) {
            iz1 = z1 - NOMANSLANDSIZE;
        } else {
            iz1 = z1 + NOMANSLANDSIZE;
        }
    }

    public int getZ2() {
        return z2;
    }

    public void setZ2(int z2) {
        if (z2 > z1) {
            this.z2 = z1;
            this.z1 = z2;
        }
        
        if (z2 >= 0) {
            iz2 = z2 + NOMANSLANDSIZE;
        } else {
            iz2 = z2 - NOMANSLANDSIZE;
        }
    }

    public HashMap<Player, SavedInventory> getInventories() {
        return inventories;
    }

    public void setInventories(HashMap<Player, SavedInventory> inventories) {
        this.inventories = inventories;
    }

    public int getIx1() {
        return ix1;
    }

    public void setIx1(int ix1) {
        this.ix1 = ix1;
    }

    public int getIx2() {
        return ix2;
    }

    public void setIx2(int ix2) {
        this.ix2 = ix2;
    }

    public int getIy1() {
        return iy1;
    }

    public void setIy1(int iy1) {
        this.iy1 = iy1;
    }

    public int getIy2() {
        return iy2;
    }

    public void setIy2(int iy2) {
        this.iy2 = iy2;
    }

    public int getIz1() {
        return iz1;
    }

    public void setIz1(int iz1) {
        this.iz1 = iz1;
    }

    public int getIz2() {
        return iz2;
    }

    public void setIz2(int iz2) {
        this.iz2 = iz2;
    }

    @Override
    public String toString() {
        return String.format("[%s] (%d, %d, %d) (%d, %d, %d)", name, x1, y1, z1, x2, y2, z2);
    }

    public boolean contains(Location loc) {

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return ((x >= x2 && x <= x1) && (z >= z2 && z <= z1) && (y >= y2 && y <= y1));
    }
    
    public boolean contains(int x, int y, int z) {
        return ((x >= x2 && x <= x1) && (z >= z2 && z <= z1) && (y >= y2 && y <= y1));
    }
    
    public boolean isInNoMansLand(int x, int y, int z) {
        return ((x >= ix2 && x <= ix1) && (z >= iz2 && z <= iz1) && (y >= iy2 && y <= iy1));
    }
    
    public boolean isInNoMansLand(Location loc) {

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return ((x >= ix2 && x <= ix1) && (z >= iz2 && z <= iz1) && (y >= iy2 && y <= iy1));
    }

    @Override
    public int compareTo(Region o) {
        if (o.x1 > x1) {
            return 1;
        } else if (o.x1 == x1) {
            
            if (o.z1 > z1) {
                return 1;
            } else if (o.z1 == z1) {
                
                if (o.y1 > y1) {
                    return 1;
                } else if (o.y1 == y1) {
                    return 0;
                }
            }
        }
        
        return -1;
    }
    
    
    public SavedInventory getInventory(Player player) {
        if (inventories == null) {
            return null;
        }
        
        return inventories.get(player);
    }
    
    
    public void saveInventory(Player player) {
        if (inventories == null) {
            inventories = new HashMap<Player, SavedInventory>();
        }
        
        inventories.put(player, InventoryManager.getInventoryContent(player.getInventory()));
    }
}
