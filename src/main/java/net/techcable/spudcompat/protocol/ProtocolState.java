package net.techcable.spudcompat.protocol;

import lombok.*;

import net.md_5.bungee.protocol.Protocol;

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
}
