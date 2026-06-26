package org.catsdev.permstaff.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PslCommand implements CommandExecutor, TabCompleter {

    private final LuckPerms luckPerms;

    public PslCommand(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("permstaff.psl")) {
            sender.sendMessage("§cУ вас нет прав.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("§cИспользование: /psl <игрок> <группа>");
            return true;
        }

        String playerName = args[0];
        String groupName = args[1];

        CompletableFuture<UUID> uuidFuture = luckPerms.getUserManager().lookupUniqueId(playerName);
        uuidFuture.thenAcceptAsync(uuid -> {
            if (uuid == null) {
                sender.sendMessage("§cИгрок §e" + playerName + " §cне найден.");
                return;
            }
            luckPerms.getUserManager().loadUser(uuid).thenAcceptAsync(user -> {
                user.data().add(InheritanceNode.builder(groupName).build());
                luckPerms.getUserManager().saveUser(user).join();
                sender.sendMessage("§aИгрок §e" + playerName + " §aдобавлен в группу §e" + groupName + "§a.");
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
            return luckPerms.getGroupManager().getLoadedGroups().stream()
                .map(Group::getName)
                .filter(name -> name.toLowerCase().startsWith(prefix))
                .collect(Collectors.toList());
        }
        return List.of();
    }
}
