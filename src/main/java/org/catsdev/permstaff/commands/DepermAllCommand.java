package org.catsdev.permstaff.commands;

import net.luckperms.api.LuckPerms;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DepermAllCommand implements CommandExecutor {

    private final LuckPerms luckPerms;

    public DepermAllCommand(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("permstaff.depermall")) {
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
                user.data().clear(n -> n.getKey().equals("*"));
                luckPerms.getUserManager().saveUser(user).join();
                sender.sendMessage("§aПраво §e* §aудалено.");
            })
            .exceptionally(ex -> {
                sender.sendMessage("§cОшибка: " + ex.getMessage());
                return null;
            });

        return true;
    }
}
