package me.CerteX.loteria;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils
{
  public static boolean isInt(String s)
  {
    try
    {
      Integer.parseInt(s);
      return true;
    }
    catch (NumberFormatException ex) {}
    return false;
  }
  
  public static String fixColors(String string)
  {
    return ChatColor.translateAlternateColorCodes('&', string);
  }
  
  @SuppressWarnings("deprecation")
public static void giveOrDrop(Player p, ItemStack is, Location loc)
  {
    if (is != null)
    {
      for (ItemStack i : p.getInventory().addItem(new ItemStack[] { is }).values()) {
        loc.getWorld().dropItem(loc, i);
      }
      p.updateInventory();
    }
  }
  public static String replace(String text, String searchString, String replacement)
  {
    if ((text == null) || (text.isEmpty()) || (searchString.isEmpty()) || (replacement == null)) {
      return text;
    }
    int start = 0;
    int max = -1;
    int end = text.indexOf(searchString, start);
    if (end == -1) {
      return text;
    }
    int replacedLength = searchString.length();
    int increase = replacement.length() - replacedLength;
    increase = increase < 0 ? 0 : increase;
    increase *= (max > 64 ? 64 : max < 0 ? 16 : max);
    StringBuilder sb = new StringBuilder(text.length() + increase);
    while (end != -1)
    {
      sb.append(text.substring(start, end)).append(replacement);
      start = end + replacedLength;
      max--;
      if (max == 0) {
        break;
      }
      end = text.indexOf(searchString, start);
    }
    sb.append(text.substring(start));
    return sb.toString();
  }
  @SuppressWarnings("deprecation")
public static ItemStack parseItemStack(String itemStack)
  {
    ItemStack is = new ItemStack(Material.AIR);
    String[] strings = itemStack.split(" ");
    String[] item = strings[0].split(":");
    if (item.length > 1)
    {
      Material m = Material.getMaterial(Integer.parseInt(item[0]));
      is.setType(m);
      is.setDurability(Short.parseShort(item[1]));
    }
    else if (isInt(item[0]))
    {
      Material m = Material.getMaterial(Integer.parseInt(item[0]));
      is.setType(m);
    }
    else
    {
      Material m = Material.getMaterial(item[0]);
      is.setType(m);
    }
    int amount = 1;
    if (isInt(strings[1])) {
      amount = Integer.parseInt(strings[1]);
    }
    is.setAmount(amount);
    for (int i = 2; i < strings.length; i++)
    {
      String s = strings[i];
      String[] trim = s.split(":");
      if (trim.length >= 1) {
        if (trim[0].equalsIgnoreCase("name"))
        {
          ItemMeta im = is.getItemMeta();
          String name = fixColors(replace(trim[1], "_", " "));
          im.setDisplayName(name);
          is.setItemMeta(im);
        }
        else if (trim[0].equalsIgnoreCase("lore"))
        {
          ItemMeta im = is.getItemMeta();
          trim[1] = replace(trim[1], "_", " ");
          String[] lorestring = trim[1].split("&nl");
          List<String> lore = new ArrayList<String>();
          for (String s1 : lorestring) {
            lore.add(fixColors(s1));
          }
          im.setLore(lore);
          is.setItemMeta(im);
        }
        else
        {
          Enchantment e = Enchantments.getEnchantment(trim[0]);
          if (e != null)
          {
            int lvl = Integer.parseInt(trim[1]);
            ItemMeta im = is.getItemMeta();
            is.setItemMeta(im);
            is.addUnsafeEnchantment(e, lvl);
          }
        }
      }
    }
    return is;
  }
}