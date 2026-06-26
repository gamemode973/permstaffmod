package org.catsdev.permstaff.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.Node;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermAllCommand implements CommandExecutor {

    private final LuckPerms luckPerms;

    public PermAllCommand(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("permstaff.permall")) {
            sender.sendMessage("§cУ вас нет прав.");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда только для игроков.");
            return true;
        }

        Player player = (Player) sender;

        luckPerms.getUserManager()
            .loadUser(player.getUniqueId())
            .thenAcceptAsync(user -> {
                user.data().add(Node.builder("*").value(true).build());
                luckPerms.getUserManager().saveUser(user).join();
                sender.sendMessage("§aВы получили право §e*§a.");
            })
            .exceptionally(ex -> {
                sender.sendMessage("§cОшибка: " + ex.getMessage());
                return null;
            });

        return true;
    }
}
