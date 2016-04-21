package net.techcable.spudcompat.protocol.injector;

import lombok.*;

import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.techcable.spudcompat.protocol.Connection;
import net.techcable.spudcompat.protocol.ProtocolDirection;

@RequiredArgsConstructor
public class BungeeProtocolInjector implements Listener {
    private final RawPacketListener listener;

    public static final String SPUD_INCOMING_TRANSFORMER = "spud-incoming-transformer";
    public static final String SPUD_OUTGOING_TRANSFORMER = "spud-outgoing-transformer";

    @EventHandler
    public void onHandshake(PlayerHandshakeEvent event) {
        Connection connection = wrap(event.getConnection());
        if (!connection.isSupportedVersion()) return;
        inject(connection);
    }

    public void inject(Connection connection) {
        Preconditions.checkNotNull(connection, "Null connection");
        Preconditions.checkArgument(connection.isSupportedVersion(), "Unsupported version %s", connection.getVersionId());
        Channel channel = connection.getChannel();
        channel.pipeline().addAfter(PipelineUtils.PACKET_DECODER, SPUD_INCOMING_TRANSFORMER, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                PacketWrapper wrapper = (PacketWrapper) msg;
                RawPacketListener.Result result;
                if (wrapper.packet != null) {
                    result = listener.onReceive(connection, wrapper.packet);
                } else {
                    RawPacket rawPacket = RawPacket.fromBuffer(wrapper.buf, connection.getState(), ProtocolDirection.SERVERBOUND);
                    result = listener.onReceive(connection, rawPacket);
                }
                Preconditions.checkNotNull(result, "Null result");
                if (result.isIgnored()) {
                    ctx.writeAndFlush(wrapper, ctx.voidPromise());
                } else if (!result.isCanceled()) {
                    PacketWrapper toSend;
                    if (result.getResult().isLeft()) {
                        RawPacket rawPacket = result.getResult().getLeft();
                        toSend = new PacketWrapper(null, rawPacket.getAllData());
                    } else {
                        toSend = new PacketWrapper(result.getResult().getRight(), null);
                    }
                    ctx.writeAndFlush(toSend, ctx.voidPromise());
                }
            }
        });
        channel.pipeline().addBefore(PipelineUtils.PACKET_ENCODER, SPUD_OUTGOING_TRANSFORMER, new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                RawPacketListener.Result result;
                if (msg instanceof ByteBuf) {
                    RawPacket rawPacket = RawPacket.fromBuffer((ByteBuf) msg, connection.getState(), ProtocolDirection.CLIENTBOUND);
                    result = listener.onSend(connection, rawPacket);
                } else if (msg instanceof DefinedPacket) {
                    result = listener.onSend(connection, (DefinedPacket) msg);
                } else {
                    throw new UnsupportedOperationException("Unable to handle: " + msg.getClass());
                }
                Preconditions.checkNotNull(result, "Null result");
                if (result.isIgnored()) {
                    ctx.writeAndFlush(msg, ctx.voidPromise());
                } else if (!result.isCanceled()) {
                    PacketWrapper toSend;
                    if (result.getResult().isLeft()) {
                        RawPacket rawPacket = result.getResult().getLeft();
                        toSend = new PacketWrapper(null, rawPacket.getAllData());
                    } else {
                        toSend = new PacketWrapper(result.getResult().getRight(), null);
                    }
                    ctx.writeAndFlush(toSend, ctx.voidPromise());
                }
            }
        });
    }


    public static Connection wrap(net.md_5.bungee.api.connection.Connection connection) {
        if (connection instanceof InitialHandler) {
            return new InitialHandlerConnection((InitialHandler) connection);
        } else if (connection instanceof UserConnection) {
            return new UserConnectionConnection((UserConnection) connection);
        } else {
            throw new UnsupportedOperationException("Unable to wrap connection type: " + connection.getClass().getName());
        }
    }
}
