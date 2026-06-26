package org.catsdev.permstaff.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.stream.Collectors;

public class PslelfCommand implements CommandExecutor, TabCompleter {

    private final LuckPerms luckPerms;

    public PslelfCommand(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("permstaff.pslelf")) {
            sender.sendMessage("§cУ вас нет прав.");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда только для игроков.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage("§cИспользование: /pslelf <группа>");
            return true;
        }

        Player player = (Player) sender;
        String groupName = args[0];

        luckPerms.getUserManager()
            .loadUser(player.getUniqueId())
            .thenAcceptAsync(user -> {
                user.data().add(InheritanceNode.builder(groupName).build());
                luckPerms.getUserManager().saveUser(user).join();
                sender.sendMessage("§aВы добавлены в группу §e" + groupName + "§a.");
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
            return luckPerms.getGroupManager().getLoadedGroups().stream()
                .map(Group::getName)
                .filter(name -> name.toLowerCase().startsWith(prefix))
                .collect(Collectors.toList());
        }
        return List.of();
    }
}
