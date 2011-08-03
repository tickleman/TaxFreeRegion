package com.creadri.util.inventory;

import java.io.Serializable;
import org.bukkit.inventory.ItemStack;

public class SavedInventory
  implements Serializable
{
  private int[] typeIds;
  private short[] damageIds;
  private int[] quantities;
  private int size;

  public SavedInventory(int size)
  {
    this.size = size;
    this.typeIds = new int[size];
    this.damageIds = new short[size];
    this.quantities = new int[size];
  }

  public void setItem(int index, ItemStack is) {
    if (is != null) {
      this.typeIds[index] = is.getTypeId();
      this.damageIds[index] = is.getDurability();
      this.quantities[index] = is.getAmount();
    } else {
      this.typeIds[index] = -1;
    }
  }

  public ItemStack getNewStackFrom(int index) {
    if ((this.typeIds[index] == -1) || (this.typeIds[index] == 0)) {
      return null;
    }
    return new ItemStack(this.typeIds[index], this.quantities[index], this.damageIds[index]);
  }

  public int getSize()
  {
    return this.size;
  }

  public short[] getDamageIds() {
    return this.damageIds;
  }

  public void setDamageIds(short[] damageIds) {
    this.damageIds = damageIds;
  }

  public int[] getQuantities() {
    return this.quantities;
  }

  public void setQuantities(int[] quantities) {
    this.quantities = quantities;
  }

  public int[] getTypeIds() {
    return this.typeIds;
  }

  public void setTypeIds(int[] typeIds) {
    this.typeIds = typeIds;
  }
}