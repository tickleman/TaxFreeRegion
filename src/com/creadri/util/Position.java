package com.creadri.util;

import java.io.Serializable;

public class Position
  implements Comparable<Position>, Serializable
{
  int x;
  int y;
  int z;
  String world;

  public Position(int x, int y, int z, String world)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.world = world;
  }

  public Position() {
  }

  public String getWorld() {
    return this.world;
  }

  public void setWorld(String world) {
    this.world = world;
  }

  public int getX() {
    return this.x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return this.y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getZ() {
    return this.z;
  }

  public void setZ(int z) {
    this.z = z;
  }

  public int compareTo(int x, int y, int z, String world) {
    return this.x - x + (this.y - y) + (this.z - z) + this.world.compareTo(world);
  }

  public int compareTo(Position o)
  {
    return this.x - o.x + (this.y - o.y) + (this.z - o.z) + this.world.compareTo(this.world);
  }

  public boolean equals(int x, int y, int z, String world) {
    return (this.x == x) && (this.y == y) && (this.z == z) && (this.world.compareTo(world) == 0);
  }

  public boolean equals(Object obj)
  {
    if (!(obj instanceof Position)) {
      return false;
    }
    Position p = (Position)obj;

    return (this.x == p.x) && (this.y == p.y) && (this.z == p.z) && (this.world.compareTo(p.world) == 0);
  }

  public int hashCode()
  {
    int hash = 7;
    hash = 53 * hash + this.x;
    hash = 53 * hash + this.y;
    hash = 53 * hash + this.z;
    hash = 53 * hash + (this.world != null ? this.world.hashCode() : 0);
    return hash;
  }
}