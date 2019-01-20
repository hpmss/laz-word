package me.hpms.lootworld.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class TaskHandler implements Runnable{
	
	private int taskId;

	public void cancelTask() {
		Bukkit.getScheduler().cancelTask(taskId);
	}
	
	public TaskHandler(JavaPlugin plugin,int delay,int period) {
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, delay, period);
	}
	
	

}
