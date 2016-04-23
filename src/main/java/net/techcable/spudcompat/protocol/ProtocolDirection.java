package net.techcable.spudcompat.protocol;

import lombok.*;

import com.google.common.base.Preconditions;

import net.md_5.bungee.protocol.ProtocolConstants;

import static com.google.common.base.Preconditions.checkNotNull;

public enum ProtocolDirection {
    CLIENTBOUND(ProtocolConstants.Direction.TO_CLIENT),
    SERVERBOUND(ProtocolConstants.Direction.TO_SERVER);

    private ProtocolDirection(ProtocolConstants.Direction bungee) {
        this.bungee = checkNotNull(bungee, "Null bungee direction");
    }

    private final ProtocolConstants.Direction bungee;

    public ProtocolConstants.Direction asBungee() {
        return bungee;
    }
}
