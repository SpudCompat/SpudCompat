package net.techcable.spudcompat.protocol.injector;

import java.lang.reflect.Field;

import io.netty.channel.Channel;

import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.Protocol;
import net.techcable.spudcompat.protocol.PlayerConnection;
import net.techcable.spudcompat.protocol.ProtocolState;

public abstract class AbstractPlayerConnection implements PlayerConnection {

    @Override
    public void sendPacket(RawPacket rawPacket) {
        getChannelWrapper().write(rawPacket.getAllData());
    }

    @Override
    public Channel getChannel() {
        return getChannelWrapper().getHandle();
    }

    private static final Field minecraftDecoderProtocolField;

    static {
        try {
            minecraftDecoderProtocolField = MinecraftDecoder.class.getDeclaredField("protocol");
            minecraftDecoderProtocolField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new AssertionError("Couldn't find protocol field", e);
        }
    }

    @Override
    public ProtocolState getState() {
        Protocol protocol;
        try {
            protocol = (Protocol) minecraftDecoderProtocolField.get(getChannel().pipeline().get(MinecraftDecoder.class));
        } catch (IllegalAccessException e) {
            throw new AssertionError("Couldn't access protocol field");
        }
        switch (protocol) {
            case GAME:
                return ProtocolState.LOGIN;
            case HANDSHAKE:
                return ProtocolState.HANDSHAKE;
            case LOGIN:
                return ProtocolState.LOGIN;
            case STATUS:
                return ProtocolState.STATUS;
            default:
                throw new AssertionError("Couldn't handle protocol: " + protocol);
        }
    }

    protected abstract ChannelWrapper getChannelWrapper();
}
