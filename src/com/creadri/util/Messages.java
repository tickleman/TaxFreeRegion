package com.creadri.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.bukkit.ChatColor;

public class Messages
{
  private Properties msgs;

  public static String replaceColor(String input)
  {
    input = input.replaceAll("%aqua%", ChatColor.AQUA.toString());
    input = input.replaceAll("%black%", ChatColor.BLACK.toString());
    input = input.replaceAll("%blue%", ChatColor.BLUE.toString());
    input = input.replaceAll("%darkaqua%", ChatColor.DARK_AQUA.toString());
    input = input.replaceAll("%darkblue%", ChatColor.DARK_BLUE.toString());
    input = input.replaceAll("%darkgray%", ChatColor.DARK_GRAY.toString());
    input = input.replaceAll("%darkgreen%", ChatColor.DARK_GREEN.toString());
    input = input.replaceAll("%darkpurple%", ChatColor.DARK_PURPLE.toString());
    input = input.replaceAll("%darkred%", ChatColor.DARK_RED.toString());
    input = input.replaceAll("%gold%", ChatColor.GOLD.toString());
    input = input.replaceAll("%gray%", ChatColor.GRAY.toString());
    input = input.replaceAll("%green%", ChatColor.GREEN.toString());
    input = input.replaceAll("%lightpurple%", ChatColor.LIGHT_PURPLE.toString());
    input = input.replaceAll("%red%", ChatColor.RED.toString());
    input = input.replaceAll("%white%", ChatColor.WHITE.toString());
    input = input.replaceAll("%yellow%", ChatColor.YELLOW.toString());

    return input;
  }

  public static String setField(String input, String field, String value) {
    return input.replaceAll(field, value);
  }

  public Messages() {
    this.msgs = new Properties();
  }

  public void loadMessages(File propertyFile) throws IOException
  {
    this.msgs.load(new FileInputStream(propertyFile));

    Iterator it = this.msgs.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry)it.next();

      entry.setValue(replaceColor((String)entry.getValue()));
    }
  }

  public String getMessage(String key) {
    if (this.msgs == null) {
      return null;
    }
    return this.msgs.getProperty(key, key + " message not set");
  }
}