package net.techcable.spudcompat.protocol.injector;

import lombok.*;

import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.techcable.spudcompat.protocol.Connection;
import net.techcable.spudcompat.protocol.PacketType;
import net.techcable.spudcompat.protocol.ProtocolDirection;
import net.techcable.spudcompat.protocol.ProtocolState;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RawPacket {
    private final ByteBuf allData;
    private final ByteBuf packetData;
    private final int id;
    private final ProtocolState protocolState;
    private final ProtocolDirection direction;
    private final PacketType type = PacketType.get(id, protocolState, direction);

    public boolean isKnownType() {
        return getType() != null;
    }

    public ByteBuf getAllData() {
        return allData;
    }

    public static RawPacket fromWrapper(PacketWrapper wrapper, ProtocolState protocolState, ProtocolDirection direction) {
        return fromBuffer(Preconditions.checkNotNull(wrapper, "Null wrapper").buf, protocolState, direction);
    }

    public static RawPacket fromBuffer(ByteBuf buf, ProtocolState protocolState, ProtocolDirection direction) {
        Preconditions.checkNotNull(buf, "Null buffer");
        Preconditions.checkNotNull(protocolState, "Null state");
        Preconditions.checkNotNull(direction, "Null direction");
        buf = Unpooled.unmodifiableBuffer(buf.duplicate()); // Read-only
        ByteBuf allData = buf.duplicate();
        int id = DefinedPacket.readVarInt(buf);
        return new RawPacket(allData, buf.slice(), id, protocolState, direction);
    }

}