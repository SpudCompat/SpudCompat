package net.techcable.spudcompat.protocol;

import java.util.Arrays;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;

import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EncryptionResponse;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.Login;
import net.md_5.bungee.protocol.packet.LoginRequest;
import net.md_5.bungee.protocol.packet.LoginSuccess;
import net.md_5.bungee.protocol.packet.PingPacket;
import net.md_5.bungee.protocol.packet.PlayerListHeaderFooter;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.Respawn;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;
import net.md_5.bungee.protocol.packet.SetCompression;
import net.md_5.bungee.protocol.packet.StatusRequest;
import net.md_5.bungee.protocol.packet.StatusResponse;
import net.md_5.bungee.protocol.packet.TabCompleteRequest;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import net.md_5.bungee.protocol.packet.Team;
import net.md_5.bungee.protocol.packet.Title;
import net.techcable.spudcompat.ProtocolVersion;
import net.techcable.spudcompat.utils.collect.ImmutableTables;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static net.techcable.spudcompat.protocol.ProtocolDirection.CLIENTBOUND;
import static net.techcable.spudcompat.protocol.ProtocolDirection.SERVERBOUND;
import static net.techcable.spudcompat.protocol.ProtocolState.*;


public enum PacketType {
    //
    // Handshake
    //

    HANDSHAKE(SERVERBOUND, ProtocolState.HANDSHAKE, 0x00),

    //
    // Play
    //

    // Dual-Way
    KEEP_ALIVE(PLAY, ImmutableMap.of(CLIENTBOUND, 0x00, SERVERBOUND, 0x00)),
    CHAT(PLAY, ImmutableMap.of(CLIENTBOUND, 0x02, SERVERBOUND, 0x01)),
    PLUGIN_MESSAGE(PLAY, ImmutableMap.of(CLIENTBOUND, 0x3F, SERVERBOUND, 0x17)),

    // Clientbound
    JOIN_GAME(CLIENTBOUND, PLAY, 0x01),
    RESPAWN(CLIENTBOUND, PLAY, 0x07),
    PLAYER_LIST_ITEM(CLIENTBOUND, PLAY, 0x38),
    TAB_COMPLETE_RESPONSE(CLIENTBOUND, PLAY, 0x3A),
    SCOREBOARD_OBJECTIVE(CLIENTBOUND, PLAY, 0x3B),
    SCOREBOARD_SCORE(CLIENTBOUND, PLAY, 0x3C),
    SCOREBOARD_DISPLAY(CLIENTBOUND, PLAY, 0x3D),
    SCOREBOARD_TEAM(CLIENTBOUND, PLAY, 0x3E),
    TITLE(CLIENTBOUND, PLAY, 0x45),
    PLAYER_LIST_HEADER_FOOTER(CLIENTBOUND, PLAY, 0x47),

    // Serverbound
    TAB_COMPLETE_REQUEST(SERVERBOUND, PLAY, 0x14),
    CLIENT_SETTINGS(SERVERBOUND, PLAY, 0x15),

    //
    // Status
    //

    // Dual-Way

    PING(STATUS, ImmutableMap.of(CLIENTBOUND, 0x01, SERVERBOUND, 0x01)),


    // Clientbound
    STATUS_RESPONSE(CLIENTBOUND, STATUS, 0x00),

    // Serverbound
    STATUS_REQUEST(SERVERBOUND, STATUS, 0x00),

    //
    // Login
    //

    // Clientbound
    ENCRYPTION_REQUEST(CLIENTBOUND, LOGIN, 0x01),
    LOGIN_SUCCESS(CLIENTBOUND, LOGIN, 0x02),
    SET_COMPRESSION(CLIENTBOUND, LOGIN, 0x03),

    // Serverbound
    LOGIN_REQUEST(SERVERBOUND, LOGIN, 0x00),
    ENCRYPTION_RESPONSE(SERVERBOUND, LOGIN, 0x01),

    //
    // Multi-State
    //

    KICK(ImmutableTables.of(CLIENTBOUND, PLAY, 0x40, CLIENTBOUND, LOGIN, 0x00));

    private PacketType(ProtocolDirection direction, ProtocolState state, int id) {
        this(ImmutableTable.of(checkNotNull(direction, "Null state"), checkNotNull(state, "Null direction"), id));
    }

    private PacketType(ProtocolState state, ImmutableMap<ProtocolDirection, Integer> map) {
        this(ImmutableTables.ofMapWithColumn(map, checkNotNull(state, "Null state")));
    }

