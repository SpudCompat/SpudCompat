package net.techcable.spudcompat.protocol.injector;

import lombok.*;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.netty.ChannelWrapper;

@RequiredArgsConstructor
public class UserConnectionPlayerConnection extends AbstractPlayerConnection {
    private final UserConnection connection;

    @Override
    public int getVersionId() {
        return connection.getPendingConnection().getVersion();
    }

    @Override
    public Server getServer() {
        return connection.getServer();
    }

    @Override
    protected ChannelWrapper getChannelWrapper() {
        return connection.getCh();
    }
}
