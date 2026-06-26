package org.catsdev.permstaff.commands;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class UnsetpermCommand implements CommandExecutor, TabCompleter {

    private final LuckPerms luckPerms;

    public UnsetpermCommand(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("permstaff.unsetperm")) {
            sender.sendMessage("§cУ вас нет прав.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("§cИспользование: /unsetperm <игрок> <право>");
            return true;
        }

        String playerName = args[0];
        String perm = args[1];

        CompletableFuture<UUID> uuidFuture = luckPerms.getUserManager().lookupUniqueId(playerName);
        uuidFuture.thenAcceptAsync(uuid -> {
            if (uuid == null) {
                sender.sendMessage("§cИгрок §e" + playerName + " §cне найден.");
                return;
            }
            luckPerms.getUserManager().loadUser(uuid).thenAcceptAsync(user -> {
                user.data().clear(n -> n.getKey().equals(perm));
                luckPerms.getUserManager().saveUser(user).join();
                sender.sendMessage("§aПраво §e" + perm + " §aсброшено (unset) у игрока §e" + playerName + "§a.");
            }).exceptionally(ex -> {
                sender.sendMessage("§cОшибка при загрузке пользователя: " + ex.getCause().getMessage());
                return null;
            });
        }).exceptionally(ex -> {
            sender.sendMessage("§cОшибка при поиске игрока: " + ex.getCause().getMessage());
            return null;
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(prefix))
                .collect(Collectors.toList());
        }
        if (args.length == 2) {
            String prefix = args[1].toLowerCase();
            List<String> perms = new ArrayList<>();
            for (Permission p : Bukkit.getPluginManager().getPermissions()) {
                if (p.getName().toLowerCase().startsWith(prefix)) {
                    perms.add(p.getName());
                }
            }
            return perms;
        }
        return List.of();
    }
}
