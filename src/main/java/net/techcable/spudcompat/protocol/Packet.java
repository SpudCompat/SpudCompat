package net.techcable.spudcompat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.md_5.bungee.protocol.DefinedPacket;
import net.techcable.spudcompat.ProtocolVersion;
import net.techcable.spudcompat.protocol.injector.RawPacket;

public interface Packet {
    public PacketType getType();

    public void write(ByteBuf buf, ProtocolVersion version, ProtocolState state, ProtocolDirection direction);

    public default RawPacket toRaw(ProtocolVersion version, ProtocolState state, ProtocolDirection direction) {
        ByteBuf buf = Unpooled.buffer();
        write(buf, version, state, direction);
        RawPacket raw = RawPacket.fromBuffer(buf, state, direction);
        assert raw.getType() == this.getType();
        return raw;
    }
}