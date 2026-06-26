package org.catsdev.permstaff;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.catsdev.permstaff.commands.*;

public class PermStaff extends JavaPlugin {

    @Override
    public void onEnable() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) {
            getLogger().severe("LuckPerms not found! Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        LuckPerms luckPerms = provider.getProvider();

        PslCommand psl = new PslCommand(luckPerms);
        getCommand("psl").setExecutor(psl);
        getCommand("psl").setTabCompleter(psl);

        PslelfCommand pslelf = new PslelfCommand(luckPerms);
        getCommand("pslelf").setExecutor(pslelf);
        getCommand("pslelf").setTabCompleter(pslelf);

        getCommand("permall").setExecutor(new PermAllCommand(luckPerms));
        getCommand("depermall").setExecutor(new DepermAllCommand(luckPerms));

        SetpermCommand setperm = new SetpermCommand(luckPerms);
        getCommand("setperm").setExecutor(setperm);
        getCommand("setperm").setTabCompleter(setperm);

        SetpermselfCommand setpermself = new SetpermselfCommand(luckPerms);
        getCommand("setpermself").setExecutor(setpermself);
        getCommand("setpermself").setTabCompleter(setpermself);

        UnsetpermCommand unsetperm = new UnsetpermCommand(luckPerms);
        getCommand("unsetperm").setExecutor(unsetperm);
        getCommand("unsetperm").setTabCompleter(unsetperm);

        UnsetpermselfCommand unsetpermself = new UnsetpermselfCommand(luckPerms);
        getCommand("unsetpermself").setExecutor(unsetpermself);
        getCommand("unsetpermself").setTabCompleter(unsetpermself);

        getLogger().info("PermStaff enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PermStaff disabled!");
    }
}
