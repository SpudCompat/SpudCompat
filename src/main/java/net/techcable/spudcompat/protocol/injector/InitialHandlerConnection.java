package net.techcable.spudcompat.protocol.injector;

import lombok.*;

import java.lang.reflect.Field;

import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.techcable.spudcompat.ProtocolVersion;

@RequiredArgsConstructor
public class InitialHandlerConnection extends AbstractConnection {
    @NonNull
    private final InitialHandler initialHandler;

    @Override
    public int getVersionId() {
        return initialHandler.getVersion();
    }

    private static final Field intialHandlerChannelWrapperField;

    static {
        try {
            intialHandlerChannelWrapperField = InitialHandler.class.getDeclaredField("ch");
            intialHandlerChannelWrapperField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new AssertionError("Couldn't find InitialHandler.ch", e);
        }
    }

    @Override
    protected ChannelWrapper getChannelWrapper() {
        try {
            return (ChannelWrapper) intialHandlerChannelWrapperField.get(initialHandler);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Couldn't get IntialHandler.ch with reflection", e);
        }
    }
}
