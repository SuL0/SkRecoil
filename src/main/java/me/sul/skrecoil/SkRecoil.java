package me.sul.skrecoil;

import ch.njol.skript.Skript;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class SkRecoil extends JavaPlugin {

    @Override
    public void onEnable() {
        Plugin skriptPlugin = Bukkit.getServer().getPluginManager().getPlugin("Skript");
        if (skriptPlugin != null) {
            if (Skript.isAcceptRegistrations()) {
                try {
                    Skript.registerAddon(this).loadClasses("me.sul.skrecoil", "effect");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDisable() {

    }
}
