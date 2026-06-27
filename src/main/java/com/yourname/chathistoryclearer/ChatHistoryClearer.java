package com.yourname.chathistoryclearer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatHistoryClearer extends JavaPlugin implements Listener, CommandExecutor {

    @Override
    public void onEnable() {
        // Регистрируем слушатель событий
        getServer().getPluginManager().registerEvents(this, this);
        
        // Регистрируем команду /clearchat
        getCommand("clearchat").setExecutor(this);
        
        getLogger().info("✅ ChatHistoryClearer включен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("❌ ChatHistoryClearer выключен!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("clearchat")) {
            return false;
        }

        // Проверка прав
        if (!sender.hasPermission("chathistoryclearer.use")) {
            sender.sendMessage("§c§l⚠ §cУ вас нет прав для использования этой команды!");
            return true;
        }

        if (args.length == 0) {
            // Очистить чат для всех игроков
            clearChatForAll(sender);
        } else if (args.length == 1) {
            // Очистить чат для конкретного игрока
            Player target = getServer().getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage("§c§l⚠ §cИгрок §e" + args[0] + " §cне найден или не онлайн!");
                return true;
            }
            
            // Проверка на bypass
            if (target.hasPermission("chathistoryclearer.bypass")) {
                sender.sendMessage("§c§l⚠ §cВы не можете очистить чат у этого игрока!");
                return true;
            }
            
            clearChatForPlayer(target);
            sender.sendMessage("§a§l✓ §aИстория чата очищена для игрока §e" + target.getName());
        } else {
            sender.sendMessage("§cИспользование: /clearchat [игрок]");
            return false;
        }

        return true;
    }

    /**
     * Очистить чат для всех игроков
     */
    private void clearChatForAll(CommandSender sender) {
        for (Player player : getServer().getOnlinePlayers()) {
            // Пропускаем игроков с правом bypass
            if (player.hasPermission("chathistoryclearer.bypass")) {
                continue;
            }
            clearChatForPlayer(player);
        }
        sender.sendMessage("§a§l✓ §aИстория чата очищена для всех игроков!");
        getLogger().info("🧹 Глобальная очистка чата выполнена: " + sender.getName());
    }

    /**
     * Очистить чат для конкретного игрока
     */
    private void clearChatForPlayer(Player player) {
        for (int i = 0; i < 100; i++) {
            player.sendMessage("");
        }
        getLogger().info("🧹 Очищена история чата для: " + player.getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Автоматически очищаем историю чата при выходе
        clearChatForPlayer(event.getPlayer());
    }
}
