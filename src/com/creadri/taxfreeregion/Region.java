package com.creadri.taxfreeregion;

import java.io.Serializable;
import org.bukkit.Location;

public class Region
  implements Comparable<Region>, Serializable
{
  private String name;
  private int x1;
  private int x2;
  private int y1;
  private int y2;
  private int z1;
  private int z2;

  public String getName()
  {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getX1() {
    return this.x1;
  }

  public void setX1(int x1) {
    this.x1 = x1;
  }

  public int getX2()
  {
    return this.x2;
  }

  public void setX2(int x2) {
    this.x2 = x2;
  }

  public int getY1()
  {
    return this.y1;
  }

  public void setY1(int y1) {
    this.y1 = y1;
  }

  public int getY2()
  {
    return this.y2;
  }

  public void setY2(int y2) {
    this.y2 = y2;
  }

  public int getZ1()
  {
    return this.z1;
  }

  public void setZ1(int z1) {
    this.z1 = z1;
  }

  public int getZ2()
  {
    return this.z2;
  }

  public void setZ2(int z2) {
    this.z2 = z2;
  }

  public String toString()
  {
    return String.format("[%s] (%d, %d, %d) (%d, %d, %d)", new Object[] { this.name, Integer.valueOf(this.x1), Integer.valueOf(this.y1), Integer.valueOf(this.z1), Integer.valueOf(this.x2), Integer.valueOf(this.y2), Integer.valueOf(this.z2) });
  }

  public boolean contains(Location loc)
  {
    int x = loc.getBlockX();
    int y = loc.getBlockY();
    int z = loc.getBlockZ();

    return (x >= this.x2) && (x <= this.x1) && (z >= this.z2) && (z <= this.z1) && (y >= this.y2) && (y <= this.y1);
  }

  public boolean contains(int x, int y, int z) {
    return (x >= this.x2) && (x <= this.x1) && (z >= this.z2) && (z <= this.z1) && (y >= this.y2) && (y <= this.y1);
  }

  public int compareTo(Region o)
  {
    if (o.x1 > this.x1)
      return 1;
    if (o.x1 == this.x1)
    {
      if (o.z1 > this.z1)
        return 1;
      if (o.z1 == this.z1)
      {
        if (o.y1 > this.y1)
          return 1;
        if (o.y1 == this.y1) {
          return 0;
        }
      }
    }

    return -1;
  }
}