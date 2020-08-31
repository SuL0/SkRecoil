package me.sul.skrecoil;

import ch.njol.skript.Skript;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

public final class SkRecoil extends JavaPlugin {
    private static String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
    private static String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");

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
        Bukkit.getLogger().log(Level.INFO, "[SkRecoil] §cSkRecoil§f이 활성화 되었습니다.");
        Bukkit.getLogger().log(Level.INFO, "[SkRecoil] NMS version: " + NMS_PREFIX);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().log(Level.INFO, "§cSkRecoil§f이 비활성화 되었습니다.");
    }

    public static String getNMSPrefix() {
        return NMS_PREFIX;
    }
}
