package com.creadri.util;

import java.io.Serializable;
import org.bukkit.Location;
import org.bukkit.World;

public class EntityPosition
  implements Serializable, Comparable<EntityPosition>
{
  private double x;
  private double y;
  private double z;
  private String world;
  private float yaw;
  private float pitch;

  public EntityPosition()
  {
  }

  public EntityPosition(double x, double y, double z, String world, float yaw, float pitch)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.world = world;
    this.yaw = yaw;
    this.pitch = pitch;
  }

  public EntityPosition(Location location) {
    this.x = location.getX();
    this.y = location.getY();
    this.z = location.getZ();
    this.world = location.getWorld().getName();
    this.yaw = location.getYaw();
    this.pitch = location.getPitch();
  }

  public Location getLocation(World world) {
    return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
  }

  public float getPitch() {
    return this.pitch;
  }

  public void setPitch(float pitch) {
    this.pitch = pitch;
  }

  public String getWorld() {
    return this.world;
  }

  public void setWorld(String world) {
    this.world = world;
  }

  public double getX() {
    return this.x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return this.y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public float getYaw() {
    return this.yaw;
  }

  public void setYaw(float yaw) {
    this.yaw = yaw;
  }

  public double getZ() {
    return this.z;
  }

  public void setZ(double z) {
    this.z = z;
  }

  public boolean equals(Object obj)
  {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    EntityPosition other = (EntityPosition)obj;
    if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
      return false;
    }
    if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
      return false;
    }
    if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
      return false;
    }

    return this.world == null ? other.world == null : this.world.equals(other.world);
  }

  public int hashCode()
  {
    int hash = 7;
    hash = 53 * hash + (int)Double.doubleToLongBits(this.x);
    hash = 53 * hash + (int)Double.doubleToLongBits(this.y);
    hash = 53 * hash + (int)Double.doubleToLongBits(this.z);
    hash = 53 * hash + (this.world != null ? this.world.hashCode() : 0);
    return hash;
  }

  public int compareTo(EntityPosition o)
  {
    return Double.compare(this.x, o.x) + Double.compare(this.y, o.y) + Double.compare(this.z, o.z) + this.world.compareTo(this.world);
  }
}