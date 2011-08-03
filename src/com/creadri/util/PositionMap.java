package com.creadri.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class PositionMap<T>
  implements Serializable
{
  private HashMap<Position, T> map;
  private Position position;

  public PositionMap()
  {
    this.position = new Position();
    this.map = new HashMap();
  }

  public void put(int x, int y, int z, String world, T object) {
    Position pos = new Position(x, y, z, world);

    this.map.put(pos, object);
  }

  public void put(Position key, T object) {
    this.map.put(key, object);
  }

  public boolean containsPosition(Position key) {
    return this.map.containsKey(key);
  }

  public boolean containsPosition(int x, int y, int z, String world) {
    this.position.setX(x);
    this.position.setY(y);
    this.position.setZ(z);
    this.position.setWorld(world);

    return this.map.containsKey(this.position);
  }

  public T get(Position key) {
    return this.map.get(key);
  }

  public T get(int x, int y, int z, String world) {
    this.position.setX(x);
    this.position.setY(y);
    this.position.setZ(z);
    this.position.setWorld(world);

    return this.map.get(this.position);
  }

  public Collection<T> values() {
    return this.map.values();
  }

  public void remove(Position key) {
    this.map.remove(key);
  }

  public void remove(int x, int y, int z, String world) {
    this.position.setX(x);
    this.position.setY(y);
    this.position.setZ(z);
    this.position.setWorld(world);

    this.map.remove(this.position);
  }

  public int size() {
    return this.map.size();
  }
}