package org.catsdev.permstaff.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetpermselfCommand implements CommandExecutor, TabCompleter {

    private final LuckPerms luckPerms;

    public SetpermselfCommand(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("permstaff.setpermself")) {
            sender.sendMessage("§cУ вас нет прав.");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда только для игроков.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("§cИспользование: /setpermself <право> <true/false>");
            return true;
        }

        Player player = (Player) sender;
        String perm = args[0];
        boolean value = Boolean.parseBoolean(args[1]);

        luckPerms.getUserManager()
            .loadUser(player.getUniqueId())
            .thenAcceptAsync(user -> {
                user.data().add(Node.builder(perm).value(value).build());
                luckPerms.getUserManager().saveUser(user).join();
                sender.sendMessage("§aПраво §e" + perm + " §aустановлено в §e" + value + "§a.");
            })
            .exceptionally(ex -> {
                sender.sendMessage("§cОшибка: " + ex.getMessage());
                return null;
            });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> perms = new ArrayList<>();
            for (Permission p : Bukkit.getPluginManager().getPermissions()) {
                if (p.getName().toLowerCase().startsWith(prefix)) {
                    perms.add(p.getName());
                }
            }
            return perms;
        }
        if (args.length == 2) {
            String prefix = args[1].toLowerCase();
            return List.of("true", "false").stream()
                .filter(s -> s.startsWith(prefix))
                .collect(Collectors.toList());
        }
        return List.of();
    }
}
