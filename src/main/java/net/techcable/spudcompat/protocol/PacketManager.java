package net.techcable.spudcompat.protocol;


import lombok.*;

import net.md_5.bungee.protocol.DefinedPacket;
import net.techcable.spudcompat.ProtocolVersion;
import net.techcable.spudcompat.SpudCompat;
import net.techcable.spudcompat.protocol.injector.BungeeProtocolInjector;
import net.techcable.spudcompat.protocol.injector.RawPacket;
import net.techcable.spudcompat.protocol.injector.RawPacketListener;
import net.techcable.spudcompat.utils.Either;

public class PacketManager implements RawPacketListener {
    @Getter
    private final SpudCompat plugin;
    private final BungeeProtocolInjector injector;

    public PacketManager(SpudCompat plugin) {
        this.plugin = plugin;
        this.injector = new BungeeProtocolInjector(this);
        plugin.getProxy().getPluginManager().registerListener(plugin, injector);
    }

    @Override
    public Result onSend(PlayerConnection connection, RawPacket packet) {
        return Result.IGNORED;
    }

    @Override
    public Result onReceive(PlayerConnection connection, RawPacket packet) {
        return Result.IGNORED;
    }

    @Override
    public Result onSend(PlayerConnection connection, DefinedPacket definedPacket) {
        ProtocolVersion version = plugin.getVersion(connection.getServer())
        return new Result(Either.ofRight(definedPacket), version);
    }

    @Override
    public Result onReceive(PlayerConnection connection, DefinedPacket definedPacket) {
        ProtocolVersion version = plugin.getVersion(connection.getServer())
        return new Result(Either.ofRight(definedPacket), version);
    }
}
