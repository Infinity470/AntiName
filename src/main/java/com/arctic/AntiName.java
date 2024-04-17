package com.arctic;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class AntiName extends JavaPlugin implements Listener {

    private List<String> playerName;
    private boolean isTrue;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("antiname").setExecutor(this::reloadCommand);
        saveDefaultConfig();
        reloadConfiguration();
        getLogger().info(ChatColor.GREEN + "AntiName has been enabled");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String getPlayerName = player.getName().toLowerCase();

        if (playerName.stream().anyMatch(s -> s.contains(getPlayerName))) {
            if (isTrue) {
                punishPlayer(player);
            }
        }
    }


    public boolean reloadCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            reloadConfiguration();
            sender.sendMessage(ChatColor.GREEN + "AntiName config.yml successfully reloaded.");
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /antiname reload");
            return false;
        }
    }

    private void reloadConfiguration() {
        FileConfiguration config = getConfig();
        isTrue = config.getBoolean("pluginEnable", false);
        playerName = config.getStringList("playerName").stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private void punishPlayer(Player player) {
        FileConfiguration config = getConfig();
        String punishCommand = config.getString("punishCommand");
        if (punishCommand != null) {
            String command = punishCommand.replace("%player%", player.getName());
            getServer().getScheduler().runTask(this, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            });
        }
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "AntiName has been disabled");
    }
}
