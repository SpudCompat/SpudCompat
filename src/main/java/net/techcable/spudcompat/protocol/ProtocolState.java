package net.techcable.spudcompat.protocol;

import lombok.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import com.google.common.base.Preconditions;

import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;
import net.techcable.spudcompat.ProtocolVersion;

import static com.google.common.base.Preconditions.checkNotNull;

@RequiredArgsConstructor
public enum ProtocolState {
    HANDSHAKE(Protocol.HANDSHAKE),
    LOGIN(Protocol.LOGIN),
    PLAY(Protocol.GAME),
    STATUS(Protocol.STATUS);

    private final Protocol bungee;

    public Protocol toBungee() {
        return bungee;
    }

    private static final MethodHandle DIRECTION_DATA_GET_ID_METHOD;
    static {
        try {
            Method m = Protocol.DirectionData.class.getDeclaredMethod("getId", Class.class, int.class);
            m.setAccessible(true);
            DIRECTION_DATA_GET_ID_METHOD = MethodHandles.lookup().unreflect(m);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new AssertionError("Couldn't find DirectionData.getId", e);
        }

    }

    @SneakyThrows
    public int getId(Class<? extends DefinedPacket> packet, ProtocolVersion version, ProtocolDirection direction) {
        checkNotNull(packet, "Null packet");
        checkNotNull(version, "Null version");
        checkNotNull(direction, "Null direction");
        final Protocol.DirectionData data;
        switch (direction) {
            case SERVERBOUND:
                data = toBungee().TO_CLIENT;
                break;
            case CLIENTBOUND:
                data = toBungee().TO_SERVER;
                break;
            default:
                throw new AssertionError("Couldn't find direction data for: " + direction);
        }
        return (int) DIRECTION_DATA_GET_ID_METHOD.invokeExact(data, packet, version.getId());
    }
}
