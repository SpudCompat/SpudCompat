package net.techcable.spudcompat.protocol.injector;

import lombok.*;

import io.netty.buffer.ByteBuf;

import net.md_5.bungee.protocol.DefinedPacket;
import net.techcable.spudcompat.ProtocolVersion;
import net.techcable.spudcompat.protocol.Packet;
import net.techcable.spudcompat.protocol.PacketType;
import net.techcable.spudcompat.protocol.ProtocolDirection;
import net.techcable.spudcompat.protocol.ProtocolState;

@RequiredArgsConstructor
public class BungeePacket implements Packet {
    private final DefinedPacket handle;

    @Override
    public PacketType getType() {
        return PacketType.fromBungee(handle.getClass());
    }

    @Override
    public void write(ByteBuf buf, ProtocolVersion version, ProtocolState state, ProtocolDirection direction) {
        int packetId = getType().getId(direction, state);
        DefinedPacket.writeVarInt(packetId, buf);
        handle.write(buf, direction.asBungee(), version.getId());
    }
}
