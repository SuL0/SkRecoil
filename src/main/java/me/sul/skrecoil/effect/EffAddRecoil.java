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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class EffAddRecoil extends Effect {
    private Expression<Player> player;
    private Expression<Number> pitch;
    private Expression<Number> yaw;

    static {
        Skript.registerEffect(EffAddRecoil.class, "add recoil %entity% with pitch %number% and yaw %number%");
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
        return getClass().toString();
    }

    @Override
    protected void execute(Event event) {
        addRecoil(Objects.requireNonNull(player.getSingle(event)), Objects.requireNonNull(yaw.getSingle(event)).doubleValue(), Objects.requireNonNull(pitch.getSingle(event)).doubleValue());
    }

    private void addRecoil(Player p, double addToYaw, double addToPitch) { // 가로, 세로
        double yaw = Math.toRadians(-p.getLocation().getYaw() - 90.0F + (-1)*addToYaw);
        double pitch = Math.toRadians(-p.getLocation().getPitch() + (-1)*addToPitch);
        if (Math.abs(pitch) > 180) pitch = (pitch >= 0) ? 180 : -180;

        double x = Math.cos(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch);
        double z = -Math.sin(yaw) * Math.cos(pitch);
        sendLookAtPacket(p, new Vector(x, y, z));
    }

    private void sendLookAtPacket(Player p, Vector vector) {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        final PacketContainer lookAt = pm.createPacket(PacketType.Play.Server.LOOK_AT, false);
        lookAt.getIntegers().write(0, 1);
        lookAt.getDoubles().write(0, p.getEyeLocation().getX() + vector.getX());
        lookAt.getDoubles().write(1, p.getEyeLocation().getY() + vector.getY());
        lookAt.getDoubles().write(2, p.getEyeLocation().getZ() + vector.getZ());
        lookAt.getBooleans().write(0, false);
        try {
            pm.sendServerPacket(p, lookAt);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
