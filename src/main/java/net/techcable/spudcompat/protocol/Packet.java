package net.techcable.spudcompat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import net.md_5.bungee.api.connection.Connection;
import net.techcable.spudcompat.ProtocolVersion;
import net.techcable.spudcompat.protocol.injector.RawPacket;

public interface Packet {
    public PacketType getType();

    public void write(ByteBuf buf, ProtocolVersion version, ProtocolState state, ProtocolDirection direction);

    public default RawPacket toRaw(ByteBufAllocator allocator, ProtocolVersion version, ProtocolState state, ProtocolDirection direction) {
        ByteBuf buf = allocator.buffer();
        write(buf, version, state, direction);
        RawPacket raw = RawPacket.fromBuffer(buf, state, direction);
        buf.release();
        assert buf.refCnt() != 0;
        assert raw.getType() == this.getType();
        return raw;
    }
}