    private PacketType(ImmutableTable<ProtocolDirection, ProtocolState, Integer> table) {
        checkNotNull(table, "Null table");
        checkArgument(!table.isEmpty(), "Empty table");
        this.ids = new int[ProtocolDirection.values().length][ProtocolState.values().length];
        for (int directionOrdinal = 0; directionOrdinal < ProtocolDirection.values().length; directionOrdinal++) {
            ProtocolDirection direction = ProtocolDirection.values()[directionOrdinal];
            for (int stateOrdinal = 0; stateOrdinal < ProtocolState.values().length; stateOrdinal++) {
                ProtocolState state = ProtocolState.values()[stateOrdinal];
                Integer id = table.get(state, direction);
                if (id != null) {
                    ids[directionOrdinal][stateOrdinal] = id;
                } else {
                    ids[directionOrdinal][stateOrdinal] = -1;
                }
            }
        }
    }

    private final int ids[][];

    public int getId(ProtocolDirection direction, ProtocolState state) {
        checkNotNull(direction, "Null direction");
        checkNotNull(state, "Null state");
        int id = ids[direction.ordinal()][state.ordinal()];
        if (id < 0) {
            throw new IllegalArgumentException("No id for " + name() + " with state " + state + " and direction " + direction);
        } else {
            return id;
        }
    }

    private static final PacketType[][][] BY_ID = new PacketType[ProtocolDirection.values().length][ProtocolState.values().length][];

    static {
        for (int directionOrdinal = 0; directionOrdinal < ProtocolDirection.values().length; directionOrdinal++) {
            ProtocolDirection direction = ProtocolDirection.values()[directionOrdinal];
            for (int stateOrdinal = 0; stateOrdinal < ProtocolState.values().length; stateOrdinal++) {
                ProtocolState state = ProtocolState.values()[stateOrdinal];
                PacketType[] byIdInner = new PacketType[0];
                for (PacketType packetType : values()) {
                    int id = packetType.getId(direction, state);
                    if (id < byIdInner.length) byIdInner = Arrays.copyOf(byIdInner, id + 1);
                    if (byIdInner[id] != null)
                        throw new AssertionError("Can't register " + packetType + " because " + byIdInner[id] + " is already registered with the id " + id + ".");
                    byIdInner[id] = packetType;
                }
                BY_ID[directionOrdinal][stateOrdinal] = byIdInner;
            }
        }
    }

    public static PacketType getById(int id, ProtocolState state, ProtocolDirection direction) {
        checkNotNull(state, "Null state");
        checkNotNull(direction, "Null direction");
        PacketType[] byIdInner = BY_ID[direction.ordinal()][state.ordinal()];
        if (id < 0) {
            throw new IllegalArgumentException("Negative id: " + id);
        } else if (id < byIdInner.length) {
            return byIdInner[id];
        } else {
            return null;
        }
    }

    private static final ImmutableMap<Class<? extends DefinedPacket>, PacketType> BY_BUNGEE = ImmutableMap.<Class<? extends DefinedPacket>, PacketType>builder()
            .put(Chat.class, CHAT)
            .put(ClientSettings.class, CLIENT_SETTINGS)
            .put(EncryptionRequest.class, ENCRYPTION_REQUEST)
            .put(EncryptionResponse.class, ENCRYPTION_RESPONSE)
            .put(Handshake.class, HANDSHAKE)
            .put(KeepAlive.class, KEEP_ALIVE)
            .put(Kick.class, KICK)
            .put(Login.class, JOIN_GAME)
            .put(LoginRequest.class, LOGIN_REQUEST)
            .put(LoginSuccess.class, LOGIN_SUCCESS)
            .put(PingPacket.class, PING)
            .put(PlayerListHeaderFooter.class, PLAYER_LIST_HEADER_FOOTER)
            .put(PlayerListItem.class, PLAYER_LIST_ITEM)
            .put(PluginMessage.class, PLUGIN_MESSAGE)
            .put(Respawn.class, RESPAWN)
            .put(ScoreboardDisplay.class, SCOREBOARD_DISPLAY)
            .put(ScoreboardObjective.class, SCOREBOARD_OBJECTIVE)
            .put(ScoreboardScore.class, SCOREBOARD_SCORE)
            .put(SetCompression.class, SET_COMPRESSION)
            .put(StatusRequest.class, STATUS_REQUEST)
            .put(StatusResponse.class, STATUS_RESPONSE)
            .put(TabCompleteRequest.class, TAB_COMPLETE_REQUEST)
            .put(TabCompleteResponse.class, TAB_COMPLETE_RESPONSE)
            .put(Team.class, SCOREBOARD_TEAM)
            .put(Title.class, TITLE)
            .build();

    public static PacketType fromBungee(Class<? extends DefinedPacket> bungeeType) {
        checkNotNull(bungeeType, "Null packet type");
        PacketType type = BY_BUNGEE.get(bungeeType);
        if (type == null) throw new IllegalArgumentException("Unknown packet type: " + bungeeType);
        return type;
    }
}
