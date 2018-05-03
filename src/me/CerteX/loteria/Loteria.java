package me.CerteX.loteria;

import java.lang.StringBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.darkblade12.particledemo.particle.ParticleEffect;

public class Loteria extends JavaPlugin implements Listener {
	
	ItemStack los = Utils.parseItemStack(this.getConfig().getString("Loteria.Items.los"));
	private Color getColor(int i) {
		Color c = null;
		switch(i){
			case 1:
				c=Color.AQUA;
			case 2:
				c=Color.BLACK;
			case 3:
				c=Color.BLUE;
			case 4:
				c=Color.FUCHSIA;
			case 5:
				c=Color.GRAY;
			case 6:
				c=Color.GREEN;
			case 7:
				c=Color.LIME;
			case 8:
				c=Color.MAROON;
			case 9:
				c=Color.NAVY;
			case 10:
				c=Color.OLIVE;
			case 11:
				c=Color.ORANGE;
			case 12:
				c=Color.PURPLE;
			case 13:
				c=Color.RED;
			case 14:
				c=Color.SILVER;
			case 15:
				c=Color.TEAL;
			case 16:
				c=Color.WHITE;
			case 17:
				c=Color.YELLOW;
		} 
		return c;
		}
	
	public void spawnRandomPrize(Player player) throws InterruptedException{
		List<Integer> chance= new ArrayList<Integer>();
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (String itemstack : this.getConfig().getStringList("Loteria.Items.drop")){
			items.add(Utils.parseItemStack(itemstack));
		}
		for (Integer ch : this.getConfig().getIntegerList("Loteria.Items.chance")){
			chance.add(ch);
		}
		int c = new Random().nextInt(10000) + 1;
		int idxc = 0;
		while (c <= chance.get(idxc)){
			idxc++;
		}
		ItemStack prize = items.get(idxc - 1);
		String cho = Integer.toString(chance.get(idxc - 1) - chance.get(idxc));
		cho = new StringBuffer(cho).insert(cho.length() - 2, ",").toString();
		Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        Random r = new Random();   
        int rt = r.nextInt(4) + 1;
        Type type = Type.BALL;       
        if (rt == 1) type = Type.BALL;
        if (rt == 2) type = Type.BALL_LARGE;
        if (rt == 3) type = Type.BURST;
        if (rt == 4) type = Type.CREEPER;
        if (rt == 5) type = Type.STAR;  
        int r1i = r.nextInt(17) + 1;
        int r2i = r.nextInt(17) + 1;
        Color c1 = getColor(r1i);
        Color c2 = getColor(r2i);
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
        fwm.addEffect(effect);
        fwm.setPower(2);
        fw.setFireworkMeta(fwm);
        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
        	public void run(){
        		player.getWorld().dropItemNaturally(fw.getLocation(), prize);
        	}
        }, 40L);
		//player.getWorld().dropItem(fw.getLocation(), prize);
		Bukkit.broadcastMessage(ChatColor.GREEN + "Gracz " + ChatColor.BLUE + player.getDisplayName() + ChatColor.GREEN + " otrzyma³ " + ChatColor.BLUE + String.valueOf(prize.getType()).replace('_', ' ') + ChatColor.GREEN + " w iloœci " + ChatColor.BLUE + prize.getAmount() + ChatColor.GREEN + " i mia³ na to " + ChatColor.BLUE + cho + "%" + ChatColor.GREEN + " szansy");
	}
	@Override
	public void onEnable(){
		new Enchantments();
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("loteria").setExecutor(this);
	}
	
	@EventHandler
	public void interaction(PlayerInteractEvent event) throws InterruptedException{
		Player player = event.getPlayer();
		Action action = event.getAction();
		if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)){
			if(player.getItemInHand().isSimilar(los)){
				ParticleEffect.CLOUD.display(4, 4, 4, 1, 4000, player.getLocation(), 32);
				spawnRandomPrize(player);
				if (player.getItemInHand().getAmount() > 1){
					player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
				}
				else
					player.setItemInHand(new ItemStack(Material.AIR));
			}
		}
	}



	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("loteria") && sender.hasPermission("loteria.give")){
			if(args.length == 2){
				los.setAmount(Integer.parseInt(args[1]));
				Player target = Bukkit.getPlayer(args[0]);
				PlayerInventory inventory = target.getInventory();
				inventory.addItem(los);
				sender.sendMessage(ChatColor.GREEN + "Gracz " + ChatColor.BLUE + args[0] + ChatColor.GREEN + " otrzyma³ los w iloœci " + ChatColor.BLUE + args[1]);
			}
			else{
				sender.sendMessage(ChatColor.RED + "Poprawne u¿ycie: /Loteria [gracz] [iloœæ]");
			}
		}
		if (label.equalsIgnoreCase("loteriainfo") && sender.hasPermission("loteria.info")){
			List<Integer> chance= new ArrayList<Integer>();
			List<ItemStack> items = new ArrayList<ItemStack>();
			for (String itemstack : this.getConfig().getStringList("Loteria.Items.drop")){
				items.add(Utils.parseItemStack(itemstack));
			}
			for (Integer ch : this.getConfig().getIntegerList("Loteria.Items.chance")){
				chance.add(ch);
			}
			int o = items.size();
			int b = o;
			sender.sendMessage(ChatColor.GREEN + "Masz szanse otrzymaæ:");
			while (o > 0){
				String chb = Integer.toString(chance.get(b - o + 1) - chance.get(b - o));
				chb = new StringBuffer(chb).insert(chb.length() - 2, ",").toString();
				sender.sendMessage(ChatColor.BLUE + String.valueOf(items.get(b - o).getType()).replace('_', ' ') + ChatColor.GREEN + " w iloœci " + ChatColor.BLUE + items.get(b - o).getAmount() + ChatColor.GREEN + " z szans¹ " + ChatColor.BLUE + chb + "%");
				o--;
			}
		}
		return false;
	}
}
