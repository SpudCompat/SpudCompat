package net.techcable.spudcompat.protocol.injector;

import lombok.*;

import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.techcable.spudcompat.protocol.PacketType;
import net.techcable.spudcompat.protocol.ProtocolDirection;
import net.techcable.spudcompat.protocol.ProtocolState;

import static com.google.common.base.Preconditions.*;

@Getter
public class RawPacket {
    private final ByteBuf allData;
    private final ByteBuf packetData;
    private final int id;
    private final ProtocolState protocolState;
    private final ProtocolDirection direction;
    private final PacketType type;

    public RawPacket(ByteBuf allData, ByteBuf packetData, int id, ProtocolState protocolState, ProtocolDirection direction) {
        this.allData = allData;
        this.packetData = packetData;
        this.id = id;
        this.protocolState = checkNotNull(protocolState, "Null state");
        this.direction = checkNotNull(direction, "Null direction");
        this.type = PacketType.getById(id, protocolState, direction);
    }

    public boolean isKnownType() {
        return getType() != null;
    }

    public ByteBuf getAllData() {
        return allData;
    }

    public static RawPacket fromWrapper(PacketWrapper wrapper, ProtocolState protocolState, ProtocolDirection direction) {
        return fromBuffer(checkNotNull(wrapper, "Null wrapper").buf, protocolState, direction);
    }

    public static RawPacket fromBuffer(ByteBuf buf, ProtocolState protocolState, ProtocolDirection direction) {
        checkNotNull(buf, "Null buffer");
        checkNotNull(protocolState, "Null state");
        checkNotNull(direction, "Null direction");
        buf = Unpooled.unmodifiableBuffer(buf.duplicate()); // Read-only
        ByteBuf allData = buf.duplicate();
        int id = DefinedPacket.readVarInt(buf);
        return new RawPacket(allData, buf.slice(), id, protocolState, direction);
    }

}