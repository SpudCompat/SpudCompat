package net.techcable.spudcompat.protocol;


import lombok.*;

import net.md_5.bungee.protocol.DefinedPacket;
import net.techcable.spudcompat.SpudCompat;
import net.techcable.spudcompat.protocol.injector.BungeeProtocolInjector;
import net.techcable.spudcompat.protocol.injector.RawPacket;
import net.techcable.spudcompat.protocol.injector.RawPacketListener;

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
    public Result onSend(Connection connection, RawPacket packet) {
        return Result.IGNORED;
    }

    @Override
    public Result onReceive(Connection connection, RawPacket packet) {
        return Result.IGNORED;
    }

    @Override
    public Result onSend(Connection connection, DefinedPacket definedPacket) {
        return Result.IGNORED;
    }

    @Override
    public Result onReceive(Connection connection, DefinedPacket definedPacket) {
        return Result.IGNORED;
    }
}
