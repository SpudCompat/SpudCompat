package net.techcable.spudcompat.protocol;

import io.netty.channel.Channel;

import net.md_5.bungee.api.connection.Server;
import net.techcable.spudcompat.ProtocolVersion;
import net.techcable.spudcompat.protocol.injector.RawPacket;

public interface PlayerConnection {

    public default boolean isSupportedVersion() {
        return ProtocolVersion.getById(getVersionId()) != null;
    }

    public default ProtocolVersion getVersion() {
        int id = getVersionId();
        ProtocolVersion version = ProtocolVersion.getById(id);
        if (version != null) {
            return version;
        } else {
            throw new IllegalStateException("Unsupported version: " + id);
        }
    }

    public ProtocolState getState();

    public int getVersionId();

    public void sendPacket(RawPacket rawPacket);

    public Channel getChannel();

    public Server getServer();
}
