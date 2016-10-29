package com.gmail.absolutevanillahelp.VanillaEssentials;

import java.io.*;
import java.util.logging.*;
import org.bukkit.configuration.file.*;

public class VEConfiguration {

	private final String fileName;
    private final VanillaEssentials plugin;
    
    private File configFile;
    private FileConfiguration fileConfiguration;
 
    public VEConfiguration(VanillaEssentials plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        File dataFolder = plugin.getDataFolder();
        if (dataFolder == null)
            throw new IllegalStateException();
        this.configFile = new File(plugin.getDataFolder(), fileName);
    }
 
    public void reloadConfig() {        
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        InputStream defConfigStream = plugin.getResource(fileName);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            fileConfiguration.setDefaults(defConfig);
        }
    }
 
    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            this.reloadConfig();
        }
        return fileConfiguration;
    }
 
    public void saveConfig() {
        if (fileConfiguration == null || configFile == null) {
            return;
        } else {
            try {
                getConfig().save(configFile);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
            }
        }
    }
    
    public void saveDefaultConfig() {
        if (!configFile.exists()) {            
            this.plugin.saveResource(fileName, false);
        }
    }
}
