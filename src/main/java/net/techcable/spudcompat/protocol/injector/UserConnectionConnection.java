package net.techcable.spudcompat.protocol.injector;

import lombok.*;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.netty.ChannelWrapper;
import net.techcable.spudcompat.ProtocolVersion;
import net.techcable.spudcompat.protocol.ProtocolState;

@RequiredArgsConstructor
public class UserConnectionConnection extends AbstractConnection {
    private final UserConnection connection;

    @Override
    public int getVersionId() {
        return connection.getPendingConnection().getVersion();
    }

    @Override
    protected ChannelWrapper getChannelWrapper() {
        return connection.getCh();
    }
}
