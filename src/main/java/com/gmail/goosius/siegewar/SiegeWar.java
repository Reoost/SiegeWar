package com.gmail.goosius.siegewar;

import com.gmail.goosius.siegewar.settings.SiegeWarSettings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;

import com.gmail.goosius.siegewar.settings.Settings;
import com.gmail.goosius.siegewar.tasks.DynmapTask;
import com.palmergames.bukkit.util.Version;
import com.gmail.goosius.siegewar.command.SiegeWarAdminCommand;
import com.gmail.goosius.siegewar.command.SiegeWarCommand;
import com.gmail.goosius.siegewar.hud.SiegeHUDManager;
import com.gmail.goosius.siegewar.listeners.SiegeWarActionListener;
import com.gmail.goosius.siegewar.listeners.SiegeWarBukkitEventListener;
import com.gmail.goosius.siegewar.listeners.SiegeWarNationEventListener;
import com.gmail.goosius.siegewar.listeners.SiegeWarPlotEventListener;
import com.gmail.goosius.siegewar.listeners.SiegeWarTownEventListener;
import com.gmail.goosius.siegewar.listeners.SiegeWarTownyEventListener;
import com.gmail.goosius.siegewar.listeners.SiegeWarCannonsListener;
import java.io.File;

public class SiegeWar extends JavaPlugin {
	
	private static SiegeWar plugin;
	public static String prefix = "[SiegeWar] ";
	private static Version requiredTownyVersion = Version.fromString("0.96.7.4");
	private final static SiegeHUDManager SiegeHudManager = new SiegeHUDManager(plugin);
	private static boolean cannonsPluginDetected;

	public static SiegeWar getSiegeWar() {
		return plugin;
	}

	public File getSiegeWarJarFile() {
		return getFile();
	}

	public static SiegeHUDManager getSiegeHUDManager() {
		return SiegeHudManager;
	}
	
    @Override
    public void onEnable() {
    	
    	plugin = this;
    	
    	printSickASCIIArt();
    	
        if (!townyVersionCheck(getTownyVersion())) {
            System.err.println(prefix + "Towny version does not meet required minimum version: " + requiredTownyVersion.toString());
            onDisable();
        } else {
            System.out.println(prefix + "Towny version " + getTownyVersion() + " found.");
        }
        
        if (!Settings.loadSettingsAndLang())
        	onDisable();

        registerCommands();
        
        if (Bukkit.getPluginManager().getPlugin("Towny").isEnabled())
        	SiegeController.loadAll();

        Plugin dynmap = Bukkit.getPluginManager().getPlugin("dynmap");
        if (dynmap != null) {
        	System.out.println(prefix + "SiegeWar found Dynmap plugin, enabling Dynmap support.");
        	DynmapTask.setupDynmapAPI((DynmapAPI) dynmap);
        } else {
        	System.out.println(prefix + "Dynmap plugin not found.");
        }

		Plugin cannons = Bukkit.getPluginManager().getPlugin("Cannons");
		if (cannons != null) {
			cannonsPluginDetected = true;
			if(SiegeWarSettings.isCannonsIntegrationEnabled()) {
				System.out.println(prefix + "SiegeWar found Cannons plugin, enabling Cannons support.");
				System.out.println(prefix + "Cannons support enabled.");
			}
		} else {
			cannonsPluginDetected = false;
			System.out.println(prefix + "Cannons plugin not found.");
		}

		registerListeners();

		System.out.println(prefix + "SiegeWar loaded successfully.");
    }
    
    @Override
    public void onDisable() {
    	DynmapTask.endDynmapTask();
    	System.err.println(prefix + "Shutting down....");
    }

	public String getVersion() {
		return this.getDescription().getVersion();
	}
	
    private boolean townyVersionCheck(String version) {
        return Version.fromString(version).compareTo(requiredTownyVersion) >= 0;
    }

    private String getTownyVersion() {
        return Bukkit.getPluginManager().getPlugin("Towny").getDescription().getVersion();
    }
	
	private void registerListeners() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new SiegeWarActionListener(this), this);
		pm.registerEvents(new SiegeWarBukkitEventListener(this), this);		
		pm.registerEvents(new SiegeWarTownyEventListener(this), this);
		pm.registerEvents(new SiegeWarNationEventListener(this), this);
		pm.registerEvents(new SiegeWarTownEventListener(this), this);
		pm.registerEvents(new SiegeWarPlotEventListener(this), this);
		if(cannonsPluginDetected) {
			pm.registerEvents(new SiegeWarCannonsListener(this), this);
		}
	}
	
	private void registerCommands() {
		getCommand("siegewar").setExecutor(new SiegeWarCommand());
		getCommand("siegewaradmin").setExecutor(new SiegeWarAdminCommand());
	}

	private void printSickASCIIArt() {
		System.out.println("  / ____|                        | |  \\ \\        / /        ");
		System.out.println(" | |     _ __ _   _ ___  __ _  __| | __\\ \\  /\\  / /_ _ _ __   ");
		System.out.println(" | |    | '__| | | / __|/ _` |/ _` |/ _ \\ \\/  \\/ / _` | '__| ");
		System.out.println(" | |____| |  | |_| \\__ \\ (_| | (_| |  __/\\  /\\  / (_| | |    ");
		System.out.println("  \\_____|_|   \\__,_|___/\\__,_|\\__,_|\\___| \\/  \\/ \\__,_|_|   ");
		System.out.println("          By Goosius & LlmDl & reoost         ");
		System.out.println("                                      ");
	}

	public static boolean getCannonsPluginDetected() {
		return cannonsPluginDetected;
	}
}
