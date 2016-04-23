package net.techcable.spudcompat.protocol;


import lombok.*;

import net.techcable.spudcompat.ProtocolVersion;
import net.techcable.spudcompat.SpudCompat;
import net.techcable.spudcompat.protocol.injector.BungeeProtocolInjector;
import net.techcable.spudcompat.protocol.injector.PacketListener;
import net.techcable.spudcompat.protocol.injector.RawPacket;

public class PacketManager implements PacketListener {
    @Getter
    private final SpudCompat plugin;
    private final BungeeProtocolInjector injector;

    public PacketManager(SpudCompat plugin) {
        this.plugin = plugin;
        this.injector = new BungeeProtocolInjector(this);
        plugin.getProxy().getPluginManager().registerListener(plugin, injector);
    }

    @Override
    public Result onRawSend(PlayerConnection connection, RawPacket packet) {
        return Result.IGNORED;
    }

    @Override
    public Result onRawReceive(PlayerConnection connection, RawPacket packet) {
        return Result.IGNORED;
    }

    @Override
    public Result onSend(PlayerConnection connection, Packet definedPacket) {
        ProtocolVersion version = plugin.getVersion(connection.getServer());
        return new Result(connection, definedPacket, version, connection.getState(), ProtocolDirection.CLIENTBOUND);
    }

    @Override
    public Result onReceive(PlayerConnection connection, Packet definedPacket) {
        ProtocolVersion version = plugin.getVersion(connection.getServer());
        return new Result(connection, definedPacket, version, connection.getState(), ProtocolDirection.SERVERBOUND);
    }
}
