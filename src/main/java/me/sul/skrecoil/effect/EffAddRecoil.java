package me.sul.skrecoil.effect;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import me.sul.skrecoil.ReflectionUtil;
import me.sul.skrecoil.SkRecoil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.logging.Level;

public class EffAddRecoil extends Effect {
    private final ProtocolManager pm = ProtocolLibrary.getProtocolManager();
    private Class<?> anchorClazz;
    private Expression<Player> player;
    private Expression<Number> pitch;
    private Expression<Number> yaw;

    static {
        Skript.registerEffect(EffAddRecoil.class, "add recoil %player% with pitch %number% and yaw %number%");
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parse) {
        this.player = (Expression<Player>) exprs[0];
        this.pitch = (Expression<Number>) exprs[1];
        this.yaw = (Expression<Number>) exprs[2];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "add recoil " + player.toString(event, b) + " with pitch " + pitch.toString(event, b) + " and yaw " + yaw.toString(event, b);
    }

    @Override
    protected void execute(Event event) {
        addRecoil(Objects.requireNonNull(player.getSingle(event)), Objects.requireNonNull(yaw.getSingle(event)).doubleValue(), Objects.requireNonNull(pitch.getSingle(event)).doubleValue());
    }

    private void addRecoil(Player player, double addToYaw, double addToPitch) { // 가로, 세로
        Vector vector = convert2Vector(player.getLocation().getYaw(), player.getLocation().getPitch(), addToYaw, addToPitch);
        sendLookAtPacket(player, vector);
    }

    private Vector convert2Vector(double origYaw, double origPitch, double addToYaw, double addToPitch) {
        double yaw = -origYaw - 90.0F + (-1)*addToYaw;
        double pitch = -origPitch + (-1)*addToPitch;
        yaw = yaw / 180.0 * Math.PI;
        pitch = pitch / 180.0 * Math.PI;
        if (Math.abs(pitch) > Math.PI / 2) {
            pitch = (pitch >= 0) ? Math.PI / 2 : -1 * Math.PI / 2;
            pitch -= Math.PI * 0.1/180;
        }

        double x = Math.cos(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch);
        double z = -Math.sin(yaw) * Math.cos(pitch);
        return new Vector(x, y, z);
    }

    private void sendLookAtPacket(Player player, Vector vector) {
        if (vector.getX() == 0 && vector.getY() == 0 && vector.getZ() == 0) return;
        PacketContainer lookAtPacket = pm.createPacket(PacketType.Play.Server.LOOK_AT, false);
        if (anchorClazz == null) {
            try {
                anchorClazz = Class.forName(SkRecoil.getNMSPrefix() + ".ArgumentAnchor$Anchor");
            } catch (ClassNotFoundException e) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to get " + SkRecoil.getNMSPrefix() + ".ArgumentAnchor$Anchor Class");
                e.printStackTrace();
            }
        }
        Object enumArgumentAnchor_EYES = ReflectionUtil.getEnumConstant(anchorClazz, "EYES");
        lookAtPacket.getModifier().write(4, enumArgumentAnchor_EYES);
        lookAtPacket.getDoubles().write(0, player.getEyeLocation().getX() + vector.getX());
        lookAtPacket.getDoubles().write(1, player.getEyeLocation().getY() + vector.getY());
        lookAtPacket.getDoubles().write(2, player.getEyeLocation().getZ() + vector.getZ());
        lookAtPacket.getBooleans().write(0, false);
        try {
            pm.sendServerPacket(player, lookAtPacket);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
